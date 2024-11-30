package com.example.planetze;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView;
import android.view.View;

public class HabitListActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference habitsRef;
    private DatabaseReference usersRef;
    private TextView textHabitTitle;
    private RecyclerView habitRecyclerView;
    private HabitAdapter habitAdapter;
    private List<String> habits;
    private List<String> filteredHabits;
    private String category;
    private String userId; // Remove hardcoded value
    private String selectedImpact = ""; // To hold the selected impact

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_list);

        // Retrieve the userId from UserSession
        userId = UserSession.userId;
        if (userId == null || userId.isEmpty()) {
            Log.e("HabitListActivity", "User ID is null or empty. Please log in again.");
            Toast.makeText(this, "User not logged in. Please log in again.", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if user is not logged in
            return;
        }

        Spinner spinner = findViewById(R.id.spinner_impact);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.impacts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Listen for selection changes in the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected impact from the spinner
                selectedImpact = parentView.getItemAtPosition(position).toString();
                filterHabits(); // Filter the habits based on the selected impact
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedImpact = ""; // If no selection is made, reset the impact
            }
        });

        category = getIntent().getStringExtra("habitCategory");

        database = FirebaseDatabase.getInstance();
        habitsRef = database.getReference("habits");
        usersRef = database.getReference("users"); // Reference to the users node

        habitRecyclerView = findViewById(R.id.habitRecyclerView);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        habits = new ArrayList<>();
        filteredHabits = new ArrayList<>();
        habitAdapter = new HabitAdapter(this, filteredHabits);
        habitRecyclerView.setAdapter(habitAdapter);

        textHabitTitle = findViewById(R.id.textHabitTitle);
        if (category != null) {
            textHabitTitle.setText(category.substring(0, 1).toUpperCase() + category.substring(1) + " Habits");
        }

        fetchHabits();

        // Set the OnItemClickListener for the adapter
        habitAdapter.setOnItemClickListener(new HabitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String habit) {
                adoptHabit(habit); // Handle habit adoption when clicked
            }
        });
    }

    private void fetchHabits() {
        if (category != null) {
            habitsRef.child(category).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    habits.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot habitSnapshot : dataSnapshot.getChildren()) {
                            String habit = habitSnapshot.getValue(String.class);
                            habits.add(habit);
                        }
                        filterHabits(); // Filter the habits when they are fetched
                        habitAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HabitListActivity.this, "No habits found for this category", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
                }
            });
        }
    }

    // Method to filter the habits based on the selected impact
    private void filterHabits() {
        filteredHabits.clear();

        // If "All" is selected, show all habits without filtering
        if ("All".equals(selectedImpact)) {
            filteredHabits.addAll(habits);
        } else {
            // Otherwise, filter habits based on selected impact
            for (String habit : habits) {
                if (habit.contains(selectedImpact)) {
                    filteredHabits.add(habit); // Add habit if it matches the selected impact
                }
            }
        }

        habitAdapter.notifyDataSetChanged(); // Refresh the adapter with filtered habits
    }

    // Method to handle the adoption of a habit by the user
    private void adoptHabit(String habit) {
        // Reference to the adopted_habits category for the current user
        DatabaseReference adoptedHabitsRef = usersRef.child(userId).child("adopted_habits").child(category);

        // Check if the habit already exists under this category for the current user
        adoptedHabitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean habitExists = false;

                // Iterate through existing habits to check if the habit already exists
                for (DataSnapshot habitSnapshot : dataSnapshot.getChildren()) {
                    String existingHabit = (String) habitSnapshot.child("habit").getValue();

                    // If the habit already exists, set habitExists to true and break the loop
                    if (existingHabit != null && existingHabit.equals(habit)) {
                        habitExists = true;
                        break;
                    }
                }

                if (habitExists) {
                    // If the habit already exists, show a message and do nothing
                    Toast.makeText(HabitListActivity.this, "Habit already adopted!", Toast.LENGTH_SHORT).show();
                } else {
                    // If the habit doesn't exist, proceed to add it
                    int index = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;

                    // Create a Map to store both habitname and daysCompleted
                    Map<String, Object> habitData = new HashMap<>();
                    habitData.put("habit", habit);
                    habitData.put("days_completed", 0);

                    // Add the habit under the calculated index and set both habitname and daysCompleted
                    adoptedHabitsRef.child(String.valueOf(index)).setValue(habitData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HabitListActivity.this, "Habit adopted!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HabitListActivity.this, "Failed to adopt habit", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error writing data: " + databaseError.getMessage());
            }
        });
    }
}
