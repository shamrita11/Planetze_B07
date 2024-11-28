package com.example.planetze;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends android.app.Service {
    private static final String CHANNEL_ID = "habit_reminder_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendHabitReminderNotification();
        return START_NOT_STICKY;
    }

    private void sendHabitReminderNotification() {
        // Create the notification channel for Android O and above
        createNotificationChannel();

        // Intent to launch MainActivity when the notification is clicked
        Intent notificationIntent = new Intent(this, EcoHabitActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // To launch the activity cleanly
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)  // Replace with your own icon
                .setContentTitle("Habit Reminder")
                .setContentText("Don't forget to log your habit today!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);  // Set the PendingIntent to handle clicks

        // Get the NotificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Send the notification with ID 1
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Habit Reminder";
            String description = "Channel for daily habit reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null;
    }
}
