package com.example.planetze;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the NotificationService to send the habit reminder notification
        Intent serviceIntent = new Intent(context, NotificationService.class);
        if (context.startService(serviceIntent) == null) {
            Log.d("NotificationReceiver", "Service not started");
        }
    }
}
