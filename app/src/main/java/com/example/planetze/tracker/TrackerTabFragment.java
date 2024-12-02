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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
    private BarChart barChart;


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
        barChart = view.findViewById(R.id.barChart);
        String date = GetDate.getDate();

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
                updateBarChart();
            });
        }
    }

    private void updateBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        double carEmission = processor.carCalculator();
        double publicTransportEmission = processor.publicTransportCalculator();
        double flightEmission = processor.flightCalculator();
        double foodEmission = processor.foodCalculator();
        double clothEmission = processor.clothesCalculator();
        double deviceEmission = processor.deviceCalculator();
        double otherEmission = processor.otherCalculator();

        if (dailyEmission > 0) {
            if (carEmission > 0) entries.add(new BarEntry(0, (float) carEmission, "Drive"));
            if (publicTransportEmission > 0) entries.add(new BarEntry(1, (float) publicTransportEmission, "Public Transport"));
            if (flightEmission > 0) entries.add(new BarEntry(2, (float) flightEmission, "Flight"));
            if (foodEmission > 0) entries.add(new BarEntry(3, (float) foodEmission, "Food"));
            if (clothEmission > 0) entries.add(new BarEntry(4, (float) clothEmission, "Clothes"));
            if (deviceEmission > 0) entries.add(new BarEntry(5, (float) deviceEmission, "Electronics"));
            if (otherEmission > 0) entries.add(new BarEntry(6, (float) otherEmission, "Other"));
            entries.add(new BarEntry(7, 0, "Walking and Cycling"));
        } else {
            entries.add(new BarEntry(7, 0, "Walking and Cycling"));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");

        // Customize the BarChart appearance
        barDataSet.setColors(new int[]{
                ContextCompat.getColor(getContext(), R.color.grey_blue),
                ContextCompat.getColor(getContext(), R.color.teal),
                ContextCompat.getColor(getContext(), R.color.soft_green),
                ContextCompat.getColor(getContext(), R.color.cream),
                ContextCompat.getColor(getContext(), R.color.light_grey),
                ContextCompat.getColor(getContext(), R.color.pale_yellow),
                ContextCompat.getColor(getContext(), R.color.soft_coral)
        });
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);

        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%.2f kg", value);
            }
        });

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true); // Make the bars fit nicely within the chart
        barChart.setExtraOffsets(13, 10, 13, 60);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.getXAxis().setLabelCount(entries.size(), false);

        // Configure X-axis labels
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-40f); // Rotate labels if they are long
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Drive", "Public Transit", "Flight", "Food", "Clothes", "Electronics", "Other", "Walking/Cycling"}));
        xAxis.setYOffset(10f);
        xAxis.setTextSize(14f);

        // Configure Y-axis (left and right)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start Y-axis at zero
        leftAxis.setTextSize(12f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable right Y-axis

        // Configure legend
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();
    }
}
