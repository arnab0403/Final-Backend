package com.major.k1.resturant.Controller;

import com.major.k1.resturant.DTO.RestaurantDTO;
import com.major.k1.resturant.Repository.RestaurantRepository;
import com.major.k1.resturant.Service.AIRestaurantService;
import com.major.k1.resturant.Service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RestaurantAIController {
@Autowired
    private  RestaurantRepository restaurantRepo;
@Autowired
    private  AIRestaurantService aiService;
@Autowired
private RestaurantService restaurantService;
    // Existing endpoint for React
    @GetMapping("/restaurants")
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    // Modified AI endpoint to match React's expectation
    @GetMapping("/ask")
    public Map<String, String> askQuestion(@RequestParam String question) {
        String aiResponse = aiService.getRestaurantRecommendation(question);
        return Map.of("response", aiResponse);
    }
}