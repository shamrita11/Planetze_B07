package com.example.planetze;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EcoGaugeActivity extends BaseActivity {
    private TextView weekCO2Text;
    private TextView monthCO2Text;
    private TextView yearCO2Text;
    private TextView YourAverageTextView;
    private TextView countryAverageTextView;
    private TextView countryNameTextView;
    private TextView worldAverageTextView;
    private Spinner timeRangeSpinner;
    private PieChart pieChart;
    private LineChart lineChart;
    private EcoGaugeCalculator calculator;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_eco_gauge; // Ensure this matches your XML layout file name
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UI elements
        weekCO2Text = findViewById(R.id.weekCO2Text);
        monthCO2Text = findViewById(R.id.monthCO2Text);
        yearCO2Text = findViewById(R.id.yearCO2Text);
        YourAverageTextView = findViewById(R.id.YourAverageTextView);
        countryAverageTextView = findViewById(R.id.countryAverageTextView);
        countryNameTextView = findViewById(R.id.countryNameTextView);
        worldAverageTextView = findViewById(R.id.worldAverageTextView);
        timeRangeSpinner = findViewById(R.id.spinner_time_range);
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        calculator = new EcoGaugeCalculator();

        // Retrieve the current user ID from UserSession
        String userId = UserSession.userId;

        if (userId == null) {
            Log.e("EcoGaugeActivity", "User ID is null. Please log in again.");
            finish();
            return;
        }

        // Initialize variables and set up views
        calculator.initializeUserVariables(userId);
        calculator.calculateTotalEmissions(userId);

        calculateAndUpdateCO2Emissions(userId);
        setupPieChart(userId);
        setupLineChart(userId);
        setupSpinner(userId);
        displayEmissions(userId); // Placeholder for user country
    }

    private void calculateAndUpdateCO2Emissions(String userId) {
        calculator.calculateEmissions(userId, (weeklyEmissions, monthlyEmissions, yearlyEmissions) -> {
            // Update the text views with calculated values
            weekCO2Text.setText(String.format(Locale.getDefault(), "%.0f kg", weeklyEmissions));
            monthCO2Text.setText(String.format(Locale.getDefault(), "%.0f kg", monthlyEmissions));
            yearCO2Text.setText(String.format(Locale.getDefault(), "%.0f kg", yearlyEmissions));

            // Write the calculated values back to the database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("totalc02emissionsweek").setValue(weeklyEmissions);
            userRef.child("totalc02emissionsmonth").setValue(monthlyEmissions);
            userRef.child("totalc02emissionsyear").setValue(yearlyEmissions).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("EcoGaugeActivity", "Emissions updated successfully in the database.");
                } else {
                    Log.e("EcoGaugeActivity", "Failed to update emissions in the database.");
                }
            });
        });
    }

    private void setupPieChart(String userId) {
        calculator.calculatePieChartData(userId, (foodPercentage, transportationPercentage, consumptionPercentage) -> {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(foodPercentage, "Food"));
            entries.add(new PieEntry(transportationPercentage, "Transportation"));
            entries.add(new PieEntry(consumptionPercentage, "Consumption"));

            PieDataSet dataSet = new PieDataSet(entries, "");
            List<Integer> colors = new ArrayList<>();
            colors.add(ContextCompat.getColor(this, R.color.teal));
            colors.add(ContextCompat.getColor(this, R.color.grey));
            colors.add(ContextCompat.getColor(this, R.color.lightblue));
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(9f);
            data.setValueTextColor(Color.BLACK);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(Locale.getDefault(), "%.0f%%", value);
                }
            });

            pieChart.setData(data);
            pieChart.setUsePercentValues(true);
            pieChart.setEntryLabelTextSize(9f);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.getDescription().setEnabled(false);
            pieChart.setCenterText("Emissions\nBreakdown");
            pieChart.setCenterTextSize(14f);
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.invalidate();

            Legend legend = pieChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setTextColor(Color.BLACK);
            legend.setTextSize(7f);
            legend.setForm(Legend.LegendForm.CIRCLE);
        });
    }

    private void setupLineChart(String userId) {
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);

        displayLineChartData(userId, "Week"); // Default to weekly view
    }

    private void setupSpinner(String userId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_range_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        timeRangeSpinner.setAdapter(adapter);
        timeRangeSpinner.setSelection(0);

        // Display data for the default selection
        displayLineChartData(userId, timeRangeSpinner.getSelectedItem().toString());

        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTimeRange = parent.getItemAtPosition(position).toString();
                displayLineChartData(userId, selectedTimeRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void displayLineChartData(String userId, String timeRange) {
        calculator.calculateLineChartData(userId, timeRange, (aggregatedData, labels) -> {
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < aggregatedData.size(); i++) {
                entries.add(new Entry(i + 1, aggregatedData.get(i))); // X-axis starts from 1
            }

            LineDataSet dataSet = new LineDataSet(entries, "CO2 Emissions (" + timeRange + ")");
            dataSet.setColor(ContextCompat.getColor(this, R.color.teal));
            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.teal));
            dataSet.setDrawValues(false);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(3f);

            LineData data = new LineData(dataSet);
            lineChart.setData(data);

            // Update X-axis labels
            lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value - 1;
                    return index >= 0 && index < labels.size() ? labels.get(index) : "";
                }
            });

            Legend legend = lineChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextSize(10f);
            legend.setTextColor(Color.BLACK);

            lineChart.invalidate();
        });
    }

    private void displayEmissions(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Fetch the user's total CO2 emissions per year
        userRef.child("averagetotalc02emissionsperyear").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userEmissionSnapshot) {
                Double userEmissions = userEmissionSnapshot.getValue(Double.class);

                // Fetch the user's country from the database
                userRef.child("usercountry").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot countrySnapshot) {
                        String userCountry = countrySnapshot.getValue(String.class);

                        if (userCountry == null || userCountry.isEmpty()) {
                            Log.e("DisplayCountryEmissions", "User country is not set.");
                            Toast.makeText(EcoGaugeActivity.this, "User country not found. Please complete the questionnaire.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Fetch emissions data for the user's country and the world
                        DatabaseReference countriesRef = FirebaseDatabase.getInstance().getReference("countries");

                        // Fetch the country emissions directly using the country as a key
                        countriesRef.child(userCountry).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot countryEmissionSnapshot) {
                                Double countryEmissions = countryEmissionSnapshot.child("national_averages").getValue(Double.class);

                                // Fetch the world emissions
                                countriesRef.child("World").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot worldEmissionSnapshot) {
                                        Double worldEmissions = worldEmissionSnapshot.child("national_averages").getValue(Double.class);

                                        // Update the UI with the retrieved data
                                        countryNameTextView.setText(userCountry);
                                        countryAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", countryEmissions != null ? countryEmissions : 0.0));
                                        worldAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", worldEmissions != null ? worldEmissions : 0.0));
                                        YourAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", userEmissions));}

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Firebase", "Failed to fetch world emissions: " + databaseError.getMessage());
                                        Toast.makeText(EcoGaugeActivity.this, "Failed to load world emissions.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Failed to fetch country emissions: " + databaseError.getMessage());
                                Toast.makeText(EcoGaugeActivity.this, "Failed to load country emissions.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to fetch user country: " + databaseError.getMessage());
                        Toast.makeText(EcoGaugeActivity.this, "Failed to retrieve user country.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch user emissions: " + databaseError.getMessage());
                Toast.makeText(EcoGaugeActivity.this, "Failed to retrieve user emissions.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}




