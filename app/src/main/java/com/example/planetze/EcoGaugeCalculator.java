package com.example.planetze;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EcoGaugeCalculator {

    private final DatabaseReference databaseReference;

    public EcoGaugeCalculator() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public interface EmissionCallback {
        void onEmissionCalculated(float weeklyEmissions, float monthlyEmissions, float yearlyEmissions);
    }
    public interface LineChartCallback {
        void onDataReady(List<Float> aggregatedData, List<String> labels);
    }

    public interface PieChartCallback {
        void onDataReady(float foodPercentage, float transportationPercentage, float consumptionPercentage);
    }

    public void initializeUserVariables(String userId) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || !dataSnapshot.hasChild("totalc02emissionsyear")) {
                    HashMap<String, Object> updates = new HashMap<>();
                    if (!dataSnapshot.hasChild("totalc02emissionsyear")) {
                        updates.put("totalc02emissionsyear", 0);
                    }
                    if (!dataSnapshot.hasChild("totalc02emissionsmonth")) {
                        updates.put("totalc02emissionsmonth", 0);
                    }
                    if (!dataSnapshot.hasChild("totalc02emissions_food")) {
                        updates.put("totalc02emissions_food", 0);
                    }
                    if (!dataSnapshot.hasChild("totalc02emissions_transportation")) {
                        updates.put("totalc02emissions_transportation", 0);
                    }
                    if (!dataSnapshot.hasChild("totalc02emissions_consumption")) {
                        updates.put("totalc02emissions_consumption", 0);
                    }
                    if (!dataSnapshot.hasChild("totaloffsetc02")) {
                        updates.put("totaloffsetc02", 0);
                    }

                    if (!updates.isEmpty()) {
                        userRef.updateChildren(updates).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "Variables initialized successfully for user: " + userId);
                            } else {
                                Log.e("Firebase", "Failed to initialize variables for user: " + userId);
                            }
                        });
                    } else {
                        Log.d("Firebase", "All user variables already exist.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    public void calculateEmissions(String userId, EmissionCallback callback) {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    float weeklyEmissions = 0f;
                    float monthlyEmissions = 0f;
                    float yearlyEmissions = 0f;

                    // Get the current date
                    Calendar calendar = Calendar.getInstance();
                    int currentYear = calendar.get(Calendar.YEAR);
                    int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed
                    int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

                    // Fetch daily emissions
                    if (dataSnapshot.child("daily_emission").exists()) {
                        for (DataSnapshot dateSnapshot : dataSnapshot.child("daily_emission").getChildren()) {
                            String date = dateSnapshot.getKey(); // Format: YYYY-MM-DD
                            if (date == null) continue;

                            // Parse the year, month, and week from the date string
                            int year = Integer.parseInt(date.substring(0, 4));
                            int month = Integer.parseInt(date.substring(5, 7));
                            int week = getWeekOfYear(date);

                            float dailyTotal = dateSnapshot.child("emission/total").getValue(Float.class) != null
                                    ? dateSnapshot.child("emission/total").getValue(Float.class)
                                    : 0f;

                            // Add to weekly total if within the last 7 days
                            if (year == currentYear && week == currentWeek) {
                                weeklyEmissions += dailyTotal;
                            }

                            // Add to monthly total if within the current month
                            if (year == currentYear && month == currentMonth) {
                                monthlyEmissions += dailyTotal;
                            }

                            // Add to yearly total if within the current year
                            if (year == currentYear) {
                                yearlyEmissions += dailyTotal;
                            }
                        }
                    }

                    // Add energy bill emissions for the current month and year
                    if (dataSnapshot.child("bill").exists()) {
                        DataSnapshot billSnapshot = dataSnapshot.child("bill");
                        for (DataSnapshot monthSnapshot : billSnapshot.getChildren()) {
                            String monthKey = monthSnapshot.getKey(); // Format: YYYY-MM
                            if (monthKey == null) continue;

                            int billYear = Integer.parseInt(monthKey.substring(0, 4));
                            int billMonth = Integer.parseInt(monthKey.substring(5, 7));

                            float monthlyBillEmission = monthSnapshot.child("monthly_emission").getValue(Float.class) != null
                                    ? monthSnapshot.child("monthly_emission").getValue(Float.class)
                                    : 0f;

                            if (billYear == currentYear && billMonth == currentMonth) {
                                monthlyEmissions += monthlyBillEmission;
                            }

                            if (billYear == currentYear) {
                                yearlyEmissions += monthlyBillEmission;
                            }
                        }
                    }

                    // Subtract offset balance from yearly emissions
                    float offsetBalance = dataSnapshot.child("totaloffsetc02").getValue(Float.class) != null
                            ? dataSnapshot.child("totaloffsetc02").getValue(Float.class)
                            : 0f;

                    yearlyEmissions -= offsetBalance;
                    if (yearlyEmissions < 0) {
                        yearlyEmissions = 0;
                    }
                    // Pass result to callback
                    callback.onEmissionCalculated(weeklyEmissions, monthlyEmissions, yearlyEmissions);

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onEmissionCalculated(0f, 0f, 0f); // Handle errors gracefully
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onEmissionCalculated(0f, 0f, 0f); // Handle errors gracefully
            }
        });
    }

    // Helper method to calculate the week of the year from a date string
    private int getWeekOfYear(String date) {
        try {
            String[] parts = date.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day); // Months are 0-indexed
            return calendar.get(Calendar.WEEK_OF_YEAR);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void calculateLineChartData(String userId, String viewType, LineChartCallback callback) {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    List<Float> aggregatedData = new ArrayList<>();
                    List<String> labels = new ArrayList<>();

                    Calendar calendar = Calendar.getInstance();
                    int currentYear = calendar.get(Calendar.YEAR);
                    int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed

                    if (viewType.equals("Daily")) {
                        // Aggregate data for the last 7 days
                        for (int i = 0; i < 7; i++) {
                            float dailyTotal = 0f;

                            for (DataSnapshot dateSnapshot : dataSnapshot.child("daily_emission").getChildren()) {
                                String date = dateSnapshot.getKey();
                                if (date == null) continue;

                                int year = Integer.parseInt(date.substring(0, 4));
                                int month = Integer.parseInt(date.substring(5, 7));
                                int day = Integer.parseInt(date.substring(8, 10));

                                // Check if the date matches the last 'i' days
                                Calendar targetDate = (Calendar) calendar.clone();
                                targetDate.add(Calendar.DAY_OF_YEAR, -i);
                                if (year == targetDate.get(Calendar.YEAR) &&
                                        month == (targetDate.get(Calendar.MONTH) + 1) &&
                                        day == targetDate.get(Calendar.DAY_OF_MONTH)) {

                                    dailyTotal += dateSnapshot.child("emission/total").getValue(Float.class) != null
                                            ? dateSnapshot.child("emission/total").getValue(Float.class)
                                            : 0f;
                                }
                            }

                            aggregatedData.add(0, dailyTotal); // Add to start of the list
                            labels.add(0, String.format(Locale.getDefault(), "%02d-%02d",
                                    calendar.get(Calendar.MONTH) + 1,
                                    calendar.get(Calendar.DAY_OF_MONTH) - i)); // Add label
                        }
                    } else if (viewType.equals("Weekly")) {
                        // Aggregate data for the last 4 weeks
                        for (int i = 0; i < 4; i++) {
                            float weeklyTotal = 0f;

                            for (DataSnapshot dateSnapshot : dataSnapshot.child("daily_emission").getChildren()) {
                                String date = dateSnapshot.getKey();
                                if (date == null) continue;

                                int year = Integer.parseInt(date.substring(0, 4));
                                int week = getWeekOfYear(date);

                                // Check if the date falls in the target week
                                if (year == currentYear && week == calendar.get(Calendar.WEEK_OF_YEAR) - i) {
                                    weeklyTotal += dateSnapshot.child("emission/total").getValue(Float.class) != null
                                            ? dateSnapshot.child("emission/total").getValue(Float.class)
                                            : 0f;
                                }
                            }

                            aggregatedData.add(0, weeklyTotal); // Add to start of the list
                            labels.add(0, "Week " + (calendar.get(Calendar.WEEK_OF_YEAR) - i)); // Add label
                        }
                    } else if (viewType.equals("Monthly")) {
                        // Aggregate data for the last 6 months
                        for (int i = 0; i < 6; i++) {
                            float monthlyTotal = 0f;

                            Calendar targetDate = (Calendar) calendar.clone();
                            targetDate.add(Calendar.MONTH, -i);
                            int targetYear = targetDate.get(Calendar.YEAR);
                            int targetMonth = targetDate.get(Calendar.MONTH) + 1; // Months are 0-indexed

                            for (DataSnapshot dateSnapshot : dataSnapshot.child("daily_emission").getChildren()) {
                                String date = dateSnapshot.getKey();
                                if (date == null) continue;

                                int year = Integer.parseInt(date.substring(0, 4));
                                int month = Integer.parseInt(date.substring(5, 7));

                                if (year == targetYear && month == targetMonth) {
                                    monthlyTotal += dateSnapshot.child("emission/total").getValue(Float.class) != null
                                            ? dateSnapshot.child("emission/total").getValue(Float.class)
                                            : 0f;
                                }
                            }

                            aggregatedData.add(0, monthlyTotal);
                            labels.add(0, String.format(Locale.getDefault(), "%02d-%04d", targetMonth, targetYear));
                        }
                    }
                        callback.onDataReady(aggregatedData, labels);

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onDataReady(new ArrayList<>(), new ArrayList<>()); // Handle errors gracefully
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReady(new ArrayList<>(), new ArrayList<>()); // Handle errors gracefully
            }
        });
    }

    public void calculatePieChartData(String userId, PieChartCallback callback) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        userRef.child("daily_emission").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    float totalFood = 0f;
                    float totalTransportation = 0f;
                    float totalConsumption = 0f;

                    // Sum up daily totals for food, transportation, and consumption
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        float dailyFood = dateSnapshot.child("emission/food").getValue(Float.class) != null
                                ? dateSnapshot.child("emission/food").getValue(Float.class)
                                : 0f;

                        float dailyTransportation = dateSnapshot.child("emission/transportation").getValue(Float.class) != null
                                ? dateSnapshot.child("emission/transportation").getValue(Float.class)
                                : 0f;

                        float dailyConsumption = dateSnapshot.child("emission/consumption").getValue(Float.class) != null
                                ? dateSnapshot.child("emission/consumption").getValue(Float.class)
                                : 0f;

                        totalFood += dailyFood;
                        totalTransportation += dailyTransportation;
                        totalConsumption += dailyConsumption;
                    }

                    // Write the calculated totals to the database
                    userRef.child("totalc02emissions_food").setValue(totalFood);
                    userRef.child("totalc02emissions_transportation").setValue(totalTransportation);
                    userRef.child("totalc02emissions_consumption").setValue(totalConsumption);

                    // Calculate percentages
                    float totalEmissions = totalFood + totalTransportation + totalConsumption;

                    if (totalEmissions == 0) {
                        // Avoid division by zero
                        callback.onDataReady(0f, 0f, 0f);
                    } else {
                        float foodPercentage = (totalFood / totalEmissions) * 100;
                        float transportationPercentage = (totalTransportation / totalEmissions) * 100;
                        float consumptionPercentage = (totalConsumption / totalEmissions) * 100;

                        // Pass percentages to callback
                        callback.onDataReady(foodPercentage, transportationPercentage, consumptionPercentage);
                    }
                } catch (Exception e) {
                    Log.e("EcoGaugeCalculator", "Error calculating pie chart data: " + e.getMessage());
                    callback.onDataReady(0f, 0f, 0f);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("EcoGaugeCalculator", "Database error: " + databaseError.getMessage());
                callback.onDataReady(0f, 0f, 0f);
            }
        });
    }
    public void calculateTotalEmissions(String userId) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // Initialize variables for total emissions
                    float foodEmissions = 0f;
                    float transportationEmissions = 0f;
                    float consumptionEmissions = 0f;
                    float energyBillEmissions = 0f;
                    float offsetBalance = 0f;

                    // Check if `totalc02emissions_food` exists
                    if (dataSnapshot.child("totalc02emissions_food").exists()) {
                        foodEmissions = dataSnapshot.child("totalc02emissions_food").getValue(Float.class) != null
                                ? dataSnapshot.child("totalc02emissions_food").getValue(Float.class)
                                : 0f;
                    }

                    // Check if `totalc02emissions_transportation` exists
                    if (dataSnapshot.child("totalc02emissions_transportation").exists()) {
                        transportationEmissions = dataSnapshot.child("totalc02emissions_transportation").getValue(Float.class) != null
                                ? dataSnapshot.child("totalc02emissions_transportation").getValue(Float.class)
                                : 0f;
                    }

                    // Check if `totalc02emissions_consumption` exists
                    if (dataSnapshot.child("totalc02emissions_consumption").exists()) {
                        consumptionEmissions = dataSnapshot.child("totalc02emissions_consumption").getValue(Float.class) != null
                                ? dataSnapshot.child("totalc02emissions_consumption").getValue(Float.class)
                                : 0f;
                    }

                    // Add energy bill emissions for all months in the database
                    if (dataSnapshot.child("bill").exists()) {
                        for (DataSnapshot monthSnapshot : dataSnapshot.child("bill").getChildren()) {
                            float monthlyBillEmission = monthSnapshot.child("monthly_emission").getValue(Float.class) != null
                                    ? monthSnapshot.child("monthly_emission").getValue(Float.class)
                                    : 0f;
                            energyBillEmissions += monthlyBillEmission;
                        }
                    }

                    // Fetch offset balance
                    if (dataSnapshot.child("totaloffsetc02").exists()) {
                        offsetBalance = dataSnapshot.child("totaloffsetc02").getValue(Float.class) != null
                                ? dataSnapshot.child("totaloffsetc02").getValue(Float.class)
                                : 0f;
                    }

                    // Compute total CO2 emissions
                    float totalEmissions = foodEmissions + transportationEmissions + consumptionEmissions + energyBillEmissions;

                    // Subtract offset balance
                    totalEmissions -= offsetBalance;

                    // Ensure total emissions are not negative
                    if (totalEmissions < 0) {
                        totalEmissions = 0;
                    }

                    // Write `TotalC02Emissions` to the database
                    userRef.child("TotalC02Emissions").setValue(totalEmissions).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Total CO2 emissions successfully calculated and updated for user: " + userId);
                        } else {
                            Log.e("Firebase", "Failed to update TotalC02Emissions for user: " + userId);
                        }
                    });

                } catch (Exception e) {
                    Log.e("Firebase", "Error calculating TotalC02Emissions: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

}
