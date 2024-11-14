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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planetze.Item;
import com.example.planetze.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogFoodFragment extends Fragment {
    private EditText editTextNumServing;
    private Spinner spinnerFoodActivity, spinnerFoodType;
    private Button buttonAdd;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);

        editTextNumServing = view.findViewById(R.id.editTextNumServing);
        spinnerFoodActivity = view.findViewById(R.id.spinnerFoodActivity);
        spinnerFoodType = view.findViewById(R.id.spinnerFoodType);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        db = FirebaseDatabase.getInstance("https://b07-demo-summer-2024-default-rtdb.firebaseio.com/");

        // Hide some of the fields initially
        editTextNumServing.setVisibility(View.GONE);
        spinnerFoodType.setVisibility(View.GONE);
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
                        buttonAdd.setVisibility(View.GONE);
                        return;
                    // Show specific fields based on actual selection
                    case "Meal":
                        editTextNumServing.setVisibility(View.VISIBLE);
                        spinnerFoodType.setVisibility(View.VISIBLE);
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

        itemsRef = db.getReference("daily_emission/food");
        // TODO: use date as the id (key)
        String date_id = itemsRef.push().getKey();
        FoodModel item = new FoodModel(date_id, numServing, foodType);

        itemsRef.child(date_id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
