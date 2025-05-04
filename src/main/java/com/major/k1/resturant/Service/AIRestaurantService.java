package com.major.k1.resturant.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.k1.resturant.DTO.RestaurantDTO;
import com.major.k1.resturant.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIRestaurantService {
    private final RestaurantRepository restaurantRepo;
    private final RestTemplate restTemplate;

    public String getRestaurantRecommendation(String question) {
        List<RestaurantDTO> restaurants = getAllRestaurants(); // Reuse your DTO method

        String restaurantData = restaurants.stream()
                .map(r -> String.format("""
                [RESTAURANT]
                Name: %s
                Location: %s
                Open Hours: %s
                Featured Dishes: %s
                Menu: %s
                Available Slots: %s
                ----------------------""",
                        r.getName(),
                        r.getPlace(),
                        r.getOpenTime(),
                        String.join(", ", r.getBestDishes()),
                        r.getMenu().stream()
                                .map(m -> m.getItem() + " (₹" + m.getPrice() + ")")
                                .collect(Collectors.joining(", ")),
                        r.getSlotTimes().stream()
                                .filter(s -> s.isAvailable())
                                .map(s -> s.getTime())
                                .collect(Collectors.joining(", "))
                ))
                .collect(Collectors.joining("\n"));

        String prompt = String.format("""
            Answer this restaurant query based ONLY on:
            %s
            
            Question: %s
            
            Rules:
            1. Be specific about dishes and prices
            2. Mention availability if relevant
            3. Keep response under 100 words""",
                restaurantData, question);

        return callOllamaAPI(prompt);
    }

    private String callOllamaAPI(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "model", "llama3",
                    "prompt", prompt,
                    "stream", false
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:11434/api/generate",
                    request,
                    String.class
            );

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.getBody()).path("response").asText();
        } catch (Exception e) {
            return "I couldn't process your request. Please try again.";
        }
    }
@Autowired
private RestaurantService restaurantService;
    // Reuse your existing DTO conversion method
    private List<RestaurantDTO> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }
}
