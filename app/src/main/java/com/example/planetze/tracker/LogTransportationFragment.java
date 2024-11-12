package com.example.b07demosummer2024.tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.b07demosummer2024.Item;
import com.example.b07demosummer2024.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//View
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
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // retrieving the corresponding view by id
        editTextDistanceDriven = view.findViewById(R.id.editTextDistanceDriven);
        editTextTransportTime = view.findViewById(R.id.editTextTransportTime);
        editTextDistanceWalked = view.findViewById(R.id.editTextDistanceWalked);
        editTextNumFlight = view.findViewById(R.id.editTextNumFlight);
        spinnerTransportActivity = view.findViewById(R.id.spinnerTransportActivity);
        spinnerTransportType = view.findViewById(R.id.spinnerTransportType);
        spinnerHaul = view.findViewById(R.id.spinnerHaul);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        db = FirebaseDatabase.getInstance("https://planetze-g16-default-rtdb.firebaseio.com/");

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
        String distanceDriven = editTextDistanceDriven.getText().toString().trim();
        String transportTime = editTextTransportTime.getText().toString().trim();
        String distanceWalked = editTextDistanceWalked.getText().toString().trim();
        String numFlight = editTextNumFlight.getText().toString().trim();
        String transportActivity = spinnerTransportActivity.getSelectedItem().toString().toLowerCase();
        String transportType = spinnerTransportType.getSelectedItem().toString().toLowerCase();
        String haul = spinnerHaul.getSelectedItem().toString().toLowerCase();

        // TODO: change the variable name to the correct ones (the ones above)
        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        itemsRef = db.getReference("categories/" + category);
        String id = itemsRef.push().getKey();
        Item item = new Item(id, title, author, genre, description);

        itemsRef.child(id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}