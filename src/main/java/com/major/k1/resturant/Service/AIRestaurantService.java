package com.major.k1.resturant.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.k1.resturant.DTO.RestaurantDTO;
import com.major.k1.resturant.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIRestaurantService {
    private final RestaurantRepository restaurantRepo;
    private final RestTemplate restTemplate;
    private final RestaurantService restaurantService;

    // Varied greeting responses
    private static final String[] GREETINGS = {
            "Namaste! I'm your restaurant concierge. How may I assist you today?",
            "Hello there! Ready to help you find the perfect dining experience.",
            "Hi! I can recommend restaurants, check availability, and suggest menus.",
            "Welcome! Ask me about restaurants in your area or specific cuisines.",
            "Good day! I'm here to help with all your restaurant queries."
    };

    // Different help examples
    private static final String[] HELP_EXAMPLES = {
            "Try: 'Find available tables for 4 at 7pm'",
            "Example: 'Show me Chinese restaurants near me'",
            "Ask: 'What vegetarian options are available?'",
            "Query: 'Which places have live music tonight?'",
            "Try asking: 'Recommend a romantic dinner spot'"
    };

    // Time-based greetings
    private static final String[] MORNING_GREETINGS = {
            "Good morning! Ready to help you plan your meals today.",
            "Morning! What restaurant information would you like?",
            "Top of the day! How can I assist with your dining plans?"
    };

    private static final String[] EVENING_GREETINGS = {
            "Good evening! Looking for dinner recommendations?",
            "Evening! Need help finding a place to dine tonight?",
            "Hello this evening! Planning a night out?"
    };

    public String getRestaurantRecommendation(String question) {
        String normalizedQuestion = question.trim().toLowerCase();

        // Handle greetings
        if (isGreeting(normalizedQuestion)) {
            return generateTimeAwareGreeting();
        }

        // Handle help requests
        if (isHelpRequest(normalizedQuestion)) {
            return generateHelpResponse();
        }

        // Handle empty queries
        if (normalizedQuestion.isEmpty()) {
            return "I'd be happy to help! Could you tell me what restaurant information you're looking for?";
        }

        // Proceed with restaurant recommendation logic
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        boolean timeSpecificQuery = question.matches(".*\\d{1,2}:?\\d{0,2}\\s*[aApP][mM].*");

        String restaurantData = formatRestaurantData(restaurants, timeSpecificQuery);
        String prompt = buildPrompt(question, restaurantData, timeSpecificQuery);

        return callOllamaAPI(prompt);
    }

    private boolean isGreeting(String message) {
        return message.matches("^(hi|hello|hey|hlw|greetings|how are you|what's up|sup|good morning|good afternoon|good evening|yo|namaste).*");
    }

    private boolean isHelpRequest(String message) {
        return message.matches(".*(help|support|what can you do|assist|options|guide).*");
    }

    private String generateTimeAwareGreeting() {
        Random random = new Random();
        int hour = java.time.LocalTime.now().getHour();

        if (hour < 12) {
            return MORNING_GREETINGS[random.nextInt(MORNING_GREETINGS.length)] + " "
                    + HELP_EXAMPLES[random.nextInt(HELP_EXAMPLES.length)];
        } else if (hour > 17) {
            return EVENING_GREETINGS[random.nextInt(EVENING_GREETINGS.length)] + " "
                    + HELP_EXAMPLES[random.nextInt(HELP_EXAMPLES.length)];
        }
        return GREETINGS[random.nextInt(GREETINGS.length)] + " "
                + HELP_EXAMPLES[random.nextInt(HELP_EXAMPLES.length)];
    }

    private String generateHelpResponse() {
        Random random = new Random();
        return "I can help you with:\n"
                + "• Finding restaurants by cuisine, location or availability\n"
                + "• Checking seat availability at specific times\n"
                + "• Viewing menus with prices\n"
                + "• Getting personalized recommendations\n\n"
                + HELP_EXAMPLES[random.nextInt(HELP_EXAMPLES.length)];
    }

    private String formatRestaurantData(List<RestaurantDTO> restaurants, boolean timeSpecific) {
        return restaurants.stream()
                .map(r -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("""
                    [RESTAURANT]
                    Name: %s
                    Location: %s
                    Open Hours: %s
                    Description: %s
                    Featured Dishes: %s
                    Menu: %s
                    Total Seats: %d
                    """,
                            r.getName(),
                            r.getPlace(),
                            r.getOpenTime(),
                            r.getDescription(),
                            String.join(", ", r.getBestDishes()),
                            r.getMenu().stream()
                                    .map(m -> m.getItem() + " (₹" + m.getPrice() + ")")
                                    .collect(Collectors.joining(", ")),
                            r.getTotalSeats()));

                    if (timeSpecific) {
                        sb.append("Available Slots:\n");
                        r.getSlotTimes().stream()
                                .filter(s -> s.isAvailable())
                                .forEach(s -> sb.append(String.format(
                                        "  - %s: %d seats\n",
                                        s.getTime(),
                                        s.getAvailableSeats())));
                    } else {
                        sb.append("Available Times: ")
                                .append(r.getSlotTimes().stream()
                                        .filter(s -> s.isAvailable())
                                        .map(s -> s.getTime())
                                        .collect(Collectors.joining(", ")))
                                .append("\n");
                    }
                    sb.append("----------------------");
                    return sb.toString();
                })
                .collect(Collectors.joining("\n"));
    }

    private String buildPrompt(String question, String restaurantData, boolean timeSpecific) {
        return String.format("""
            ROLE: You are an expert restaurant concierge.
            CONTEXT: %s
            DATA:
            %s
            
            QUESTION: %s
            
            RESPONSE RULES:
            1. Be friendly and professional
            2. For time queries, include: "[Name] has [X] seats at [time]"
            3. Always show prices in ₹
            4. Keep responses under 120 words
            5. If unsure, suggest alternatives
            """,
                timeSpecific ? "User is asking about specific time availability" : "General restaurant query",
                restaurantData,
                question);
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

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response.getBody()).path("response").asText();
        } catch (Exception e) {
            return "Apologies, I'm having trouble accessing that information. Please try again later.";
        }
    }
}