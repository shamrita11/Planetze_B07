package com.example.planetze;

import java.util.ArrayList;
import java.util.Map;

public class FoodCalculator {

    public double calculateFoodEmissions(ArrayList<Map<String, String>> responses) {
        double totalEmissions = 0.0;

        // Default CO2 emissions for diet types (kg/year)
        Map<String, Double> dietEmissions = Map.of(
                "Vegetarian", 1000.0,
                "Vegan", 500.0,
                "Pescatarian (fish/seafood)", 1500.0,
                "Meat-based (eat all types of animal products)", 0.0 // Meat-based calculates separately
        );

        // CO2 emissions for meat consumption (kg/year)
        Map<String, Double> beefEmissions = Map.of(
                "Daily", 2500.0,
                "Frequently (3-5 times/week)", 1900.0,
                "Occasionally (1-2 times/week)", 1300.0,
                "Never", 0.0
        );

        Map<String, Double> porkEmissions = Map.of(
                "Daily", 1450.0,
                "Frequently (3-5 times/week)", 860.0,
                "Occasionally (1-2 times/week)", 450.0,
                "Never", 0.0
        );

        Map<String, Double> chickenEmissions = Map.of(
                "Daily", 950.0,
                "Frequently (3-5 times/week)", 600.0,
                "Occasionally (1-2 times/week)", 200.0,
                "Never", 0.0
        );

        Map<String, Double> fishEmissions = Map.of(
                "Daily", 800.0,
                "Frequently (3-5 times/week)", 500.0,
                "Occasionally (1-2 times/week)", 150.0,
                "Never", 0.0
        );

        // CO2 emissions for food waste (kg/year)
        Map<String, Double> foodWasteEmissions = Map.of(
                "Never", 0.0,
                "Rarely", 23.4,
                "Occasionally", 70.2,
                "Frequently", 140.4
        );

        // Fetch responses using helper method
        String dietType = getResponseValue("q6", "a6", responses);
        String beefConsumption = getResponseValue("q6_1_beef", "a6_1_beef", responses);
        String porkConsumption = getResponseValue("q6_1_pork", "a6_1_pork", responses);
        String chickenConsumption = getResponseValue("q6_1_chicken", "a6_1_chicken", responses);
        String fishConsumption = getResponseValue("q6_1_fish", "a6_1_fish", responses);
        String foodWaste = getResponseValue("q7", "a7", responses);

        // Calculate diet emissions
        if (dietEmissions.containsKey(dietType)) {
            totalEmissions += dietEmissions.get(dietType);
        }

        // Only calculate meat emissions if diet type is "Meat-based"
        if ("Meat-based (eat all types of animal products)".equalsIgnoreCase(dietType)) {
            if (beefEmissions.containsKey(beefConsumption)) {
                totalEmissions += beefEmissions.get(beefConsumption);
            }

            if (porkEmissions.containsKey(porkConsumption)) {
                totalEmissions += porkEmissions.get(porkConsumption);
            }

            if (chickenEmissions.containsKey(chickenConsumption)) {
                totalEmissions += chickenEmissions.get(chickenConsumption);
            }

            if (fishEmissions.containsKey(fishConsumption)) {
                totalEmissions += fishEmissions.get(fishConsumption);
            }
        }

        // Add food waste emissions
        if (foodWasteEmissions.containsKey(foodWaste)) {
            totalEmissions += foodWasteEmissions.get(foodWaste);
        }

        return totalEmissions; // Return total food-related emissions in kg/year
    }

    /**
     * Helper method to fetch a response value by question and answer keys.
     */
    private String getResponseValue(String questionKey, String answerKey, ArrayList<Map<String, String>> responses) {
        for (Map<String, String> response : responses) {
            if (response.containsKey(questionKey)) {
                return response.getOrDefault(answerKey, "");
            }
        }
        return "";
    }
}

