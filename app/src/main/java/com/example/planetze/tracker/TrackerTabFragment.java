package com.example.planetze.tracker;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.planetze.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackerTabFragment extends Fragment {

    /**
     * This interface helps TrackerTabFragment to load fragments in TrackerActivity
     */
    public interface OnTrackerTabInteractionListener {
        void onFoodButtonClicked(boolean isIncrement, String date);
        void onTransportationButtonClicked(boolean isIncrement, String date);
        void onConsumptionButtonClicked(boolean isIncrement, String date);
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
            throw new ClassCastException(context
                    + " must implement TrackerTabFragment.OnTrackerTabInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker_tab, container, false);

        totalEmission = view.findViewById(R.id.total_emission_value);
        Button buttonTransportation = view.findViewById(R.id.buttonTransportation);
        Button buttonFood = view.findViewById(R.id.buttonFood);
        Button buttonConsumption = view.findViewById(R.id.buttonConsumption);
        pieChart = view.findViewById(R.id.pieChart);
        String date = GetDate.getDate();

        updateDisplay(true);

        // We make an assumption here:
        // For Tracker tab, when user log information, we assume that if they log for the same
        // activity multiple time, they are adding onto their previous log. For example, if the
        // use logged 1 hour of bus. If they log another 0.5 hour, we assume that today they
        // in total took 1.5 hours of bus. This is reasonable because activities can happen
        // through out the day, and the same activity can happen multiple times. So the user
        // can choose to log whenever they want and add onto it later. (In calendar section,
        // the assumption is different)
        buttonTransportation.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTransportationButtonClicked(true, date);
            }
        });

        buttonFood.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFoodButtonClicked(true, date);
            }
        });

        buttonConsumption.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onConsumptionButtonClicked(true, date);
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
            processor = new DailyEmissionProcessor(getContext(), GetDate.getDate(), () -> {
                processor.mainUploader();
                dailyEmission = processor.dailyTotalCalculator();  // Calculate total emissions
                // Update the daily emission text in the xml
                String dailyEmissionText = String.format(Locale.getDefault(),"%.2f kg", dailyEmission);
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
        double otherEmission = processor.otherCalculator();

        if (dailyEmission + 0.000001 > 0.000001) {
            if (carEmission > 0) entries.add(new PieEntry((float) (carEmission / dailyEmission * 100), "Drive"));
            if (publicTransportEmission > 0) entries.add(new PieEntry((float) (publicTransportEmission / dailyEmission * 100), "Public Transport"));
            if (flightEmission > 0) entries.add(new PieEntry((float) (flightEmission / dailyEmission * 100), "Flight"));
            if (foodEmission > 0) entries.add(new PieEntry((float) (foodEmission / dailyEmission * 100), "Food"));
            if (clothEmission > 0) entries.add(new PieEntry((float) (clothEmission / dailyEmission * 100), "Clothes"));
            if (deviceEmission > 0) entries.add(new PieEntry((float) (deviceEmission / dailyEmission * 100), "Electronics"));
            if (otherEmission > 0) entries.add(new PieEntry((float) (otherEmission / dailyEmission * 100), "Other Purchases"));
            pieChart.getDescription().setText("Emission Breakdown");
        } else {
            entries.add(new PieEntry(100f, "Walking and Cycling"));
            pieChart.getDescription().setText("No Activity Logged");
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");

        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.grey_blue));
        colors.add(ContextCompat.getColor(getContext(), R.color.teal));
        colors.add(ContextCompat.getColor(getContext(), R.color.soft_green));
        colors.add(ContextCompat.getColor(getContext(), R.color.cream));
        colors.add(ContextCompat.getColor(getContext(), R.color.light_gray));
        colors.add(ContextCompat.getColor(getContext(), R.color.pale_yellow));
        colors.add(ContextCompat.getColor(getContext(), R.color.high_contrast_teal));
        colors.add(ContextCompat.getColor(getContext(), R.color.soft_coral));

        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(2f); // Add spacing between slices
        pieDataSet.setValueLinePart1Length(0.6f); // Adjust the length of the value lines
        pieDataSet.setValueLinePart2Length(0.2f); // Adjust the second part of the value lines
        pieDataSet.setValueLineColor(Color.BLACK); // Make the lines clearer
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // Position value labels outside slices
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
        pieChart.setExtraOffsets(15, 15, 15, 15);

        pieChart.setEntryLabelTextSize(15f);
        pieChart.setEntryLabelColor(Color.BLACK);
        //set customized format for the labels
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.2f%%", value);
            }
        });

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pieEntry = (PieEntry) e;
                    Toast.makeText(getContext(), pieEntry.getLabel() + ": " + pieEntry.getValue() + "%", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });

        // Configure legend
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(15f);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setMaxSizePercent(0.95f);
        legend.setXEntrySpace(25f);
        legend.setYEntrySpace(5f);
        legend.setYOffset(10f);
        legend.setXOffset(10f);
        legend.setTextSize(15f); // Slightly smaller for better wrapping
        legend.setTextColor(Color.BLACK);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
