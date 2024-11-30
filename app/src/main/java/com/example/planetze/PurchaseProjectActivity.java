package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.math.BigDecimal;

public class PurchaseProjectActivity extends BaseActivity {

    private String userId; // Declare userId as a class variable

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_purchase_project; // Ensure this matches your XML filename
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_project);

        // Retrieve the current user's ID from UserSession
        userId = UserSession.userId;

        // Check if userId is valid
        if (userId == null || userId.isEmpty()) {
            // Handle invalid session, redirect to login
            Intent intent = new Intent(this, EcoBalanceActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Get references to the views
        TextView projectNameText = findViewById(R.id.projectNameText);
        TextView projectDetailsText = findViewById(R.id.projectDetailsText);
        TextView totalCostText = findViewById(R.id.totalCostText);
        EditText co2OffsetInput = findViewById(R.id.co2OffsetInput);

        // Retrieve data from the Intent with default values
        String projectName = getIntent().getStringExtra("projectName");
        if (projectName == null) projectName = "Unnamed Project";

        String projectDescription = getIntent().getStringExtra("projectDescription");
        if (projectDescription == null) projectDescription = "No description available";

        String projectLocation = getIntent().getStringExtra("projectLocation");
        if (projectLocation == null) projectLocation = "Unknown location";

        String projectImpact = getIntent().getStringExtra("projectImpact");
        if (projectImpact == null) projectImpact = "No impact data available";

        double projectCost = getIntent().getDoubleExtra("projectCost", 0.0);

        // Update the UI
        projectNameText.setText(projectName);
        String projectDetails = "Description: " + projectDescription + "\n" +
                "Impact: " + projectImpact + "\n" +
                "Location: " + projectLocation + "\n" +
                "Cost per ton: $" + String.format("%.2f", projectCost);
        projectDetailsText.setText(projectDetails);

        Button purchaseButton = findViewById(R.id.purchaseButton);

        // Changes the total cost depending on how many tonnes the user wants to offset
        co2OffsetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Recalculate total cost when input changes
                if (!s.toString().isEmpty()) {
                    try {
                        int tonnes = Integer.parseInt(s.toString());
                        double totalCost = tonnes * projectCost;
                        totalCostText.setText("Total: $" + String.format("%.2f", totalCost));
                    } catch (NumberFormatException e) {
                        totalCostText.setText("Total: $0.00");
                    }
                } else {
                    totalCostText.setText("Total: $0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        purchaseButton.setOnClickListener(v -> {
            // Retrieve user input
            String inputTonnes = co2OffsetInput.getText().toString();
            if (inputTonnes.isEmpty()) {
                totalCostText.setText("Please enter the number of tonnes.");
                return;
            }

            double tonnesToOffset;
            try {
                tonnesToOffset = Double.parseDouble(inputTonnes);
                if (tonnesToOffset <= 0) {
                    totalCostText.setText("Please enter a positive number.");
                    return;
                }
            } catch (NumberFormatException e) {
                totalCostText.setText("Invalid input. Please enter a number.");
                return;
            }

            // Reference to the user's offset data in the database
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId) // Use the dynamic userId here
                    .child("totaloffsetc02");

            // Use Firebase transaction to update the totaloffsetc02 value
            userRef.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                    Double currentOffset = mutableData.getValue(Double.class);
                    if (currentOffset == null) {
                        currentOffset = 0.0; // Initialize if missing
                    }
                    BigDecimal updatedOffset = BigDecimal.valueOf(currentOffset)
                            .add(BigDecimal.valueOf(tonnesToOffset).multiply(BigDecimal.valueOf(1000)));
                    mutableData.setValue(updatedOffset.doubleValue());
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (committed) {
                        // Navigate to confirmation screen on success
                        Intent intent = new Intent(PurchaseProjectActivity.this, PurchaseConfirmationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle failure
                        totalCostText.setText("Failed to update offset value. Try again.");
                    }
                }
            });
        });
    }
}
