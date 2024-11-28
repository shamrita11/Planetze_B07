package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page); // Reference the welcome_page.xml

        // Link the "Get Started" button
        Button getStartedButton = findViewById(R.id.get_started_button);

        // Set up click listener for the button
        getStartedButton.setOnClickListener(v -> {
            // Navigate to the CarbonFootprintQuestionnaireActivity
            Intent intent = new Intent(WelcomeActivity.this, CarbonFootprintQuestionnaireActivity.class);
            startActivity(intent);
        });
    }
}
