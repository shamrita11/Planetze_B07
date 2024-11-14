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

import com.example.planetze.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogConsumptionFragment extends Fragment {
    private EditText editTextNumCloth, editTextNumDevice, editTextNumPurchase, editTextBill;
    private Spinner spinnerConsumeActivity, spinnerDeviceType, spinnerPurchaseType, spinnerBillType;
    private Button buttonAdd;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get view (TODO: change the xml file name to the correct one)
        View view = inflater.inflate(R.layout.fragment_log_consumption, container, false);

        // retrieving the corresponding view by id
        editTextNumCloth = view.findViewById(R.id.editTextNumCloth);
        editTextNumDevice = view.findViewById(R.id.editTextNumDevice);
        editTextNumPurchase = view.findViewById(R.id.editTextNumPurchase);
        editTextBill = view.findViewById(R.id.editTextBill);
        spinnerConsumeActivity = view.findViewById(R.id.spinnerConsumeActivity);
        spinnerDeviceType = view.findViewById(R.id.spinnerDeviceType);
        spinnerPurchaseType = view.findViewById(R.id.spinnerPurchaseType);
        spinnerBillType = view.findViewById(R.id.spinnerBillType);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        db = FirebaseDatabase.getInstance("https://planetze-g16-default-rtdb.firebaseio.com/");

        // Hide some of the fields initially
        editTextNumCloth.setVisibility(View.GONE);
        editTextNumDevice.setVisibility(View.GONE);
        editTextNumPurchase.setVisibility(View.GONE);
        editTextBill.setVisibility(View.GONE);
        spinnerDeviceType.setVisibility(View.GONE);
        spinnerPurchaseType.setVisibility(View.GONE);
        spinnerBillType.setVisibility(View.GONE);
        buttonAdd.setVisibility(View.GONE);

        // Set up the spinner with categories
        //TODO: figure out where and how to change the view based on choice chosen from spinner
        ArrayAdapter<CharSequence> ConsumeActivityAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.consumption_activity, android.R.layout.simple_spinner_item);
        ConsumeActivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConsumeActivity.setAdapter(ConsumeActivityAdapter);

        ArrayAdapter<CharSequence> DeviceTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.device_type, android.R.layout.simple_spinner_item);
        DeviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeviceType.setAdapter(DeviceTypeAdapter);

        ArrayAdapter<CharSequence> PurchaseTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.purchase_type, android.R.layout.simple_spinner_item);
        PurchaseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPurchaseType.setAdapter(PurchaseTypeAdapter);

        ArrayAdapter<CharSequence> BillTypeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.bill_type, android.R.layout.simple_spinner_item);
        BillTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBillType.setAdapter(BillTypeAdapter);

        // onItemSelected for spinnerTransportActivity
        spinnerConsumeActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedActivity = parent.getSelectedItem().toString();

                switch (selectedActivity) {
                    case "Select an activity":
                        // if the placeholder is chosen again, hide all other fields
                        editTextNumCloth.setVisibility(View.GONE);
                        editTextNumDevice.setVisibility(View.GONE);
                        editTextNumPurchase.setVisibility(View.GONE);
                        editTextBill.setVisibility(View.GONE);
                        spinnerDeviceType.setVisibility(View.GONE);
                        spinnerPurchaseType.setVisibility(View.GONE);
                        spinnerBillType.setVisibility(View.GONE);
                        buttonAdd.setVisibility(View.GONE);
                        return;
                    // Show specific fields based on actual selection
                    case "Buy new clothes":
                        editTextNumCloth.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                    case "Buy electronics":
                        spinnerDeviceType.setVisibility(View.VISIBLE);
                        editTextNumDevice.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                    case "Other purchases":
                        spinnerPurchaseType.setVisibility(View.VISIBLE);
                        editTextNumPurchase.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);
                        break;
                    case "Energy bills":
                        spinnerBillType.setVisibility(View.VISIBLE);
                        editTextBill.setVisibility(View.VISIBLE);
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
        String consumeActivity = spinnerConsumeActivity.getSelectedItem().toString().toLowerCase();
        String numClothStr, numDeviceStr, numPurchaseStr, billStr,
                deviceType, purchaseType, billType;
        int numCloth, numDevice, numPurchase;
        double bill;

        if (editTextNumCloth.getVisibility() == View.VISIBLE) {
            numClothStr = editTextNumCloth.getText().toString().trim();
            // check if user input is empty
            if (numClothStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out the number of clothes purchased",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // if not empty, convert it to a double
            try {
                numCloth = Integer.parseInt(numClothStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid number of clothes",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            numCloth = 0;
        }

        if (editTextNumDevice.getVisibility() == View.VISIBLE) {
            numDeviceStr = editTextNumDevice.getText().toString().trim();
            // check if user input is empty
            if (numDeviceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out the number of devices purchased",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // if not empty, turn it into double
            try {
                numDevice = Integer.parseInt(numDeviceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid number of devices",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            numDevice = 0;
        }

        if (editTextNumPurchase.getVisibility() == View.VISIBLE) {
            numPurchaseStr = editTextNumPurchase.getText().toString().trim();
            if (numPurchaseStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out the number of purchases made",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                numPurchase = Integer.parseInt(numPurchaseStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid number of purchase",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            numPurchase = 0;
        }

        if (editTextBill.getVisibility() == View.VISIBLE) {
            billStr = editTextBill.getText().toString().trim();
            if (billStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out the bill amount",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                bill = Double.parseDouble(billStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid bill amount",
                        Toast.LENGTH_SHORT).show();
                return;  // Stop the function if parsing fails
            }
        } else {
            bill = 0;
        }

        if (spinnerDeviceType.getVisibility() == View.VISIBLE) {
            deviceType = spinnerDeviceType.getSelectedItem().toString().toLowerCase();
            if (deviceType.equals("select the electronic device type")) {
                Toast.makeText(getContext(), "Please choose an electronic device type",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            deviceType = "";
        }

        if (spinnerPurchaseType.getVisibility() == View.VISIBLE) {
            purchaseType = spinnerPurchaseType.getSelectedItem().toString().toLowerCase();
            if (purchaseType.equals("select the purchase type")) {
                Toast.makeText(getContext(), "Please choose a purchase type",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            purchaseType = "";
        }

        if (spinnerBillType.getVisibility() == View.VISIBLE) {
            billType = spinnerBillType.getSelectedItem().toString().toLowerCase();
            if (billType.equals("select the type of bill")) {
                Toast.makeText(getContext(), "Please choose the type of bill",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            billType = "";
        }

        // TODO: check or create a path for storing those info
        // TODO: do we want daily_emission/date/consumption?
        itemsRef = db.getReference("daily_emission/consumption");
        // TODO: figure out how to get current date (and generate an id) to then create key-value
        //  pair
        // TODO: if we can use date as the key?
        String date_id = itemsRef.push().getKey();
        // TODO: see if this is the structure of database we want
        // TODO: see if we can modify the items instead if already exist an item with this
        //  specific date
        ConsumptionModel item = new ConsumptionModel(date_id, numCloth, deviceType, numDevice,
                purchaseType, numPurchase, billType, bill);

        itemsRef.child(date_id).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to add item", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
