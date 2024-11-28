package com.example.planetze;

import java.util.ArrayList;
import java.util.Map;

public class TransportationCalculator {

    public double calculateTransportationEmissions(ArrayList<Map<String, String>> responses) {
        double totalEmissions = 0.0;

        // Default CO2 emission factors for cars (kg/km)
        Map<String, Double> carEmissionFactors = Map.of(
                "Gasoline", 0.24,
                "Diesel", 0.27,
                "Hybrid", 0.16,
                "Electric", 0.05
        );

        // Car distance ranges (in km)
        Map<String, Integer> carDistanceMap = Map.of(
                "Up to 5,000 km", 5000,
                "5,000–10,000 km", 10000,
                "10,000–15,000 km", 15000,
                "15,000–20,000 km", 20000,
                "20,000–25,000 km", 25000,
                "More than 25,000 km", 35000
        );

        // Public transport emissions table
        Map<String, Map<String, Double>> publicTransportEmissions = Map.of(
                "Occasionally", Map.of(
                        "Under 1 hour", 246.0,
                        "1-3 hours", 819.0,
                        "3-5 hours", 1638.0,
                        "5-10 hours", 3071.0,
                        "More than 10 hours", 4095.0
                ),
                "Frequently", Map.of(
                        "Under 1 hour", 573.0,
                        "1-3 hours", 1911.0,
                        "3-5 hours", 3822.0,
                        "5-10 hours", 7166.0,
                        "More than 10 hours", 9555.0
                ),
                "Always", Map.of(
                        "Under 1 hour", 573.0,
                        "1-3 hours", 1911.0,
                        "3-5 hours", 3822.0,
                        "5-10 hours", 7166.0,
                        "More than 10 hours", 9555.0
                )
        );

        // Flight emissions (kg/year)
        Map<String, Double> shortHaulFlights = Map.of(
                "None", 0.0,
                "1–2 flights", 225.0,
                "3–5 flights", 600.0,
                "6–10 flights", 1200.0,
                "More than 10 flights", 1800.0
        );

        Map<String, Double> longHaulFlights = Map.of(
                "None", 0.0,
                "1–2 flights", 825.0,
                "3–5 flights", 2200.0,
                "6–10 flights", 4400.0,
                "More than 10 flights", 6600.0
        );

        // Variables to store user responses for public transportation
        String transportFrequency = "";
        String transportTime = "";

        // Process responses
        for (Map<String, String> response : responses) {
            String question = response.getOrDefault("question", "");
            String answer = response.getOrDefault("answer", "");

            switch (question) {
                case "Do you own or regularly use a car?":
                    if (answer.equalsIgnoreCase("No")) {
                        // Skip car-related calculations if the user doesn't own a car
                        break;
                    }
                    break;

                case "What type of car do you drive?":
                    // Store car emission factor for later calculation
                    if (carEmissionFactors.containsKey(answer)) {
                        double emissionFactor = carEmissionFactors.get(answer);
                        totalEmissions += emissionFactor; // Add emission factor for distance calculation
                    }
                    break;

                case "How many kilometers/miles do you drive per year?":
                    // Calculate car emissions
                    if (carDistanceMap.containsKey(answer)) {
                        int distance = carDistanceMap.get(answer);
                        double emissionFactor = carEmissionFactors.getOrDefault(response.get("CarType"), 0.0);
                        totalEmissions += emissionFactor * distance;
                    }
                    break;

                case "How often do you use public transportation?":
                    // Store transport frequency
                    transportFrequency = answer;
                    break;

                case "How much time do you spend on public transport per week?":
                    // Store transport time
                    transportTime = answer;
                    break;

                case "How many short-haul flights have you taken in the past year?":
                    // Calculate short-haul flight emissions
                    if (shortHaulFlights.containsKey(answer)) {
                        totalEmissions += shortHaulFlights.get(answer);
                    }
                    break;

                case "How many long-haul flights have you taken in the past year?":
                    // Calculate long-haul flight emissions
                    if (longHaulFlights.containsKey(answer)) {
                        totalEmissions += longHaulFlights.get(answer);
                    }
                    break;

                default:
                    // Ignore unrelated questions
                    break;
            }
        }

        // Calculate public transport emissions based on frequency and time
        if (!transportFrequency.equalsIgnoreCase("Never") && publicTransportEmissions.containsKey(transportFrequency)) {
            if (publicTransportEmissions.get(transportFrequency).containsKey(transportTime)) {
                totalEmissions += publicTransportEmissions.get(transportFrequency).get(transportTime);
            }
        }

        return totalEmissions; // Return total transportation emissions in kg/year
    }
}
