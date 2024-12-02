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


    private double fetchValueFromCSV(int rowNum, int colNum) {
        double cellValue = 0.0;
        try {
            // Open the CSV file
            InputStream fileStream = appContext.getResources().openRawResource(R.raw.housingdata); // Ensure the name matches the file in res/raw
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));


            String line;
            int currentRow = 0;


            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                if (currentRow == rowNum) {
                    // Split the row into columns
                    String[] columns = line.split(","); // Adjust the delimiter if needed
                    if (colNum < columns.length) {
                        cellValue = Double.parseDouble(columns[colNum].trim());
                    }
                    break;
                }
                currentRow++;
            }


            reader.close();
            fileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cellValue;
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
        int[][] rowSelector = {{9, 33, 57, 81, 57}, {17, 41, 65, 90, 65}, {25, 49, 73, 98, 73}};
        int[] heatingOffsets = {2, 7, 12, 17, 22, 27};

        // Extract answers
        String homeSize = getAnswer("a10", responses);
        String householdSize = getAnswer("a9", responses);
        String heatingEnergy = getAnswer("a11", responses);
        String electricityBill = getAnswer("a12", responses);
        String waterHeatingEnergy = getAnswer("a13", responses);
        String renewableEnergy = getAnswer("a14", responses);

        // Debug inputs
        System.out.println("Debug - Home Size: " + homeSize);
        System.out.println("Debug - Household Size: " + householdSize);
        System.out.println("Debug - Heating Energy: " + heatingEnergy);
        System.out.println("Debug - Electricity Bill: " + electricityBill);
        System.out.println("Debug - Water Heating Energy: " + waterHeatingEnergy);
        System.out.println("Debug - Renewable Energy: " + renewableEnergy);

        // Validate inputs
        if (homeSize == null || householdSize == null || homeSize.isEmpty() || householdSize.isEmpty()) {
            throw new IllegalArgumentException("Missing required input: homeSize or householdSize");
        }

        // Map home size and household size to indices
        int row13 = mapHomeSizeToIndex(homeSize);
        int row15 = mapHouseholdSizeToIndex(householdSize);

        // Validate mapped indices
        if (row13 < 0 || row13 >= rowSelector.length || row15 < 0 || row15 >= rowSelector[row13].length) {
            throw new IllegalArgumentException("Invalid indices for home size or household size. " +
                    "Home Size: " + homeSize + " (" + row13 + "), " +
                    "Household Size: " + householdSize + " (" + row15 + ")");
        }

        int baseRow = rowSelector[row13][row15] + 3;

        // Handle heating-related logic
        int heatingIndex = mapHeatingEnergyToIndex(heatingEnergy);
        if (heatingIndex < 0 || heatingIndex >= heatingOffsets.length) {
            throw new IllegalArgumentException("Invalid heating energy index: " + heatingIndex);
        }

        if (heatingIndex == 5) { // "Other"
            double heatingSum = 0.0;
            for (int offset = 0; offset < 5; offset++) {
                int column = heatingOffsets[offset];
                heatingSum += fetchValueFromCSV(baseRow, column);
            }
            totalHousing += heatingSum / 5;
        } else {
            int column = heatingOffsets[heatingIndex];
            totalHousing += fetchValueFromCSV(baseRow, column);
        }

        // Add 233 kg if heating water source differs from home heating source
        if (!heatingEnergy.equalsIgnoreCase(waterHeatingEnergy)) {
            totalHousing += 233;
        }

        // Renewable energy adjustments
        if ("Yes, primarily (more than 50% of energy use)".equalsIgnoreCase(renewableEnergy)) {
            totalHousing -= 6000;
        } else if ("Yes, partially (less than 50% of energy use)".equalsIgnoreCase(renewableEnergy)) {
            totalHousing -= 4000;
        }

        // Electricity bill adjustment
        Map<String, Double> electricityEmissions = Map.of(
                "Under $50", 500.0,
                "$50–$100", 1000.0,
                "$100–$150", 1500.0,
                "$150–$200", 2000.0,
                "Over $200", 2500.0
        );

        if (electricityEmissions.containsKey(electricityBill)) {
            totalHousing += electricityEmissions.get(electricityBill);
        } else {
            System.out.println("Invalid or missing electricity bill data.");
        }

        return totalHousing;
    }
    

    private int mapHomeSizeToIndex(String homeSize) {
        switch (homeSize) {
            case "Under 1000 sq. ft.":
                return 0;
            case "1000–2000 sq. ft.":
                return 1;
            case "Over 2,000 sq. ft.":
                return 2;
            default:
                return -1;
        }
    }


    private int mapHouseholdSizeToIndex(String householdSize) {
        switch (householdSize) {
            case "1":
                return 0;
            case "2":
                return 1;
            case "3–4":
                return 2;
            case "5 or more people":
                return 3;
            default:
                return -1;
        }
    }


    private int mapHeatingEnergyToIndex(String heatingEnergy) {
        switch (heatingEnergy) {
            case "Natural Gas":
                return 0;
            case "Electricity":
                return 1;
            case "Oil":
                return 2;
            case "Propane":
                return 3;
            case "Wood":
                return 4;
            default:
                return 5; // "Other"
        }
    }
}



