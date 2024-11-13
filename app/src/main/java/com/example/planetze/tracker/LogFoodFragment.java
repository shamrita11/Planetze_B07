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

        // Set up the spinner with categories
        ArrayAdapter<CharSequence> FoodActivityAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.food_activity, android.R.layout.simple_spinner_item);
        FoodActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodActivity.setAdapter(FoodActivityAdapter);

        ArrayAdapter<CharSequence> FoodTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.food_type, android.R.layout.simple_spinner_item);
        FoodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodType.setAdapter(FoodTypeAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        return view;
    }

    private void addItem() {
        String numServingStr = editTextNumServing.getText().toString().trim();
        String foodActivity = spinnerFoodActivity.getSelectedItem().toString().toLowerCase();
        String foodType = spinnerFoodType.getSelectedItem().toString().toLowerCase();

        // check if the fields are empty. If they are, raise a message to the user
        if (numServingStr.isEmpty() || foodActivity.isEmpty() || foodType.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // if the fields have been filled out, turn certain fields into double / int as fit
        int numServing;
        try {
            numServing = Integer.parseInt(numServingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers in all fields", Toast.LENGTH_SHORT).show();
            return;  // Stop the function if parsing fails
        }

        itemsRef = db.getReference("categories/food");
        String id = itemsRef.push().getKey();
        Item item = new Item(id, numServing, foodActivity, foodType);

        itemsRef.child(id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class LogTransportationFragment extends Fragment {
        private EditText editTextDistanceDriven, editTextTransportTime, editTextDistanceWalked, editTextNumFlight;
        private Spinner spinnerTransportActivity, spinnerTransportType, spinnerHaul;
        private Button buttonAdd;
        private FirebaseDatabase db;
        private DatabaseReference itemsRef;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //get view (TODO: change the xml file name to the correct one)
            View view = inflater.inflate(R.layout.fragment_log_transport, container, false);

            // retrieving the corresponding view by id
            editTextDistanceDriven = view.findViewById(R.id.editTextDistanceDriven);
            editTextTransportTime = view.findViewById(R.id.editTextTransportTime);
            editTextDistanceWalked = view.findViewById(R.id.editTextDistanceWalked);
            editTextNumFlight = view.findViewById(R.id.editTextNumFlight);
            spinnerTransportActivity = view.findViewById(R.id.spinnerTransportActivity);
            spinnerTransportType = view.findViewById(R.id.spinnerTransportType);
            // TODO: when creating this xml object, remember to indicate what short haul and long haul
            //  mean (i.e. short haul means <1500 km) in the text above spinner
            spinnerHaul = view.findViewById(R.id.spinnerHaul);
            buttonAdd = view.findViewById(R.id.buttonAdd);

            db = FirebaseDatabase.getInstance("https://planetze-g16-default-rtdb.firebaseio.com/");

            // Hide all other text fields, spinners and buttons initially
            editTextDistanceDriven.setVisibility(View.GONE);
            editTextTransportTime.setVisibility(View.GONE);
            editTextDistanceWalked.setVisibility(View.GONE);
            editTextNumFlight.setVisibility(View.GONE);
            spinnerTransportType.setVisibility(View.GONE);
            spinnerHaul.setVisibility(View.GONE);
            buttonAdd.setVisibility(View.GONE);

            // Set up the spinner with categories
            ArrayAdapter<CharSequence> TransportActivityAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.transport_activity, android.R.layout.simple_spinner_item);
            TransportActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTransportActivity.setAdapter(TransportActivityAdapter);

            ArrayAdapter<CharSequence> TransportTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.transport_type, android.R.layout.simple_spinner_item);
            TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTransportType.setAdapter(TransportTypeAdapter);

            ArrayAdapter<CharSequence> HaulAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.haul_type, android.R.layout.simple_spinner_item);
            HaulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerHaul.setAdapter(HaulAdapter);

            // onItemSelected for spinnerTransportActivity
            spinnerTransportActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedActivity = spinnerTransportActivity.getSelectedItem().toString();

                    if (selectedActivity.equals("Select an activity")) {
                        editTextDistanceDriven.setText("");
                        editTextTransportTime.setText("");
                        editTextDistanceWalked.setText("");
                        editTextNumFlight.setText("");
                        spinnerTransportType.setSelection(0);
                        spinnerHaul.setSelection(0);
                        return;
                    }

                    if (selectedActivity.equals("Drive personal vehicle")) {
                        editTextDistanceDriven.setVisibility(View.VISIBLE);
                    } else if (selectedActivity.equals())
                        case "Take public transportation":
                            spinnerTransportType.setVisibility(View.VISIBLE);
                            editTextTransportTime.setVisibility(View.VISIBLE);
                            break;

                        case "cycling or walking":
                            editTextDistanceWalked.setVisibility(View.VISIBLE);
                            break;

                        case "flight":
                            editTextNumFlight.setVisibility(View.VISIBLE);
                            spinnerHaul.setVisibility(View.VISIBLE);
                            break;
                    }
                    buttonAdd.setVisibility(View.VISIBLE);
            };

            });

            // onclick for the button
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItem();
                }
            });

            return view;
        }
}