package com.example.planetze.tracker;

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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

//View
public class LogTransportationFragment extends Fragment {
    private EditText editTextDistanceDriven, editTextTransportTime, editTextDistanceWalked
            , editTextNumFlight;
    private Spinner spinnerTransportActivity, spinnerTransportType, spinnerHaul;
    private TextView labelDistanceDriven, labelTransportType, labelTransportTime, labelDistanceWalked
            , labelNumFlight, labelHaul;
    private Button buttonAdd;
    private DailyEmissionProcessor processor;
    private final boolean isIncrement;
    private final String dateKey;

    public LogTransportationFragment(boolean isIncrement, String date) {
        this.isIncrement = isIncrement;
        this.dateKey = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get view
        View view = inflater.inflate(R.layout.fragment_log_transport, container, false);
        View includedView = view.findViewById(R.id.includedButtonBack);

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
        ImageButton buttonBack = includedView.findViewById(R.id.buttonBack);

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
        ArrayAdapter<CharSequence> TransportActivityAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.transport_activity, android.R.layout.simple_spinner_item);
        TransportActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransportActivity.setAdapter(TransportActivityAdapter);

        ArrayAdapter<CharSequence> TransportTypeAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.transport_type, android.R.layout.simple_spinner_item);
        TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransportType.setAdapter(TransportTypeAdapter);

        ArrayAdapter<CharSequence> HaulAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.haul_type, android.R.layout.simple_spinner_item);
        HaulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHaul.setAdapter(HaulAdapter);

        // onItemSelected for spinnerTransportActivity
        spinnerTransportActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedActivity = parent.getSelectedItem().toString();

                switch (selectedActivity) {
                    // if the placeholder is chosen again, hide all other fields
                    case "Select an activity":
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
                    case "Drive personal vehicle":
                        editTextDistanceDriven.setVisibility(View.VISIBLE);
                        labelDistanceDriven.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        // also make sure anything else need to be gone
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

                        break;
                    case "Take public transportation":
                        spinnerTransportType.setVisibility(View.VISIBLE);
                        editTextTransportTime.setVisibility(View.VISIBLE);
                        labelTransportType.setVisibility(View.VISIBLE);
                        labelTransportTime.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        editTextDistanceDriven.setVisibility(View.GONE);
                        labelDistanceDriven.setVisibility(View.GONE);
                        editTextDistanceWalked.setVisibility(View.GONE);
                        labelDistanceWalked.setVisibility(View.GONE);
                        editTextNumFlight.setVisibility(View.GONE);
                        labelNumFlight.setVisibility(View.GONE);
                        spinnerHaul.setVisibility(View.GONE);
                        labelHaul.setVisibility(View.GONE);

                        break;
                    case "Cycling or walking":
                        editTextDistanceWalked.setVisibility(View.VISIBLE);
                        labelDistanceWalked.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        editTextDistanceDriven.setVisibility(View.GONE);
                        labelDistanceDriven.setVisibility(View.GONE);
                        editTextTransportTime.setVisibility(View.GONE);
                        labelTransportTime.setVisibility(View.GONE);
                        editTextNumFlight.setVisibility(View.GONE);
                        labelNumFlight.setVisibility(View.GONE);
                        spinnerTransportType.setVisibility(View.GONE);
                        labelTransportType.setVisibility(View.GONE);
                        spinnerHaul.setVisibility(View.GONE);
                        labelHaul.setVisibility(View.GONE);

                        break;
                    case "Flight":
                        editTextNumFlight.setVisibility(View.VISIBLE);
                        spinnerHaul.setVisibility(View.VISIBLE);
                        labelNumFlight.setVisibility(View.VISIBLE);
                        labelHaul.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        editTextDistanceDriven.setVisibility(View.GONE);
                        labelDistanceDriven.setVisibility(View.GONE);
                        editTextTransportTime.setVisibility(View.GONE);
                        labelTransportTime.setVisibility(View.GONE);
                        editTextDistanceWalked.setVisibility(View.GONE);
                        labelDistanceWalked.setVisibility(View.GONE);
                        spinnerTransportType.setVisibility(View.GONE);
                        labelTransportType.setVisibility(View.GONE);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed here; fields are hidden by default
            }
        });

        buttonAdd.setOnClickListener(v -> {
            addItem();
            editTextDistanceDriven.setText("");
            editTextTransportTime.setText("");
            editTextDistanceWalked.setText("");
            editTextNumFlight.setText("");
            spinnerTransportType.setSelection(0);
            spinnerHaul.setSelection(0);
        });

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void addItem() {
        String transportActivity = spinnerTransportActivity.getSelectedItem().toString().toLowerCase();
        String distanceDrivenStr, transportTimeStr, distanceWalkedStr,
                numFlightStr, transportType, haul;
        double distanceDriven, transportTime, distanceWalked;
        int numFlight;

        // obtain user input
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
                if (distanceDriven < 0) {
                    Toast.makeText(getContext(), "Distance driven cannot be negative",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
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

            if (transportTimeStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out transport time",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                transportTime = Double.parseDouble(transportTimeStr);
                if (transportTime < 0) {
                    Toast.makeText(getContext(), "Transport time cannot be negative",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid time",
                        Toast.LENGTH_SHORT).show();
                return;
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
                if (distanceWalked < 0) {
                    Toast.makeText(getContext(), "Distance walked or cycled cannot be negative",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid distance walked/cycled",
                        Toast.LENGTH_SHORT).show();
                return;
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
                if (numFlight < 0) {
                    Toast.makeText(getContext(), "Number of flights cannot be negative",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid number of flights",
                        Toast.LENGTH_SHORT).show();
                return;
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

        // Log data into database
        FirebaseManager manager = new FirebaseManager(getContext());
        List<Task<Void>> tasks = new ArrayList<>();
        String userId = UserSession.getUserId(getContext());

        // userId > daily_emission > 2024-11-19 > transportation
        String commonPath = "users/" + userId + "/daily_emission/" + dateKey
                + "/transportation/";

        // ... transportation > "drive personal vehicle" > "distance_driven": 1.5
        if(transportActivity.equals("drive personal vehicle")) {
            String carPath = commonPath + "drive_personal_vehicle/distance_driven";
            tasks.add(manager.updateNode(carPath, distanceDriven, isIncrement));
        }

        // ... transportation > "take_public_transportation" > "transport_time": 1.5
        if(transportActivity.equals("take public transportation")) {
            String publicPath = commonPath + "take_public_transportation/transport_time";
            tasks.add(manager.updateNode(publicPath, transportTime, isIncrement));
        }

        // ... transportation > "cycling_or_walking" > "distance_wc": 1.5
        if(transportActivity.equals("cycling or walking")) {
            String walkPath = commonPath + "cycling_or_walking/distance_wc";
            tasks.add(manager.updateNode(walkPath, distanceWalked, isIncrement));
        }

        // ... transportation > "flight" > "short": 2
        //                               > "long": 1
        if(transportActivity.equals("flight")) {
            String[] splitString = haul.split(" ");
            String flightKey = splitString[0];
            String flightPath = commonPath + "flight/" + flightKey;
            tasks.add(manager.updateNode(flightPath, numFlight, isIncrement));
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            // This block will run after all tasks are completed
            if (task.isSuccessful()) {
                uploadData(true);
            } else {
                Toast.makeText(getContext(), "Failed to log activities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(boolean forceRefresh) {
        if (processor == null || forceRefresh) {
            processor = new DailyEmissionProcessor(getContext(), dateKey, () -> {
                processor.mainUploader();  // Upload transport data after loading
            });
        }
    }
}
