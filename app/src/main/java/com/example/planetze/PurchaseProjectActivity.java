package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PurchaseProjectActivity extends BaseActivity {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_purchase_project; // Ensure this matches your XML filename
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_project);

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
        purchaseButton.setOnClickListener(v -> {
            // Navigate to Purchase Confirmation Activity
            Intent intent = new Intent(PurchaseProjectActivity.this, PurchaseConfirmationActivity.class);
            startActivity(intent);
        });

        //changes the total cost depending on how much tonnes the user wants to offset
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
    }
}
