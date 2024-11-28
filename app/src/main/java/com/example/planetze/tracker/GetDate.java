package com.example.planetze.tracker;

import android.os.Build;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This helper class provides a static method for all classes to use to get today's date in
 * format "YYYY-MM-DD"
 */
public class GetDate {
    public static String getDate() {
        String date;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate today = LocalDate.now();
            date = today.format(DateTimeFormatter.ISO_DATE);
        } else {
            date = "";
        }
        return date;
    }
}
