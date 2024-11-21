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

//View
public class LogTransportationFragment extends Fragment {
    private EditText editTextDistanceDriven, editTextTransportTime, editTextDistanceWalked
            , editTextNumFlight;
    private Spinner spinnerTransportActivity, spinnerTransportType, spinnerHaul;
    private TextView labelDistanceDriven, labelTransportType, labelTransportTime, labelDistanceWalked
            , labelNumFlight, labelHaul;
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
        labelDistanceDriven = view.findViewById(R.id.labelDistanceDriven);
        editTextTransportTime = view.findViewById(R.id.editTextTransportTime);
        labelTransportTime = view.findViewById(R.id.labelTransportTime);
        editTextDistanceWalked = view.findViewById(R.id.editTextDistanceWalked);
        labelDistanceWalked = view.findViewById(R.id.labelDistanceWalked);
        editTextNumFlight = view.findViewById(R.id.editTextNumFlight);
        labelNumFlight = view.findViewById(R.id.labelNumFlight);
        spinnerTransportActivity = view.findViewById(R.id.spinnerTransportActivity);
        spinnerTransportType = view.findViewById(R.id.spinnerTransportType);
        labelTransportType = view.findViewById(R.id.labelTransportType);
        spinnerHaul = view.findViewById(R.id.spinnerHaul);
        labelHaul = view.findViewById(R.id.labelHaul);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        db = FirebaseDatabase.getInstance("https://planetze-g16-default-rtdb.firebaseio.com/");

        // Hide some of the fields initially
        editTextDistanceDriven.setVisibility(View.GONE);
        labelDistanceDriven.setVisibility(View.GONE);
        editTextTransportTime.setVisibility(View.GONE);
        labelTransportTime.setVisibility(View.GONE);
        editTextDistanceWalked.setVisibility(View.GONE);
        labelDistanceWalked.setVisibility(View.GONE);
        editTextNumFlight.setVisibility(View.GONE);
        labelNumFlight.setVisibility(View.GONE);
        spinnerTransportType.setVisibility(View.GONE);
        labelTransportType.setVisibility(View.GONE);
        spinnerHaul.setVisibility(View.GONE);
        labelHaul.setVisibility(View.GONE);
        buttonAdd.setVisibility(View.GONE);

        // Set up the spinner with categories
        //TODO: figure out where and how to change the view based on choice chosen from spinner
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
                String selectedActivity = parent.getSelectedItem().toString();

                switch (selectedActivity) {
                    case "Select an activity":
                        // if the placeholder is chosen again, hide all other fields
                        editTextDistanceDriven.setVisibility(View.GONE);
                        labelDistanceDriven.setVisibility(View.GONE);
                        editTextTransportTime.setVisibility(View.GONE);
                        labelTransportTime.setVisibility(View.GONE);
                        editTextDistanceWalked.setVisibility(View.GONE);
                        labelDistanceWalked.setVisibility(View.GONE);
                        editTextNumFlight.setVisibility(View.GONE);
                        labelNumFlight.setVisibility(View.GONE);
                        spinnerTransportType.setVisibility(View.GONE);
                        labelTransportType.setVisibility(View.GONE);
                        spinnerHaul.setVisibility(View.GONE);
                        labelHaul.setVisibility(View.GONE);
                        buttonAdd.setVisibility(View.GONE);
                        return;
                    // Show specific fields based on actual selection
                    case "Drive Personal Vehicle":
                        editTextDistanceDriven.setVisibility(View.VISIBLE);
                        labelDistanceDriven.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                    case "Take public transportation":
                        spinnerTransportType.setVisibility(View.VISIBLE);
                        editTextTransportTime.setVisibility(View.VISIBLE);
                        labelTransportType.setVisibility(View.VISIBLE);
                        labelTransportTime.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                    case "Cycling or walking":
                        editTextDistanceWalked.setVisibility(View.VISIBLE);
                        labelDistanceWalked.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                    case "Flight":
                        editTextNumFlight.setVisibility(View.VISIBLE);
                        spinnerHaul.setVisibility(View.VISIBLE);
                        labelNumFlight.setVisibility(View.VISIBLE);
                        labelHaul.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed here; fields are hidden by default
            }
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

    private void addItem() {
        String transportActivity = spinnerTransportActivity.getSelectedItem().toString().toLowerCase();
        String distanceDrivenStr, transportTimeStr, distanceWalkedStr,
                numFlightStr, transportType, haul;
        double distanceDriven, transportTime, distanceWalked;
        int numFlight;

        if (editTextDistanceDriven.getVisibility() == View.VISIBLE) {
            distanceDrivenStr = editTextDistanceDriven.getText().toString().trim();
            // check if user input is empty
            if (distanceDrivenStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out distance driven",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // if not empty, convert it to a double
            try {
                distanceDriven = Double.parseDouble(distanceDrivenStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid distance driven",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            distanceDriven = 0;
        }

        if (editTextTransportTime.getVisibility() == View.VISIBLE) {
            transportTimeStr = editTextTransportTime.getText().toString().trim();
            // check if user input is empty
            if (transportTimeStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out transport time",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // if not empty, turn it into double
            try {
                transportTime = Double.parseDouble(transportTimeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid time",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            transportTime = 0;
        }

        if (editTextDistanceWalked.getVisibility() == View.VISIBLE) {
            distanceWalkedStr = editTextDistanceWalked.getText().toString().trim();
            if (distanceWalkedStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out distance walked or cycled",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                distanceWalked = Double.parseDouble(distanceWalkedStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid distance walked/cycled",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            distanceWalked = 0;
        }

        if (editTextNumFlight.getVisibility() == View.VISIBLE) {
            numFlightStr = editTextNumFlight.getText().toString().trim();
            if (numFlightStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out number of flight",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                numFlight = Integer.parseInt(numFlightStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid number of flights",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            numFlight = 0;
        }

        if (spinnerTransportType.getVisibility() == View.VISIBLE) {
            transportType = spinnerTransportType.getSelectedItem().toString().toLowerCase();
            if (transportType.equals("select a transport type")) {
                Toast.makeText(getContext(), "Please choose a transport type",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            transportType = "";
        }

        if (spinnerHaul.getVisibility() == View.VISIBLE) {
            haul = spinnerHaul.getSelectedItem().toString().toLowerCase();
            if (haul.equals("select the haul type")) {
                Toast.makeText(getContext(), "Please choose a haul type",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            haul = "";
        }

        // TODO: check or create a path for storing those info
        itemsRef = db.getReference("daily_emission/transportation");
        // TODO: figure out how to get current date (and generate an id) to then create key-value
        //  pair
        // TODO: if we can use date as the key?
        String date_id = itemsRef.push().getKey();
        // TODO: see if this is the structure of database we want
        // TODO: see if we can modify the items instead if already exist an item with this
        //  specific date
        TransportModel item = new TransportModel(date_id, distanceDriven, transportType,
                transportTime, distanceWalked, numFlight, haul);

        itemsRef.child(date_id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
