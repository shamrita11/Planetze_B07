package com.example.planetze.tracker;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.planetze.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackerTabFragment extends Fragment {

    public interface OnTrackerTabInteractionListener {
        void onFoodButtonClicked();
        void onTransportationButtonClicked();
        void onConsumptionButtonClicked();
    }

    private OnTrackerTabInteractionListener mListener;
    private TextView totalEmission;
    private DailyEmissionProcessor processor;
    double dailyEmission;
    private PieChart pieChart;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTrackerTabInteractionListener) {
            mListener = (OnTrackerTabInteractionListener) context; // Cast context to the interface
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement TrackerTabFragment.OnTrackerTabInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker_tab, container, false);

        //TODO: figure out how to add a pie chart
        totalEmission = view.findViewById(R.id.total_emission_value);
        Button buttonTransportation = view.findViewById(R.id.buttonTransportation);
        Button buttonFood = view.findViewById(R.id.buttonFood);
        Button buttonConsumption = view.findViewById(R.id.buttonConsumption);
        pieChart = view.findViewById(R.id.pieChart);

        buttonTransportation.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTransportationButtonClicked();
            }
        });

        buttonFood.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFoodButtonClicked();
            }
        });

        buttonConsumption.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onConsumptionButtonClicked();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        // Doesn't seem to on resume when getting back from logFragments
        super.onResume();
        updateDisplay(true);
    }

    private void updateDisplay(boolean forceRefresh) {
        // Call the daily emission calculator and uploader here
        // Initialize the processor only once in onResume or onCreate
        if (processor == null || forceRefresh) {
            processor = new DailyEmissionProcessor(getContext(), () -> {
                // All data loaded, now calculate and upload emissions
                processor.mainUploader();  // Upload data after loading
                dailyEmission = processor.dailyTotalCalculator();  // Calculate total emissions
                // Convert kg to tonnes (the convert dailyEmission to a string to be displayed)
                // TODO: verify if we need to display in kg or tonnes
                String dailyEmissionText = String.format("%.2f kg", dailyEmission);
                totalEmission.setText(dailyEmissionText);  // Update the UI with the result

                // update the chart once all data are calculated
                updatePieChart();
            });
        }
    }

    private void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        double carEmission = processor.carCalculator();
        double publicTransportEmission = processor.publicTransportCalculator();
        double flightEmission = processor.flightCalculator();
        double foodEmission = processor.foodCalculator();
        double clothEmission = processor.clothesCalculator();
        double deviceEmission = processor.deviceCalculator();

        if (dailyEmission > 0) {
            if (carEmission > 0) entries.add(new PieEntry((float) (carEmission / dailyEmission * 100), "Drive"));
            if (publicTransportEmission > 0) entries.add(new PieEntry((float) (publicTransportEmission / dailyEmission * 100), "Public Transport"));
            if (flightEmission > 0) entries.add(new PieEntry((float) (flightEmission / dailyEmission * 100), "Flight"));
            if (foodEmission > 0) entries.add(new PieEntry((float) (foodEmission / dailyEmission * 100), "Food"));
            if (clothEmission > 0) entries.add(new PieEntry((float) (clothEmission / dailyEmission * 100), "Clothes"));
            if (deviceEmission > 0) entries.add(new PieEntry((float) (deviceEmission / dailyEmission * 100), "Electronics"));
            pieChart.getDescription().setText("Emission Breakdown");
        } else {
            entries.add(new PieEntry(100f, "No Emissions Logged"));
            pieChart.getDescription().setText("No Emissions Logged");
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");

        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.grey_blue));
        colors.add(ContextCompat.getColor(getContext(), R.color.teal));
        colors.add(ContextCompat.getColor(getContext(), R.color.soft_green));
        colors.add(ContextCompat.getColor(getContext(), R.color.cream));
        colors.add(ContextCompat.getColor(getContext(), R.color.light_grey));
        colors.add(ContextCompat.getColor(getContext(), R.color.pale_yellow));
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setCenterText("Emission by Activity");
        pieChart.setCenterTextColor(Color.BLACK);

        pieChart.setEntryLabelTextSize(15f);
        pieChart.setEntryLabelColor(Color.BLACK);
        //set customized format for the labels
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.1f%%", value);
            }
        });

        // Configure legend
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(15f);
        legend.setDrawInside(false);
        legend.setXEntrySpace(25f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);
        legend.setXOffset(10f);
        legend.setTextSize(15f); // Slightly smaller for better wrapping
        legend.setTextColor(Color.BLACK);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
