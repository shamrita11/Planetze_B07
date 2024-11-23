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
import androidx.fragment.app.FragmentTransaction;

import com.example.planetze.R;

public class CalendarTabFragment extends Fragment {
    //TODO: change the code to be for Calender section
    private TrackerTabFragment.OnTrackerTabInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TrackerTabFragment.OnTrackerTabInteractionListener) {
            mListener = (TrackerTabFragment.OnTrackerTabInteractionListener) context; // Cast context to the interface
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
        TextView totalEmission = view.findViewById(R.id.total_emission_value);
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
}
