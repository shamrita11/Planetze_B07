package com.example.planetze.tracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.planetze.BaseActivity;
import com.example.planetze.R;
import com.google.android.material.tabs.TabLayout;

public class TrackerActivity extends BaseActivity implements TrackerTabFragment.OnTrackerTabInteractionListener {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_eco_tracker; // Ensure this matches your XML layout file name
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the TrackerFragment when the activity is created
        if (savedInstanceState == null) {
            loadFragment(new TrackerFragment());
        }
    }

    @Override
    public void onFoodButtonClicked(boolean isIncrement, String date) {
        // Handle navigation to LogFoodFragment
        loadFragment(new LogFoodFragment(isIncrement, date));
    }

    @Override
    public void onTransportationButtonClicked(boolean isIncrement, String date) {
        // Handle navigation to LogTransportationFragment
        loadFragment(new LogTransportationFragment(isIncrement, date));
    }

    @Override
    public void onConsumptionButtonClicked(boolean isIncrement, String date) {
        // Handle navigation to LogConsumptionFragment
        loadFragment(new LogConsumptionFragment(isIncrement, date));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
