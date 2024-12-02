package com.example.planetze;

import static java.security.AccessController.getContext;

import android.os.Bundle;
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
import java.util.List;

public class AdoptedHabitsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private RecyclerView adoptedHabitsRecyclerView;
    private AdoptedHabitsAdapter adoptedHabitsAdapter;
    private List<AdoptedHabit> adoptedHabitsList;
    private String userId; // Use dynamic user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopted_habits);

        // Get the current user ID from UserSession
        userId = UserSession.getUserId(this);

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not logged in. Please log in.", Toast.LENGTH_SHORT).show();
            finish(); // Exit the activity if the user is not logged in
            return;
        }

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        adoptedHabitsRecyclerView = findViewById(R.id.adoptedHabitsRecyclerView);
        adoptedHabitsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adoptedHabitsList = new ArrayList<>();
        adoptedHabitsAdapter = new AdoptedHabitsAdapter(this, adoptedHabitsList, userId);
        adoptedHabitsRecyclerView.setAdapter(adoptedHabitsAdapter);

        fetchAdoptedHabits();
    }

    private void fetchAdoptedHabits() {
        usersRef.child(userId).child("adopted_habits").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adoptedHabitsList.clear();

                if (dataSnapshot.exists()) {
                    // Iterate over categories
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String category = categorySnapshot.getKey(); // Category name
                        for (DataSnapshot habitSnapshot : categorySnapshot.getChildren()) {
                            // For each habit, retrieve the habit name and daysCompleted
                            String habit = habitSnapshot.child("habit").getValue(String.class);
                            int daysCompleted = habitSnapshot.child("days_completed").getValue(Integer.class);

                            // Create and add the AdoptedHabit object to the list
                            adoptedHabitsList.add(new AdoptedHabit(category, habit, daysCompleted));
                        }
                    }
                    adoptedHabitsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdoptedHabitsActivity.this, "No adopted habits found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdoptedHabitsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
