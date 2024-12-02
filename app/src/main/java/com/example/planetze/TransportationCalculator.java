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
                "Up to 5,000 km (3,000 miles)", 5000,
                "5,000–10,000 km (3,000-6,000 miles)", 10000,
                "10,000–15,000 km (6,000-9,000 miles)", 15000,
                "15,000–20,000 km (9,000-12,000 miles)", 20000,
                "20,000–25,000 km (12,000-15,000 miles)", 25000,
                "More than 25,000 km (15,000 miles)", 35000
        );

        // Public transport emissions table
        Map<String, Map<String, Double>> publicTransportEmissions = Map.of(
                "Occasionally (1-2 times/week)", Map.of(
                        "Under 1 hour", 246.0,
                        "1-3 hours", 819.0,
                        "3-5 hours", 1638.0,
                        "5-10 hours", 3071.0,
                        "More than 10 hours", 4095.0
                ),
                "Frequently (3-4 times/week)", Map.of(
                        "Under 1 hour", 573.0,
                        "1-3 hours", 1911.0,
                        "3-5 hours", 3822.0,
                        "5-10 hours", 7166.0,
                        "More than 10 hours", 9555.0
                ),
                "Always (5+ times/week)", Map.of(
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

        // Fetch responses
        String carOwnership = getResponseValue("q1", "a1", responses);
        String carType = getResponseValue("q1_1", "a1_1", responses);
        String carDistance = getResponseValue("q1_2", "a1_2", responses);
        String transportFrequency = getResponseValue("q2", "a2", responses);
        String transportTime = getResponseValue("q3", "a3", responses);
        String shortHaulFlightCount = getResponseValue("q4", "a4", responses);
        String longHaulFlightCount = getResponseValue("q5", "a5", responses);

        // Calculate car emissions
        if ("Yes".equalsIgnoreCase(carOwnership)) {
            if (carEmissionFactors.containsKey(carType)) {
                if (carDistanceMap.containsKey(carDistance)) {
                    double emissionFactor = carEmissionFactors.get(carType);
                    int distance = carDistanceMap.get(carDistance);
                    totalEmissions += emissionFactor * distance;
                } else {
                    System.out.println("Car distance not found: " + carDistance);
                }
            } else {
                System.out.println("Car type not found: " + carType);
            }
        } else {
            System.out.println("User does not own a car.");
        }



        // Calculate public transport emissions
        if (publicTransportEmissions.containsKey(transportFrequency)) {
            if (publicTransportEmissions.get(transportFrequency).containsKey(transportTime)) {
                totalEmissions += publicTransportEmissions.get(transportFrequency).get(transportTime);
            } else {
                System.out.println("Transport time not found: " + transportTime);
            }
        } else {
            System.out.println("Transport frequency not found: " + transportFrequency);
        }




        // Calculate short-haul flight emissions
        if (shortHaulFlights.containsKey(shortHaulFlightCount)) {
            totalEmissions += shortHaulFlights.get(shortHaulFlightCount);
        } else {
            System.out.println("Short-haul flight data missing.");
        }

        // Calculate long-haul flight emissions
        if (longHaulFlights.containsKey(longHaulFlightCount)) {
            totalEmissions += longHaulFlights.get(longHaulFlightCount);
        } else {
            System.out.println("Long-haul flight data missing.");
        }

        System.out.println("Total Transportation Emissions: " + totalEmissions + " kg/year");
        return totalEmissions;
    }

    /**
     * Helper method to fetch a response value by question and answer keys.
     */
    private String getResponseValue(String questionKey, String answerKey, ArrayList<Map<String, String>> responses) {
        for (Map<String, String> response : responses) {
            if (response.containsKey(questionKey) && response.containsKey(answerKey)) {
                return response.get(answerKey);
            }
        }
        return ""; // Default if not found
    }

}





