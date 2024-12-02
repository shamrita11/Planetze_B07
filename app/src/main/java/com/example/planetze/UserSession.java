package com.example.planetze;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";

    // Save the userId to SharedPreferences
    public static void setUserId(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.apply(); // Apply changes asynchronously
    }

    // Retrieve the userId from SharedPreferences
    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, null); // Return null if userId is not found
    }

    // Clear the stored userId
    public static void clearUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USER_ID);
        editor.apply();
    }
}
