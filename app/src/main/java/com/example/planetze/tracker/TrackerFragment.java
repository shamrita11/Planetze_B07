package com.example.planetze.tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.planetze.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class TrackerFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.eco_tracker_base, container, false);

        // Find TabLayout and ViewPager2 from the layout
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Set up ViewPager2 with fragments for the tabs
        setupViewPager();

        return view;
    }

    // Initializes the ViewPager2 and set an adapter to link the tabs with their fragments
    private void setupViewPager() {
        // Create a list of fragments and titles for the tabs
        List<Fragment> fragments = new ArrayList<>();
        List<String> tabTitles = new ArrayList<>();
        List<String> tabDescriptions = new ArrayList<>();

        // Add fragments, titles, and descriptions
        fragments.add(new TrackerTabFragment());  // Fragment for "Tracker" tab
        tabTitles.add("Tracker");
        tabDescriptions.add("Navigate to the Tracker tab to log your activities");

        fragments.add(new CalendarTabFragment()); // Fragment for "Calendar" tab
        tabTitles.add("Calendar");
        tabDescriptions.add("View your activity calendar in the Calendar tab");

        // TODO: why is there no Habits tab?
        fragments.add(new HabitsTabFragment());   // Fragment for "Habits" tab
        tabTitles.add("Habits");
        tabDescriptions.add("Track and manage habits in the Habits tab");

        // Set up the adapter
        TrackerViewPagerAdapter adapter = new TrackerViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Attach TabLayout to ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles.get(position)); // Set tab title
            tab.setContentDescription(tabDescriptions.get(position)); // Set accessibility description
        }).attach();
    }
}
