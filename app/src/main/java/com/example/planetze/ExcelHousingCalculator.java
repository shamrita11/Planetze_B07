package com.example.planetze;


import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;


public class ExcelHousingCalculator {


    private Context appContext;


    public ExcelHousingCalculator(Context context) {
        this.appContext = context;
    }


    private double fetchValueFromCSV(String homeType, String homeSize, String occupants, String electricityBill, String heatingSource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(appContext.getResources().openRawResource(R.raw.housingdata)))) {
            String line = reader.readLine(); // Read header
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns[0].trim().equalsIgnoreCase(homeType) &&
                        columns[1].trim().equalsIgnoreCase(homeSize) &&
                        columns[2].trim().equalsIgnoreCase(occupants) &&
                        columns[3].trim().equalsIgnoreCase(electricityBill) &&
                        columns[4].trim().equalsIgnoreCase(heatingSource)) {
                    return Double.parseDouble(columns[5].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0; // Default if no match found
    }


    // Fetch a specific answer from the responses list
    private String getAnswer(String answerKey, ArrayList<Map<String, String>> responses) {
        for (Map<String, String> response : responses) {
            if (response.containsKey(answerKey)) {
                return response.get(answerKey);
            }
        }
        return ""; // Return an empty string if the answer is not found
    }

    public double calculateHousingScore(ArrayList<Map<String, String>> responses) {
        double totalHousing = 0.0;

        // Extract answers
        String homeType = getAnswer("a8", responses);
        String homeSize = getAnswer("a10", responses);
        String householdSize = getAnswer("a9", responses);
        String electricityBill = getAnswer("a12", responses);
        String heatingSource = getAnswer("a11", responses);
        String waterHeatingEnergy = getAnswer("a13", responses);
        String renewableEnergy = getAnswer("a14", responses);

        // Debug inputs
        System.out.println("Home Type: " + homeType);
        System.out.println("Home Size: " + homeSize);
        System.out.println("Household Size: " + householdSize);
        System.out.println("Electricity Bill: " + electricityBill);
        System.out.println("Heating Source: " + heatingSource);
        System.out.println("Water Heating Energy: " + waterHeatingEnergy);
        System.out.println("Renewable Energy: " + renewableEnergy);

        // Lookup CO2e from CSV
        totalHousing += fetchValueFromCSV(homeType, homeSize, householdSize, electricityBill, heatingSource);

        // Add 233 kg if heating water source differs from home heating source
        if (!heatingSource.equalsIgnoreCase(waterHeatingEnergy)) {
            totalHousing += 233;
        }

        // Renewable energy adjustments
        if ("Yes, primarily (more than 50% of energy use)".equalsIgnoreCase(renewableEnergy)) {
            totalHousing -= 6000;
        } else if ("Yes, partially (less than 50% of energy use)".equalsIgnoreCase(renewableEnergy)) {
            totalHousing -= 4000;
        }

        return totalHousing;
    }

}



