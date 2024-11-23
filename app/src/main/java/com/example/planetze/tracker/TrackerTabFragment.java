package com.example.planetze.tracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planetze.R;

public class TrackerTabFragment extends Fragment {

    public interface OnTrackerTabInteractionListener {
        void onFoodButtonClicked();
        void onTransportationButtonClicked();
        void onConsumptionButtonClicked();
    }

    private OnTrackerTabInteractionListener mListener;
    private TextView totalEmission;
    private DailyEmissionProcessor processor;

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

        // TODO: calculate this whenever this fragment is loaded
//        DailyEmissionProcessor processor = new DailyEmissionProcessor(this.getContext());
//        double dailyEmission = processor.dailyTotalCalculator();
//        processor.mainUploader();
//
//        // make sure to convert kg to tonnes of CO2
//        String dailyEmissionText = (dailyEmission / 1000.0) + " tonnes";
//        totalEmission.setText(dailyEmissionText);

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
        super.onResume();
        // Call the daily emission calculator and uploader here
        // Initialize the processor only once in onResume or onCreate
        if (processor == null) {
            processor = new DailyEmissionProcessor(getContext(), () -> {
                // All data loaded, now calculate and upload emissions
                processor.mainUploader();  // Upload data after loading
                double dailyEmission = processor.dailyTotalCalculator();  // Calculate total emissions
                // Convert kg to tonnes (the convert dailyEmission to a string to be displayed)
                String dailyEmissionText = String.format("%.3f tonnes", dailyEmission / 1000.0);
                totalEmission.setText(dailyEmissionText);  // Update the UI with the result
            });
        }
    }

    private void calculateAndDisplayDailyEmission() {

        //        DailyEmissionProcessor processor = new DailyEmissionProcessor(this.getContext());
//        double dailyEmission = processor.dailyTotalCalculator(); // Calculates total daily emissions
//        processor.mainUploader(); // Uploads the data to Firebase or backend

        // Update the total emission value in the UI
        // Convert kg to tonnes
//        String dailyEmissionText = String.format("%.2f tonnes", dailyEmission / 1000.0);
//        totalEmission.setText(dailyEmissionText);
    }
}
