package com.example.planetze;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class EcoGaugeActivity extends AppCompatActivity {
    private TextView weekCO2Text;
    private TextView monthCO2Text;
    private TextView yearCO2Text;
    private Spinner timeRangeSpinner;
    private TextView countryAverageTextView;
    private TextView countryNameTextView;
    private TextView worldAverageTextView;
    private HashMap<String, Double> emissionsData;
    private PieChart pieChart;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_gauge);

        weekCO2Text = findViewById(R.id.weekCO2Text);
        monthCO2Text = findViewById(R.id.monthCO2Text);
        yearCO2Text = findViewById(R.id.yearCO2Text);
        calculateAndUpdateCO2Emissions();

        setupPieChart();
        lineChart = findViewById(R.id.lineChart);
        setupLineChart();

        timeRangeSpinner = findViewById(R.id.spinner_time_range);
        setupSpinner();

        countryAverageTextView = findViewById(R.id.countryAverageTextView);
        countryNameTextView = findViewById(R.id.countryNameTextView);
        worldAverageTextView = findViewById(R.id.worldAverageTextView);

        emissionsData = new HashMap<>();
        initializeEmissionsData();
        displayCountryEmissions("Democratic Republic of Congo"); // Placeholder for now
    }
    private void calculateAndUpdateCO2Emissions() {
        float weeklyEmissions = 500f;  // placeholder
        float monthlyEmissions = 12000f;  // placeholder
        float yearlyEmissions = 144800f;  //placeholder

        weekCO2Text.setText(String.format(Locale.getDefault(), "%.0f kg", weeklyEmissions));
        monthCO2Text.setText(String.format(Locale.getDefault(), "%.0f kg", monthlyEmissions));
        yearCO2Text.setText(String.format(Locale.getDefault(), "%.0f kg", yearlyEmissions));
    }


    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_range_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        timeRangeSpinner.setAdapter(adapter);

        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTimeRange = parent.getItemAtPosition(position).toString();
                displayLineChartData(selectedTimeRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupLineChart() {
        // chart properties
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);

        displayLineChartData("Week"); // Default chart
    }
    private void displayLineChartData(String timeRange) {
        List<Entry> entries = new ArrayList<>();

        // Dummy data for testing
        switch (timeRange) {
            case "Daily":
                entries.add(new Entry(1, 300));
                entries.add(new Entry(2, 310));
                entries.add(new Entry(3, 290));
                entries.add(new Entry(4, 305));
                break;
            case "Weekly":
                entries.add(new Entry(1, 3000));
                entries.add(new Entry(2, 3200));
                entries.add(new Entry(3, 3100));
                entries.add(new Entry(4, 3300));
                break;
            case "Monthly":
                entries.add(new Entry(1, 14000));
                entries.add(new Entry(2, 16000));
                entries.add(new Entry(3, 11000));
                entries.add(new Entry(4, 10000));
                break;
        }

        LineDataSet dataSet = new LineDataSet(entries, "CO2 Emissions (" + timeRange + ")");
        dataSet.setColor(ContextCompat.getColor(this, R.color.teal)); // Line color
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.teal)); // Circle point color
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2f); // Line thickness
        dataSet.setCircleRadius(3f); // Circle size

        LineData data = new LineData(dataSet);

        lineChart.setData(data);

        // Set Legend properties
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(10f);
        legend.setTextColor(Color.BLACK);

        // Refresh the chart
        lineChart.invalidate();
    }


    private void initializeEmissionsData() {
        InputStream inputStream = getResources().openRawResource(R.raw.emissions_data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length == 2) {
                    String country = columns[0].trim();
                    double emissions = Double.parseDouble(columns[1].trim());
                    emissionsData.put(country, emissions);
                }
            }
        } catch (Exception e) {
            Log.e("EcoGaugeActivity", "Error reading CSV file", e);
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                Log.e("EcoGaugeActivity", "Error closing reader", e);
            }
        }
    }

    private void displayCountryEmissions(String userCountry) {
        Double countryEmissions = emissionsData.getOrDefault(userCountry, 0.0);
        Double worldEmissions = emissionsData.getOrDefault("World", 0.0);

        countryNameTextView.setText(userCountry);
        countryAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", countryEmissions));
        worldAverageTextView.setText(String.format(Locale.getDefault(), "%.2f kg per year", worldEmissions));
    }

    private void setupPieChart() {
        pieChart = findViewById(R.id.pieChart);

        // Dummy data for emissions breakdown
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "Transportation"));
        entries.add(new PieEntry(30f, "Energy"));
        entries.add(new PieEntry(20f, "Food"));
        entries.add(new PieEntry(10f, "Consumption"));

        PieDataSet dataSet = new PieDataSet(entries,"");

        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(this, R.color.teal));
        colors.add(ContextCompat.getColor(this, R.color.grey));
        colors.add(ContextCompat.getColor(this, R.color.lightblue));
        colors.add(ContextCompat.getColor(this, R.color.cream));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(9f);
        data.setValueTextColor(Color.BLACK);

        //set customized format for the labels
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
    }
}


