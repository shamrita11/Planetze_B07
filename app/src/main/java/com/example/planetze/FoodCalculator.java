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
                "Pescatarian", 1500.0,
                "Meat-based", 0.0 // Will calculate based on meat consumption
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

        // Variables to store user responses
        String dietType = "";
        String beefConsumption = "";
        String porkConsumption = "";
        String chickenConsumption = "";
        String fishConsumption = "";
        String foodWaste = "";

        // Process responses
        for (Map<String, String> response : responses) {
            String question = response.getOrDefault("question", "");
            String answer = response.getOrDefault("answer", "");

            switch (question) {
                case "What best describes your diet?":
                    dietType = answer;
                    if (dietEmissions.containsKey(dietType)) {
                        totalEmissions += dietEmissions.get(dietType);
                    }
                    break;

                case "How often do you eat beef?":
                    beefConsumption = answer;
                    if (beefEmissions.containsKey(beefConsumption)) {
                        totalEmissions += beefEmissions.get(beefConsumption);
                    }
                    break;

                case "How often do you eat pork?":
                    porkConsumption = answer;
                    if (porkEmissions.containsKey(porkConsumption)) {
                        totalEmissions += porkEmissions.get(porkConsumption);
                    }
                    break;

                case "How often do you eat chicken?":
                    chickenConsumption = answer;
                    if (chickenEmissions.containsKey(chickenConsumption)) {
                        totalEmissions += chickenEmissions.get(chickenConsumption);
                    }
                    break;

                case "How often do you eat fish?":
                    fishConsumption = answer;
                    if (fishEmissions.containsKey(fishConsumption)) {
                        totalEmissions += fishEmissions.get(fishConsumption);
                    }
                    break;

                case "How often do you waste food or throw away uneaten leftovers?":
                    foodWaste = answer;
                    if (foodWasteEmissions.containsKey(foodWaste)) {
                        totalEmissions += foodWasteEmissions.get(foodWaste);
                    }
                    break;

                default:
                    // Ignore unrelated questions
                    break;
            }
        }

        return totalEmissions; // Return total food-related emissions in kg/year
    }
}
