package com.example.b07demosummer2024;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class HabitListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_list);


        // Reference the back button
        ImageButton backButton = findViewById(R.id.back_button);


        // Check if button is found before setting click listener
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to MainActivity
                    Intent intent = new Intent(HabitListActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Optional: finish the current activity
                }
            });
        }


        // Reference the transportation button (more descriptive name)
        Button viewTransportHabitsButton = findViewById(R.id.transportation_button);


        // Check if button is found before setting click listener
        if (viewTransportHabitsButton != null) {
            viewTransportHabitsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to TransportationHabitList activity
                    Intent intent = new Intent(HabitListActivity.this, TransportationHabitList.class);
                    startActivity(intent);
                    finish(); // Optional: finish the current activity
                }
            });


            // Reference the transportation button (more descriptive name)
            Button viewConsumptionHabitsButton = findViewById(R.id.consumption_button);

            // Check if button is found before setting click listener
            if (viewConsumptionHabitsButton != null) {
                viewConsumptionHabitsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to ConsumptionHabitList activity
                        Intent intent = new Intent(HabitListActivity.this, ConsumptionHabitList.class);
                        startActivity(intent);
                        finish(); // Optional: finish the current activity
                    }
                });
            }
        }
    }
}
