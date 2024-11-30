package com.example.planetze.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planetze.EcoHabitActivity;
import com.example.planetze.R;

public class HabitsTabFragment extends Fragment {
    // NOTE: this is a placeholder java class for Habits section
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit_tab, container, false);
        Button btnEcoHabit = view.findViewById(R.id.btn_eco_habit);
        btnEcoHabit.setOnClickListener(v -> {
            // Intent to navigate to EcoHabitActivity
            Intent intent = new Intent(getActivity(), EcoHabitActivity.class);
            startActivity(intent);  // Launch the AdoptedHabitsActivity
        });
        return view;
    }
}
