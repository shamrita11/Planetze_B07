package com.example.planetze;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
        countryAverageTextView = findViewById(R.id.countryAverageTextView);
        countryNameTextView = findViewById(R.id.countryNameTextView);
        worldAverageTextView = findViewById(R.id.worldAverageTextView);
        timeRangeSpinner = findViewById(R.id.spinner_time_range);
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        calculator = new EcoGaugeCalculator();

        // Calculate and update CO2 emissions
        calculateAndUpdateCO2Emissions();

        // Set up visualizations
        setupPieChart();
        setupLineChart();
        setupSpinner();

        // Display country-specific emissions
        displayCountryEmissions("Canada"); // Placeholder for user country

        //calculates for the user
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            String userId = auth.getCurrentUser().getUid();
//            calculator.initializeUserVariables(userId);
//        } else {
//            Log.e("EcoGaugeActivity", "User is not logged in.");
//        }
        String userId = "user1";
        calculator.initializeUserVariables(userId);
        calculator.calculateTotalEmissions(userId); //put this in user instead of here
    }

    private void calculateAndUpdateCO2Emissions() {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        String userId = auth.getCurrentUser().getUid();
            String userId = "user1";

            // Use EcoGaugeCalculator to calculate emissions
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


        private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_range_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        timeRangeSpinner.setAdapter(adapter);
        timeRangeSpinner.setSelection(0); // Set default selection

        // Display data for the default selection
        displayLineChartData(timeRangeSpinner.getSelectedItem().toString());

        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTimeRange = parent.getItemAtPosition(position).toString();
                if (selectedTimeRange != null && !selectedTimeRange.isEmpty()) {
                    displayLineChartData(selectedTimeRange);
                } else {
                    Log.e("Spinner", "Selected time range is null or empty.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
        private void setupPieChart() {
            String userId = "user1"; // Replace with dynamic logic if necessary
            calculator.calculatePieChartData(userId, (foodPercentage, transportationPercentage, consumptionPercentage) -> {
                // Create entries for pie chart
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

                pieChart.invalidate(); // Refresh the chart

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

        private void setupLineChart() {
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);

        displayLineChartData("Week"); // Default to weekly view
    }

    private void displayLineChartData(String timeRange) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //if (auth.getCurrentUser() != null) {
            String userId = "user1";
            //String userId = auth.getCurrentUser().getUid();

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

                lineChart.invalidate(); // Refresh the chart
            });
       // } else {
           // Log.e("EcoGaugeActivity", "User is not logged in.");
        //}
    }

    private void displayCountryEmissions(String userCountry) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("countries");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double countryEmissions = 0.0;
                Double worldEmissions = 0.0;

                for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                    String countryName = countrySnapshot.child("name").getValue(String.class);
                    if (countryName != null && countryName.equalsIgnoreCase(userCountry)) {
                        countryEmissions = countrySnapshot.child("national_averages").getValue(Double.class);
                    }
                    if (countryName != null && countryName.equalsIgnoreCase("World")) {
                        worldEmissions = countrySnapshot.child("national_averages").getValue(Double.class);
                    }
                }

                countryNameTextView.setText(userCountry);
                countryAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", countryEmissions));
                worldAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", worldEmissions));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching country emissions data: " + databaseError.getMessage());
            }
        });
    }
}



