package com.example.planetze.tracker;

import android.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class LogConsumptionFragment extends Fragment {
    private EditText editTextNumCloth, editTextNumDevice, editTextNumPurchase, editTextBill;
    private Spinner spinnerConsumeActivity, spinnerDeviceType, spinnerPurchaseType, spinnerBillType;
    private TextView labelNumCloth, labelDeviceType, labelNumDevice, labelPurchaseType
            , labelNumPurchase, labelBillType, labelBill;
    private Button buttonAdd;
    private ImageButton buttonBack;
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //get view (TODO: change the xml file name to the correct one)
        View view = inflater.inflate(R.layout.fragment_log_consumption, container, false);
        View includedView = view.findViewById(R.id.includedButtonBack);

        // retrieving the corresponding view by id
        editTextNumCloth = view.findViewById(R.id.editTextNumCloth);
        editTextNumDevice = view.findViewById(R.id.editTextNumDevice);
        editTextNumPurchase = view.findViewById(R.id.editTextNumPurchase);
        editTextBill = view.findViewById(R.id.editTextBill);
        spinnerConsumeActivity = view.findViewById(R.id.spinnerConsumeActivity);
        spinnerDeviceType = view.findViewById(R.id.spinnerDeviceType);
        spinnerPurchaseType = view.findViewById(R.id.spinnerPurchaseType);
        spinnerBillType = view.findViewById(R.id.spinnerBillType);
        labelNumCloth = view.findViewById(R.id.labelNumCloth);
        labelDeviceType = view.findViewById(R.id.labelDeviceType);
        labelNumDevice = view.findViewById(R.id.labelNumDevice);
        labelPurchaseType = view.findViewById(R.id.labelPurchaseType);
        labelNumPurchase = view.findViewById(R.id.labelNumPurchase);
        labelBillType = view.findViewById(R.id.labelBillType);
        labelBill = view.findViewById(R.id.labelBill);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonBack = includedView.findViewById(R.id.buttonBack);

        db = FirebaseDatabase.getInstance();

        // Hide some of the fields initially
        editTextNumCloth.setVisibility(View.GONE);
        editTextNumDevice.setVisibility(View.GONE);
        editTextNumPurchase.setVisibility(View.GONE);
        editTextBill.setVisibility(View.GONE);
        spinnerDeviceType.setVisibility(View.GONE);
        spinnerPurchaseType.setVisibility(View.GONE);
        spinnerBillType.setVisibility(View.GONE);
        labelNumCloth.setVisibility(View.GONE);
        labelDeviceType.setVisibility(View.GONE);
        labelNumDevice.setVisibility(View.GONE);
        labelPurchaseType.setVisibility(View.GONE);
        labelNumPurchase.setVisibility(View.GONE);
        labelBillType.setVisibility(View.GONE);
        labelBill.setVisibility(View.GONE);
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
                        labelNumCloth.setVisibility(View.GONE);
                        labelDeviceType.setVisibility(View.GONE);
                        labelNumDevice.setVisibility(View.GONE);
                        labelPurchaseType.setVisibility(View.GONE);
                        labelNumPurchase.setVisibility(View.GONE);
                        labelBillType.setVisibility(View.GONE);
                        labelBill.setVisibility(View.GONE);
                        buttonAdd.setVisibility(View.GONE);
                        return;
                    // Show specific fields based on actual selection
                    case "Buy new clothes":
                        editTextNumCloth.setVisibility(View.VISIBLE);
                        labelNumCloth.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        // hide the other elements
                        editTextNumDevice.setVisibility(View.GONE);
                        editTextNumPurchase.setVisibility(View.GONE);
                        editTextBill.setVisibility(View.GONE);
                        spinnerDeviceType.setVisibility(View.GONE);
                        spinnerPurchaseType.setVisibility(View.GONE);
                        spinnerBillType.setVisibility(View.GONE);
                        labelDeviceType.setVisibility(View.GONE);
                        labelNumDevice.setVisibility(View.GONE);
                        labelPurchaseType.setVisibility(View.GONE);
                        labelNumPurchase.setVisibility(View.GONE);
                        labelBillType.setVisibility(View.GONE);
                        labelBill.setVisibility(View.GONE);

                        break;
                    case "Buy electronics":
                        spinnerDeviceType.setVisibility(View.VISIBLE);
                        editTextNumDevice.setVisibility(View.VISIBLE);
                        labelDeviceType.setVisibility(View.VISIBLE);
                        labelNumDevice.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        editTextNumCloth.setVisibility(View.GONE);
                        editTextNumPurchase.setVisibility(View.GONE);
                        editTextBill.setVisibility(View.GONE);
                        spinnerPurchaseType.setVisibility(View.GONE);
                        spinnerBillType.setVisibility(View.GONE);
                        labelNumCloth.setVisibility(View.GONE);
                        labelPurchaseType.setVisibility(View.GONE);
                        labelNumPurchase.setVisibility(View.GONE);
                        labelBillType.setVisibility(View.GONE);
                        labelBill.setVisibility(View.GONE);

                        break;
                    case "Other purchases":
                        spinnerPurchaseType.setVisibility(View.VISIBLE);
                        editTextNumPurchase.setVisibility(View.VISIBLE);
                        labelPurchaseType.setVisibility(View.VISIBLE);
                        labelNumPurchase.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        editTextNumCloth.setVisibility(View.GONE);
                        editTextNumDevice.setVisibility(View.GONE);
                        editTextBill.setVisibility(View.GONE);
                        spinnerDeviceType.setVisibility(View.GONE);
                        spinnerBillType.setVisibility(View.GONE);
                        labelNumCloth.setVisibility(View.GONE);
                        labelDeviceType.setVisibility(View.GONE);
                        labelNumDevice.setVisibility(View.GONE);
                        labelBillType.setVisibility(View.GONE);
                        labelBill.setVisibility(View.GONE);

                        break;
                    case "Energy bills":
                        spinnerBillType.setVisibility(View.VISIBLE);
                        editTextBill.setVisibility(View.VISIBLE);
                        labelBillType.setVisibility(View.VISIBLE);
                        labelBill.setVisibility(View.VISIBLE);
                        buttonAdd.setVisibility(View.VISIBLE);

                        editTextNumCloth.setVisibility(View.GONE);
                        editTextNumDevice.setVisibility(View.GONE);
                        editTextNumPurchase.setVisibility(View.GONE);
                        spinnerDeviceType.setVisibility(View.GONE);
                        spinnerPurchaseType.setVisibility(View.GONE);
                        labelNumCloth.setVisibility(View.GONE);
                        labelDeviceType.setVisibility(View.GONE);
                        labelNumDevice.setVisibility(View.GONE);
                        labelPurchaseType.setVisibility(View.GONE);
                        labelNumPurchase.setVisibility(View.GONE);

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

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

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

        String userId = "user1";
        String dateKey = "2024-11-19";
        itemsRef = db.getReference("users").child(userId).child("daily_emission");

        // user1 > daily_emission > 2024-11-19 > consumption > consumptionActivity
        DatabaseReference consumptionRef = itemsRef.child(dateKey).child("consumption")
                .child(consumeActivity);

        // Log data for number of clothes purchased
        // ... consumption > "buy new clothes" > "numCloth": 1
        if(consumeActivity.equals("buy new clothes")) {
            // Check if the numCloth field already exist
            consumptionRef.child("num_cloth").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        int existingNum = task.getResult().getValue(Integer.class);
                        consumptionRef.child("num_cloth").setValue(existingNum + numCloth);
                        Toast.makeText(getContext(), "Your consumption data was updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // the path does not exist, so create the path (include any node that are missing)
                        consumptionRef.child("num_cloth").setValue(numCloth);
                        Toast.makeText(getContext(), "New consumption data was logged", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // Handle Firebase request failure
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ... consumption > "buy electronics" > "TV": 1
        //                                     > "smartphone": 2
        if(consumeActivity.equals("buy electronics")) {
            consumptionRef.child(deviceType).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Long existingNum = task.getResult().getValue(Long.class);
                        int updatedNum = (existingNum != null ? existingNum.intValue() : 0) + numDevice;
                        consumptionRef.child(deviceType).setValue(updatedNum);
                        Toast.makeText(getContext(), "Your consumption data was updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // the path does not exist, so create the path (include any node that are missing)
                        consumptionRef.child(deviceType).setValue(numDevice);
                        Toast.makeText(getContext(), "New consumption data was logged", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // Handle Firebase request failure
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ... consumption > "other purchases" > "furniture": 1
        //                                     > "appliances": 1
        if(consumeActivity.equals("other purchases")) {
            consumptionRef.child(purchaseType).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        int existingNum = task.getResult().getValue(Integer.class);
                        consumptionRef.child(purchaseType).setValue(existingNum + numPurchase);
                        Toast.makeText(getContext(), "Your consumption data was updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // the path does not exist, so create the path (include any node that are missing)
                        consumptionRef.child(purchaseType).setValue(numPurchase);
                        Toast.makeText(getContext(), "New consumption data was logged", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // Handle Firebase request failure
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // ... > "daily_emission" > "bill" > "2024-11" > "water": 150
        //                                        > "electricity": 165.2
        if(consumeActivity.equals("energy bills")) {
            String month = dateKey.substring(0, 7);

            DatabaseReference billRef = itemsRef.child("bill").child(month);
            billRef.child(billType).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        double existingAmount = task.getResult().getValue(Double.class);
                        billRef.child(billType).setValue(existingAmount + bill);
                        Toast.makeText(getContext(), "Your consumption data was updated", Toast.LENGTH_SHORT).show();
                    } else {
                        // the path does not exist, so create the path (include any node that are missing)
                        billRef.child(billType).setValue(bill);
                        Toast.makeText(getContext(), "New consumption data was logged", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    // Handle Firebase request failure
                    Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });

            if(billType.equals("electricity")) {
                // Other than uploading data into database, we also need to check for electricity bill
                // for electricity bill, we made some assumptions for daily emission calculation
                // See DailyEmissionProcessor.java for more info
                // TODO: change to correct path
                DatabaseReference billRangeRef = itemsRef.child("questionnaire_responses")
                        .child("consumption").child("2").child("answer");
                billRangeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Fetch the value
                        if (dataSnapshot.exists()) {
                            String billRange = dataSnapshot.getValue(String.class);

                            // Assumption:
                            // for the bill amount range, we assume it's lower inclusive and upper exclusive
                            // (i.e. 50 <= amount < 100)
                            int lower;
                            int upper;
                            //TODO: verify the string format
                            if(billRange.startsWith("Under")) {
                                upper = Integer.parseInt(billRange.replace("Under $", "").trim());
                                lower = 0;
                            } else if(billRange.startsWith("Over")) {
                                lower = Integer.parseInt(billRange.replace("Over $", "").trim());
                                upper = Integer.MAX_VALUE; // indicate no upper bound
                            } else {
                                String[] parts = billRange.replace("$", "").split("-");
                                lower = Integer.parseInt(parts[0].trim());
                                upper = Integer.parseInt(parts[1].trim());
                            }

                            // check if the bill amount this month falls in range
                            // if not, we warn the user about it
                            if(!(lower <= bill && bill < upper)) {
                                showWarningDialog("The new bill amount is outside of your " +
                                        "indicated monthly electricity bill range. Please ensure you " +
                                        "update your questionnaire answer if your bill amount have " +
                                        "significant changes.");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void showWarningDialog(String message) {
        new AlertDialog.Builder(getContext()).setTitle("Notice").setMessage(message)
                .setCancelable(false).setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss(); // close the dialog when "Ok" is clicked
                })
                .show();
    }
}
