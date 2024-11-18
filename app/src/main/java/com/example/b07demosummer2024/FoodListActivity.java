package com.example.b07demosummer2024;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list2);

        // Reference the back button
        ImageButton backButton = findViewById(R.id.food_back);

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(FoodListActivity.this, HabitListActivity.class);
                startActivity(intent);
                // Optional: finish the current activity to prevent returning to it
                finish();
            }
        });
    }
}