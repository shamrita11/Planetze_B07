package com.example.planetze;

import java.util.ArrayList;
import java.util.Map;

public class ConsumptionCalculator {

    public double calculateConsumptionEmissions(ArrayList<Map<String, String>> responses) {
        double totalEmissions = 0.0;

        // Default CO2 emissions for clothing purchases (kg/year)
        Map<String, Double> clothingEmissions = Map.of(
                "Monthly", 360.0,
                "Quarterly", 120.0,
                "Annually", 100.0,
                "Rarely", 5.0
        );

        // CO2 emissions adjustment for second-hand/eco-friendly products
        Map<String, Double> secondHandReduction = Map.of(
                "Yes, regularly", 0.5,  // Reduces emissions by 50%
                "Yes, occasionally", 0.7,  // Reduces emissions by 30%
                "No", 1.0  // No reduction
        );

        // CO2 emissions for electronic devices (kg/year)
        Map<String, Double> electronicDeviceEmissions = Map.of(
                "None", 0.0,
                "1", 300.0,
                "2", 600.0,
                "3", 900.0
        );

        // Recycling reduction based on clothing frequency
        Map<String, Map<String, Double>> recyclingReductionByClothing = Map.of(
                "Monthly", Map.of(
                        "Occasionally", 54.0,
                        "Frequently", 108.0,
                        "Always", 180.0
                ),
                "Annually", Map.of(
                        "Occasionally", 15.0,
                        "Frequently", 30.0,
                        "Always", 50.0
                ),
                "Rarely", Map.of(
                        "Occasionally", 0.75,
                        "Frequently", 1.5,
                        "Always", 2.5
                )
        );

        // Device-specific recycling reductions
        Map<String, Map<String, Double>> deviceRecyclingAdjustments = Map.of(
                "1", Map.of(
                        "Occasionally", 45.0,
                        "Frequently", 60.0,
                        "Always", 90.0
                ),
                "2", Map.of(
                        "Occasionally", 60.0,
                        "Frequently", 120.0,
                        "Always", 180.0
                ),
                "3 or more", Map.of(
                        "Occasionally", 90.0,
                        "Frequently", 180.0,
                        "Always", 270.0
                )
        );

        // Fetch user responses
        String clothingFrequency = getResponseValue("q15", "a15", responses);
        String secondHandHabit = getResponseValue("q16", "a16", responses);
        String deviceCount = getResponseValue("q17", "a17", responses);
        String recyclingHabit = getResponseValue("q18", "a18", responses);

        // Calculate emissions for clothing purchases
        double clothingEmissionsValue = 0.0;
        if (clothingEmissions.containsKey(clothingFrequency)) {
            clothingEmissionsValue = clothingEmissions.get(clothingFrequency);
        }

        // Apply second-hand/eco-friendly reduction
        if (secondHandReduction.containsKey(secondHandHabit)) {
            double reductionFactor = secondHandReduction.get(secondHandHabit);
            clothingEmissionsValue *= reductionFactor; // Apply reduction
        }

        totalEmissions += clothingEmissionsValue;

        // Calculate emissions for electronic devices
        double deviceEmissionsValue = 0.0;
        if (electronicDeviceEmissions.containsKey(deviceCount)) {
            deviceEmissionsValue = electronicDeviceEmissions.get(deviceCount);
        }
        totalEmissions += deviceEmissionsValue;

        // Apply recycling reductions for clothing
        if (recyclingReductionByClothing.containsKey(clothingFrequency)) {
            Map<String, Double> recyclingMap = recyclingReductionByClothing.get(clothingFrequency);
            if (recyclingMap.containsKey(recyclingHabit)) {
                totalEmissions -= recyclingMap.get(recyclingHabit);
            }
        }

        // Apply device-specific recycling reductions
        if (deviceRecyclingAdjustments.containsKey(deviceCount)) {
            Map<String, Double> deviceRecyclingMap = deviceRecyclingAdjustments.get(deviceCount);
            if (deviceRecyclingMap.containsKey(recyclingHabit)) {
                totalEmissions -= deviceRecyclingMap.get(recyclingHabit);
            }
        }

        return totalEmissions; // Return total consumption-related emissions in kg/year
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

