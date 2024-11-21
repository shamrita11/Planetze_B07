package com.example.planetze.tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planetze.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LogFoodFragment extends Fragment {
    private EditText editTextNumServing;
    private Spinner spinnerFoodActivity, spinnerFoodType;
    private Button buttonAdd, buttonBack;
    private TextView labelFoodType, labelNumServing;


    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);
        View includedView = view.findViewById(R.id.includedBackButton);

        editTextNumServing = view.findViewById(R.id.editTextNumServing);
        spinnerFoodActivity = view.findViewById(R.id.spinnerFoodActivity);
        spinnerFoodType = view.findViewById(R.id.spinnerFoodType);
        labelFoodType = view.findViewById(R.id.labelFoodType);
        labelNumServing = view.findViewById(R.id.labelNumServing);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonBack = includedView.findViewById(R.id.buttonBack);

        db = FirebaseDatabase.getInstance("https://planetze-g16-default-rtdb.firebaseio.com/");

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

        // TODO: fix this back button (NullPointerError)
        // buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void addItem() {
        String foodActivity = spinnerFoodActivity.getSelectedItem().toString().toLowerCase();
        String numServingStr, foodType;
        int numServing;

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
            foodType = spinnerFoodType.getSelectedItem().toString().toLowerCase();
            if (foodType.equals("select the food type")) {
                Toast.makeText(getContext(), "Please choose the food type",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            foodType = "";
        }

        // TODO: replace with dynamic date generation
        // Store data under userId > daily_emission > date > food > meal
        String userId = "user1";
        // String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dateKey = "2024-11-19";

        itemsRef = db.getReference(userId);

        // user1 > daily_emission > 2024-11-19 > food > meal > foodType: numServing
        DatabaseReference foodRef = itemsRef.child("daily_emission").child(dateKey)
                .child("food").child(foodActivity);

        // Check if the food type path exists (to create it if not)
        foodRef.child(foodType).get().addOnCompleteListener(foodTask -> {
            if (foodTask.isSuccessful()) {
                if (foodTask.getResult().exists()) {
                    // If the food activity already exists, get the current value and add to it
                    int existingNumServing = foodTask.getResult().getValue(Integer.class);
                    int newNumServing = existingNumServing + numServing;  // Adding to the existing value
                    foodRef.child(foodType).setValue(newNumServing);
                    Toast.makeText(getContext(), "Your food data was updated", Toast.LENGTH_SHORT).show();
                } else {
                    // If the food type oath doesn't exist, create this whole path
                    foodRef.child(foodType).setValue(numServing);
                    Toast.makeText(getContext(), "New food data was logged", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle Firebase request failure
                Toast.makeText(getContext(), "Failed to fetch food data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
