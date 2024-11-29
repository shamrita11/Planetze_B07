package com.example.planetze.tracker;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planetze.R;
import com.example.planetze.UserSession;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LogFoodFragment extends Fragment {
    private EditText editTextNumServing;
    private Spinner spinnerFoodActivity, spinnerFoodType;
    private Button buttonAdd;
    private ImageButton buttonBack;
    private TextView labelFoodType, labelNumServing;
    DailyEmissionProcessor processor;
    private final boolean isIncrement;
    private final String dateKey;

    public LogFoodFragment(boolean isIncrement, String date) {
        this.isIncrement = isIncrement;
        this.dateKey = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);
        View includedView = view.findViewById(R.id.includedButtonBack);

        editTextNumServing = view.findViewById(R.id.editTextNumServing);
        spinnerFoodActivity = view.findViewById(R.id.spinnerFoodActivity);
        spinnerFoodType = view.findViewById(R.id.spinnerFoodType);
        labelFoodType = view.findViewById(R.id.labelFoodType);
        labelNumServing = view.findViewById(R.id.labelNumServing);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonBack = includedView.findViewById(R.id.buttonBack);

        // Hide some of the fields initially
        editTextNumServing.setVisibility(View.GONE);
        spinnerFoodType.setVisibility(View.GONE);
        labelFoodType.setVisibility(View.GONE);
        labelNumServing.setVisibility(View.GONE);
        buttonAdd.setVisibility(View.GONE);

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> FoodActivityAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.food_activity, android.R.layout.simple_spinner_item);
        FoodActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodActivity.setAdapter(FoodActivityAdapter);

        ArrayAdapter<CharSequence> FoodTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.food_type, android.R.layout.simple_spinner_item);
        FoodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodType.setAdapter(FoodTypeAdapter);

        // onItemSelected for spinnerTransportActivity
        spinnerFoodActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedActivity = parent.getSelectedItem().toString();

                switch (selectedActivity) {
                    case "Select an activity":
                        // if the placeholder is chosen again, hide all other fields
                        editTextNumServing.setVisibility(View.GONE);
                        spinnerFoodType.setVisibility(View.GONE);
                        labelFoodType.setVisibility(View.GONE);
                        labelNumServing.setVisibility(View.GONE);
                        buttonAdd.setVisibility(View.GONE);
                        return;
                    // Show specific fields based on actual selection
                    case "Meal":
                        editTextNumServing.setVisibility(View.VISIBLE);
                        spinnerFoodType.setVisibility(View.VISIBLE);
                        labelFoodType.setVisibility(View.VISIBLE);
                        labelNumServing.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed here; fields are hidden by default
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void addItem() {
        String foodActivity = spinnerFoodActivity.getSelectedItem().toString().toLowerCase();
        String numServingStr, foodType;
        int numServing;
        //TODO: make sure numbers are positive (input)
        if (editTextNumServing.getVisibility() == View.VISIBLE) {
            numServingStr = editTextNumServing.getText().toString().trim();
            // check if user input is empty
            if (numServingStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out number of servings",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // if not empty, convert it to a int
            try {
                numServing = Integer.parseInt(numServingStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid number of servings",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            numServing = 0;
        }

        if (spinnerFoodType.getVisibility() == View.VISIBLE) {
            foodType = spinnerFoodType.getSelectedItem().toString().toLowerCase().replace("-", "_");
            if (foodType.equals("select the food type")) {
                Toast.makeText(getContext(), "Please choose the food type",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            foodType = "";
        }

        // Store data into database
        // Store data under userId > daily_emission > date > food > meal
        FirebaseManager manager = new FirebaseManager(getContext());
        // String userId = "user1";
        // String dateKey = GetDate.getDate();
        //String dateKey = "2024-11-19";

        // user1 > daily_emission > 2024-11-19 > food > meal > chicken: 1
        String foodRefPath = "users/" + UserSession.userId + "/daily_emission/" + dateKey
                + "/food/meal/" + foodType;
        manager.updateNode(foodRefPath, numServing, isIncrement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uploadData(true); // Upload only after successful update
                    } else {
                        Toast.makeText(getContext(), "Failed to log food data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadData(boolean forceRefresh) {
        if (processor == null || forceRefresh) {
            processor = new DailyEmissionProcessor(getContext(), dateKey, () -> {
                processor.mainUploader();  // Upload food data after loading
            });
        }
    }
}
