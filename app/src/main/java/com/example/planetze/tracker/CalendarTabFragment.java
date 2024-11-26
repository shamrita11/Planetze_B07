package com.example.planetze.tracker;

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

import com.example.planetze.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Locale;

public class CalendarTabFragment extends Fragment {
    //TODO: change the code to be for Calender section
    private TrackerTabFragment.OnTrackerTabInteractionListener mListener;
    private DailyEmissionProcessor processor;
    private String date;

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

        Button buttonTransportation = view.findViewById(R.id.buttonTransportation);
        Button buttonFood = view.findViewById(R.id.buttonFood);
        Button buttonConsumption = view.findViewById(R.id.buttonConsumption);
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        TextView activityList = view.findViewById(R.id.activity_list);
        String userId = "user1"; // switch to actual user id

        // render calendar and date selection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            calendarView.setDateTextAppearance(R.style.CalendarDayTextStyle);
        }

        buttonTransportation.setEnabled(false);
        buttonFood.setEnabled(false);
        buttonConsumption.setEnabled(false);

        // listener for date selection to customize selected date background and text color
        calendarView.setOnDateChangeListener((calendarView1, year, month, dayOfMonth) -> {
            date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            Toast.makeText(getContext(), "Selected Date: " + date, Toast.LENGTH_SHORT).show();
            updateActivityList(date, userId, activityList);

            buttonTransportation.setEnabled(true);
            buttonFood.setEnabled(true);
            buttonConsumption.setEnabled(true);
        });

        // handle the click of the three log buttons
        // We make assumption:
        // if user is updating data from the calender section, we assume that the data they enter is
        // the updated data for that specific activity. So, we overwrite the old data for this
        // activity in the database and replace with their newly entered data. (This is the
        // editing functionality as mentioned in the requirement)
        // TODO: check if the isIncrement variable is working as intended
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

    /**
     * This method updates the text of the textView activity_list with data that were logged
     * by the user on the specific date.
     * @param dateKey is the chosen date, used to access the node within database
     * @param userId is the user's id, used to access the current user's data from database
     * @param activityList is the textview for which we update the text of
     */
    private void updateActivityList(String dateKey, String userId, TextView activityList) {
        DatabaseReference commonRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId);
        DatabaseReference dailyRef = commonRef.child("daily_emission").child(dateKey);

        dailyRef.get().addOnCompleteListener(task -> {
            // Separate isSuccessful and getResult
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot dateSnapshot = task.getResult();
                StringBuilder activityListBuilder = new StringBuilder();

                // Section: Transportation
                DataSnapshot transportationSnapshot = dateSnapshot.child("transportation");
                DataSnapshot emissionSnapshot = dateSnapshot.child("emission");
                if (transportationSnapshot.exists()) {
                    for (DataSnapshot activitySnapshot : transportationSnapshot.getChildren()) {
                        String activityKey = activitySnapshot.getKey(); // e.g., "drive_personal_vehicle"
                        assert activityKey != null;
                        String activityName = activityKey.replace("_", " ");
                        double co2e = emissionSnapshot.child(activityKey).getValue(Double.class); // CO2e from emissions
                        activityListBuilder.append(activityName)
                                .append(" - ")
                                .append(String.format(Locale.getDefault(), "%.2f", co2e))
                                .append(" kg CO2e\n");

                        // Details for each activity
                        for (DataSnapshot detailSnapshot : activitySnapshot.getChildren()) {
                            String detailKey = detailSnapshot.getKey(); // e.g., "distance_driven"
                            Object detailValue = detailSnapshot.getValue();
                            activityListBuilder.append("\t").append("• ")
                                    .append(detailKey.replace("_", " "))
                                    .append(": ")
                                    .append(detailValue)
                                    .append(activityKey.equals("drive_personal_vehicle") ? " km" : "")
                                    .append(activityKey.equals("take_public_transportation") ? " hours" : "")
                                    .append(activityKey.equals("cycling_or_walking") ? " km" : "")
                                    .append(activityKey.equals("flight") ? " flight" : "")
                                    .append("\n");
                        }

                        activityListBuilder.append("\n");
                    }
                }

                // Section: Consumption
                DataSnapshot consumptionSnapshot = dateSnapshot.child("consumption");
                if (consumptionSnapshot.exists()) {
                    for (DataSnapshot activitySnapshot : consumptionSnapshot.getChildren()) {
                        String activityKey = activitySnapshot.getKey(); // e.g., "buy_electronics"
                        String activityName = activityKey.replace("_", " ");
                        double co2e = emissionSnapshot.child(activityKey).getValue(Double.class);
                        activityListBuilder.append(activityName)
                                .append(" - ")
                                .append(String.format(Locale.getDefault(), "%.2f", co2e))
                                .append(" kg CO2e\n");

                        // Details for each activity
                        for (DataSnapshot detailSnapshot : activitySnapshot.getChildren()) {
                            String detailKey = detailSnapshot.getKey(); // e.g., "smartphone"
                            Object detailValue = detailSnapshot.getValue();
                            activityListBuilder.append("\t").append("• ")
                                    .append(detailKey.replace("_", " "))
                                    .append(": ")
                                    .append(detailValue)
                                    .append(" purchased\n");
                        }

                        activityListBuilder.append("\n");
                    }
                }

                // Section: Food
                DataSnapshot foodSnapshot = dateSnapshot.child("food");
                if (foodSnapshot.exists()) {
                    double co2e = emissionSnapshot.child("food").getValue(Double.class);
                    activityListBuilder.append("food - ")
                            .append(String.format(Locale.getDefault(), "%.2f", co2e))
                            .append(" kg CO2e\n");

                    for (DataSnapshot activitySnapshot : foodSnapshot.getChildren()) {
                        String foodKey = activitySnapshot.getKey(); // e.g., "chicken"
                        Object servings = activitySnapshot.getValue();
                        activityListBuilder.append("\t").append("• ")
                                .append(foodKey)
                                .append(": ")
                                .append(servings)
                                .append(" servings\n");
                    }

                    activityListBuilder.append("\n");
                }

                if(activityListBuilder.toString().equals("")) {
                    activityList.setText(getString(R.string.no_activity_logged));
                } else {
                    activityList.setText(activityListBuilder.toString());
                }
            } else if (!task.getResult().exists()) {
                activityList.setText(getString(R.string.no_activity_logged));
            } else {
                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
