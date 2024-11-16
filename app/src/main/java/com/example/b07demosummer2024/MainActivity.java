package com.example.b07demosummer2024;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference habitsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to a button
        Button navigateButton = findViewById(R.id.btn_habitList);

        // Set click listener, this is going from Lucy's section A
        // to my section B where it shows the habit list
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to SecondActivity
                Intent intent = new Intent(MainActivity.this, HabitListActivity.class);
                startActivity(intent);
            }
        });



//        // Initialize Firebase Database
//        database = FirebaseDatabase.getInstance();
//        habitsRef = database.getReference("habits");
//
//        // Retrieve and log the habits data
//        habitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
//                    String category = categorySnapshot.getKey();
//                    Log.d("MainActivity", "Category: " + category);
//
//                    for (DataSnapshot habitSnapshot : categorySnapshot.getChildren()) {
//                        String habit = habitSnapshot.child("habit").getValue(String.class);
//                        String impact = habitSnapshot.child("impact").getValue(String.class);
//                        Log.d("MainActivity", "Habit: " + habit + ", Impact: " + impact);
//                    }
//                }
//            }
//        });
    }
}
