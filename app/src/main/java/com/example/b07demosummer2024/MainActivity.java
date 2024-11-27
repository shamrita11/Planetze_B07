package com.example.b07demosummer2024;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

    private static final String CHANNEL_ID = "habit_reminder_channel";
    private FirebaseDatabase database;
    private DatabaseReference habitsRef;
    private RecyclerView habitCategoryRecyclerView;
    private HabitCategoryAdapter habitCategoryAdapter;
    private List<String> habitCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request POST_NOTIFICATIONS permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Set the alarm to remind the user every 8 hours
        setHabitReminderAlarm();

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

    private void setHabitReminderAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);  // Custom BroadcastReceiver

        // Add FLAG_IMMUTABLE to the PendingIntent to comply with Android 12+ requirements
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Cancel any existing alarm before setting a new one
        alarmManager.cancel(pendingIntent);

        // Set the alarm to trigger every 8 hours (1 minute for testing purposes)
        //long interval = 8 * 60 * 60 * 1000; // 8 hours in milliseconds
        long interval = 5000;
        long triggerAtMillis = System.currentTimeMillis() + interval; // First trigger after 8 hours

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with setting up notifications
            } else {
                // Permission denied, handle appropriately
                Log.d("MainActivity", "POST_NOTIFICATIONS permission denied");
            }
        }
    }
}
