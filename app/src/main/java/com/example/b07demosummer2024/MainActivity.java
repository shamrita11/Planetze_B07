package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference habitsRef;
    private RecyclerView habitCategoryRecyclerView;
    private HabitCategoryAdapter habitCategoryAdapter;
    private List<String> habitCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        habitsRef = database.getReference("habits");

        // Set up RecyclerView
        habitCategoryRecyclerView = findViewById(R.id.habitCategoryRecyclerView);
        habitCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        habitCategories = new ArrayList<>();
        habitCategoryAdapter = new HabitCategoryAdapter(this, habitCategories);
        habitCategoryRecyclerView.setAdapter(habitCategoryAdapter);

        // Fetch habit categories from Firebase
        habitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get habit categories keys from Firebase
                    for (DataSnapshot habitCategorySnapshot : dataSnapshot.getChildren()) {
                        String habitCategory = habitCategorySnapshot.getKey();
                        habitCategories.add(habitCategory); // Add habit category to the list
                    }
                    habitCategoryAdapter.notifyDataSetChanged(); // Notify the adapter to update the UI
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });

        // Set up click listener for the Adopted Habits TextView
        TextView btnAdoptedHabits = findViewById(R.id.btn_adopted_habits);
        btnAdoptedHabits.setOnClickListener(v -> {
            // Intent to navigate to AdoptedHabitsActivity
            Intent intent = new Intent(MainActivity.this, AdoptedHabitsActivity.class);
            startActivity(intent);  // Launch the AdoptedHabitsActivity
        });
    }
}
