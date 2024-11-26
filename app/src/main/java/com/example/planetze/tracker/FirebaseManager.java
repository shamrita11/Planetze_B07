package com.example.planetze.tracker;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This is a helper function to update information in firebase database
 */
public class FirebaseManager {
    private final Context context;
    private final FirebaseDatabase db;

    public FirebaseManager(Context context) {
        this.context = context;
        this.db = FirebaseDatabase.getInstance();
    }

    /**
     * Update or create a Firebase database node and value
     * @param isIncrement allows user to choose whether the value should be added to the
     *                    existing value in database, or overwrite it. For logging activity
     *                    for each day, this will increment it. For updating logged activity
     *                    in calendar section, this will overwrite.
     */
    public <T> Task<Void> updateNode(String refPath, T value, boolean isIncrement) {
        DatabaseReference ref = db.getReference(refPath);

        // Check if the path exists (to create it if not, otherwise update it)
        return ref.get().continueWithTask(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // if this path already exist, get it's value and update it
                    if(isIncrement) {
                        if(value instanceof Double) {
                            double existingDouble = task.getResult().getValue(Double.class);
                            double newDouble = existingDouble + (double) value;  // Adding to the existing value
                            return ref.setValue(newDouble);
                        } else if(value instanceof Integer) {
                            int existingInt = task.getResult().getValue(Integer.class);
                            int newInt = existingInt + (int) value;  // Adding to the existing value
                            return ref.setValue(newInt);
                        } else {
                            Toast.makeText(context,"Increment not supported for this type.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // only other choice is a string (no other type of input for now)
                        // do not increment a string
                        return ref.setValue(value);
                    }
                    Toast.makeText(context, "Activity Was Updated.", Toast.LENGTH_SHORT).show();
                } else {
                    // If the path doesn't exist, create this whole path
                    Toast.makeText(context, "New Activity Was Logged", Toast.LENGTH_SHORT).show();
                    return ref.setValue(value);
                }
            } else {
                // Handle Firebase request failure
                Toast.makeText(context, "Failed to fetch food data", Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }

}
