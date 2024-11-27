package com.example.planetze.tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planetze.R;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarTabFragment extends Fragment {
    //TODO: change the code to be for Calender section
    private TrackerTabFragment.OnTrackerTabInteractionListener mListener;
    private DailyEmissionProcessor processor;
    MaterialCalendarView materialCalendarView;
    Button buttonTransportation, buttonFood, buttonConsumption;
    ActivityListAdapter adapter;
    private String date;
    String userId;
    private List<ActivityItem> activityItems = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_calendar_tab, container, false);

        buttonTransportation = view.findViewById(R.id.buttonTransportation);
        buttonFood = view.findViewById(R.id.buttonFood);
        buttonConsumption = view.findViewById(R.id.buttonConsumption);
        materialCalendarView = view.findViewById(R.id.materialCalendarView);
        RecyclerView recyclerView = view.findViewById(R.id.activity_list);
        userId = "user1"; // switch to actual user id
        // render calendar and date selection
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            calendarView.setDateTextAppearance(R.style.CalendarDayTextStyle);
//        }

        // TODO: change the disabled color of buttons
        buttonTransportation.setEnabled(false);
        buttonFood.setEnabled(false);
        buttonConsumption.setEnabled(false);

        // listener for date selection to customize selected date background and text color
//        calendarView.setOnDateChangeListener((calendarView1, year, month, dayOfMonth) -> {
//            date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
//            Toast.makeText(getContext(), "Selected Date: " + date, Toast.LENGTH_SHORT).show();
//            updateActivityList();
//
//            buttonTransportation.setEnabled(true);
//            buttonFood.setEnabled(true);
//            buttonConsumption.setEnabled(true);
//        });

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            materialCalendarView.removeDecorators();

            if (selected) {
                // Apply the background and text color for the newly selected date
                materialCalendarView.addDecorator(new SelectedDateDecorator(date, getContext()));
            }

            int year = date.getYear();
            int month = date.getMonth();
            int day = date.getDay();
            this.date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);

            Toast.makeText(getContext(), "Selected Date: " + this.date, Toast.LENGTH_SHORT).show();

            // Update activity list or perform other actions
            updateActivityList();

            // Enable buttons after a date is selected
            buttonTransportation.setEnabled(true);
            buttonFood.setEnabled(true);
            buttonConsumption.setEnabled(true);
        });

        adapter = new ActivityListAdapter(activityItems, (activityType, activityKey, detailKey) -> {
            // Handle the button click for the specific detail
            Toast.makeText(getContext(), "Deleting Activity...", Toast.LENGTH_SHORT).show();

            DatabaseReference detailRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId).child("daily_emission").child(date)
                    .child(activityType).child(activityKey).child(detailKey);

            detailRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Detail deleted successfully.", Toast.LENGTH_SHORT).show();
                    updateActivityList(); // Refresh the data
                    processor = new DailyEmissionProcessor(getContext(), date, () -> {
                            processor.mainUploader();  // Upload data
                    });
                } else {
                    Toast.makeText(getContext(), "Failed to delete detail.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // We make assumption:
        // if user is updating data from the calender section, we assume that the data they enter is
        // the updated data for that specific activity. So, we overwrite the old data for this
        // activity in the database and replace with their newly entered data. (This is the
        // editing functionality as mentioned in the requirement)
        buttonTransportation.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTransportationButtonClicked(false, date);
            }
        });

        buttonFood.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFoodButtonClicked(false, date);
            }
        });

        buttonConsumption.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onConsumptionButtonClicked(false, date);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        materialCalendarView.clearSelection();
        activityItems.clear();
        buttonTransportation.setEnabled(false);
        buttonFood.setEnabled(false);
        buttonConsumption.setEnabled(false);
    }

    /**
     * This method gets data from database to populate activityItems for use by recycler view
     */
    private void updateActivityList() {
        activityItems.clear();

        DatabaseReference commonRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId);
        DatabaseReference dailyRef = commonRef.child("daily_emission").child(date);

        dailyRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot dateSnapshot = task.getResult();

                // Section: Transportation
                DataSnapshot transportationSnapshot = dateSnapshot.child("transportation");
                DataSnapshot emissionSnapshot = dateSnapshot.child("emission");
                if (transportationSnapshot.exists()) {
                    String activityType = "transportation";
                    StringBuilder stringBuilder = new StringBuilder();

                    for (DataSnapshot activitySnapshot : transportationSnapshot.getChildren()) {
                        String activityKey = activitySnapshot.getKey(); // e.g., "drive_personal_vehicle"
                        String activityName = activityKey.replace("_", " ");
                        double co2eRaw = emissionSnapshot.child(activityKey).getValue(Double.class); // CO2e from emissions
                        String co2e = String.format(Locale.getDefault(), "%.2f", co2eRaw);
                        Map<String, String> detailMap = new HashMap<>();

                        // Details for each activity
                        for (DataSnapshot detailSnapshot : activitySnapshot.getChildren()) {
                            String detailKey = detailSnapshot.getKey(); // e.g., "distance_driven"
                            Object detailValue = detailSnapshot.getValue(); // e.g. 1.5
                            stringBuilder.append(detailValue)
                                    .append(activityKey.equals("drive_personal_vehicle") ? " km" : "")
                                    .append(activityKey.equals("take_public_transportation") ? " hours" : "")
                                    .append(activityKey.equals("cycling_or_walking") ? " km" : "")
                                    .append(activityKey.equals("flight") ? " flight" : "")
                                    .append("\n");
                            String details = stringBuilder.toString();
                            detailMap.put(detailKey, details);
                            stringBuilder.setLength(0);
                        }

                        activityItems.add(new ActivityItem(activityType, activityName, co2e, detailMap));
                    }
                }

                // Section: Consumption
                DataSnapshot consumptionSnapshot = dateSnapshot.child("consumption");
                if (consumptionSnapshot.exists()) {
                    String activityType = "consumption";
                    StringBuilder stringBuilder = new StringBuilder();

                    for (DataSnapshot activitySnapshot : consumptionSnapshot.getChildren()) {
                        String activityKey = activitySnapshot.getKey(); // e.g., "buy_electronics"
                        String activityName = activityKey.replace("_", " ");
                        double co2eRaw = emissionSnapshot.child(activityKey).getValue(Double.class);
                        String co2e = String.format(Locale.getDefault(), "%.2f", co2eRaw);
                        Map<String, String> detailMap = new HashMap<>();

                        // Details for each activity
                        for (DataSnapshot detailSnapshot : activitySnapshot.getChildren()) {
                            String detailKey = detailSnapshot.getKey(); // e.g., "smartphone"
                            Object detailValue = detailSnapshot.getValue();
                            stringBuilder.append(detailValue).append(" purchased\n");
                            String details = stringBuilder.toString();
                            detailMap.put(detailKey, details);
                            stringBuilder.setLength(0);
                        }

                        activityItems.add(new ActivityItem(activityType, activityName, co2e, detailMap));
                    }
                }

                // Section: Food
                DataSnapshot foodSnapshot = dateSnapshot.child("food");
                if (foodSnapshot.exists()) {
                    String activityType = "food";
                    StringBuilder stringBuilder = new StringBuilder();

                    for (DataSnapshot activitySnapshot : foodSnapshot.getChildren()) {
                        String activityKey = activitySnapshot.getKey(); // e.g., "meal"
                        String activityName = activityKey.replace("_", " ");
                        double co2eRaw = emissionSnapshot.child("food").getValue(Double.class);
                        String co2e = String.format(Locale.getDefault(), "%.2f", co2eRaw);
                        Map<String, String> detailMap = new HashMap<>();

                        // Details for each activity
                        for (DataSnapshot detailSnapshot : activitySnapshot.getChildren()) {
                            String detailKey = detailSnapshot.getKey(); // e.g., "beef"
                            Object detailValue = detailSnapshot.getValue(); // e.g., 1
                            stringBuilder.append(detailValue).append(" servings\n");
                            String details = stringBuilder.toString();
                            detailMap.put(detailKey, details);
                            stringBuilder.setLength(0);
                        }

                        activityItems.add(new ActivityItem(activityType, activityName, co2e, detailMap));
                    }
                }
            } else if (!task.getResult().exists()) {
                activityItems.add(new ActivityItem(null
                        , "No activity logged for this day", "0", null));
            } else {
                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }

            // Handle empty cases and notify the adapter
            if (activityItems.isEmpty()) {
                activityItems.add(new ActivityItem(null
                        , "No activity logged for this day", "0", null));
            }
            adapter.notifyDataSetChanged();
        });

    }

//    // TODO: see if this works (understand the code
//    private void showDeleteActivityDialog(String dateKey, String userId, TextView activityList
//            , Button buttonDeleteActivity) {
//        DatabaseReference dailyRef = FirebaseDatabase.getInstance().getReference("users")
//                .child(userId).child("daily_emission").child(dateKey);
//
//        dailyRef.child("emission").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult().exists()) {
//                DataSnapshot emissionSnapshot = task.getResult();
//                List<String> activities = new ArrayList<>();
//                List<String> activityKeys = new ArrayList<>();
//
//                // Fetch activity names and keys
//                for (DataSnapshot child : emissionSnapshot.getChildren()) {
//                    String activityKey = child.getKey();
//                    double co2e = child.getValue(Double.class);
//                    activities.add(activityKey.replace("_", " ") + " - " +
//                            String.format(Locale.getDefault(), "%.2f", co2e) + " kg CO2e");
//                    activityKeys.add(activityKey);
//                }
//
//                // Show dialog with options
//                new AlertDialog.Builder(getContext())
//                        .setTitle("Select Activity to Delete")
//                        .setItems(activities.toArray(new String[0]), (dialog, which) -> {
//                            String selectedActivityKey = activityKeys.get(which);
//                            deleteActivity(dateKey, userId, selectedActivityKey, activityList, buttonDeleteActivity);
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .show();
//            } else {
//                Toast.makeText(getContext(), "No activities to delete for this date.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // TODO: understand this
//    private void deleteActivity(String dateKey, String userId, String activityKey
//            , TextView activityList, Button buttonDeleteActivity) {
//        DatabaseReference dailyRef = FirebaseDatabase.getInstance().getReference("users")
//                .child(userId).child("daily_emission").child(dateKey);
//
//        // Remove the activity node and emission entry
//        dailyRef.child("transportation").child(activityKey).removeValue();
//        dailyRef.child("consumption").child(activityKey).removeValue();
//        dailyRef.child("food").child(activityKey).removeValue();
//        dailyRef.child("emission").child(activityKey).removeValue()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getContext(), "Activity deleted successfully.", Toast.LENGTH_SHORT).show();
//
//                        // Recalculate emissions
//                        if (processor == null) {
//                            processor = new DailyEmissionProcessor(getContext(), dateKey, () -> {
//                                processor.mainUploader();
//                                updateActivityList(dateKey, userId, activityList, buttonDeleteActivity); // Update UI
//                            });
//                        } else {
//                            processor.mainUploader();
//                            updateActivityList(dateKey, userId, activityList, buttonDeleteActivity); // Update UI
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Failed to delete activity.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}
