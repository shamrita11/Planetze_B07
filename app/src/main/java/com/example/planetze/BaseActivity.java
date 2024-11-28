package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure the layout from child classes is used
        setContentView(getLayoutResourceId());

        // Set up navigation bar
        setupNavigationBar();
    }

    protected abstract int getLayoutResourceId();

    private void setupNavigationBar() {
        // Find navigation bar buttons
        ImageButton trackerButton = findViewById(R.id.trackerButton);
        ImageButton gaugeButton = findViewById(R.id.gaugeButton);
        ImageButton hubButton = findViewById(R.id.hubButton);
        ImageButton balanceButton = findViewById(R.id.balanceButton);
        ImageButton agentButton = findViewById(R.id.agentButton);
        ImageButton userButton = findViewById(R.id.userButton);


        //if (trackerButton != null) {
           // trackerButton.setOnClickListener(v -> navigateToActivity(TrackerActivity.class));
        //}
        if (gaugeButton != null) {
            gaugeButton.setOnClickListener(v -> navigateToActivity(EcoGaugeActivity.class));
        }
        //if (hubButton != null) {
            //hubButton.setOnClickListener(v -> navigateToActivity(EcoHubActivity.class));
        //}
        if (balanceButton != null) {
            balanceButton.setOnClickListener(v -> navigateToActivity(EcoBalanceActivity.class));
        }
        //if (agentButton != null) {
        // agentButton.setOnClickListener(v -> navigateToActivity(AgentActivity.class));
        //}

        //if (userButton != null) {
           // userButton.setOnClickListener(v -> navigateToActivity(UserActivity.class));
        //}
    }

    private void navigateToActivity(Class<?> activityClass) {
        if (!this.getClass().equals(activityClass)) {
            Intent intent = new Intent(this, activityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}