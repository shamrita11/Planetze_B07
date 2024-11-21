package com.example.planetze.tracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.planetze.R;
import com.example.planetze.ScrollerFragment;
import com.example.planetze.SpinnerFragment;

public class TrackerTabFragment extends Fragment {

    public interface OnTrackerTabInteractionListener {
        void onFoodButtonClicked();
        void onTransportationButtonClicked();
        void onConsumptionButtonClicked();
    }

    private OnTrackerTabInteractionListener mListener;

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
        Button buttonTransportation = view.findViewById(R.id.buttonTransportation);
        Button buttonFood = view.findViewById(R.id.buttonFood);
        Button buttonConsumption = view.findViewById(R.id.buttonConsumption);

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
