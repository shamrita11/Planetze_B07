package com.example.planetze.tracker;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.util.Map;

public class ActivityItem {
    private String activityType;
    private String activityName;
    private String co2e;
    private Map<String, String> details; // Key: detail name, Value: detail value

    /**
     * Take in the appropriate variables and initialize the fields
     * @param activityName is the activity name, for example, "Drive personal vehicle"
     * @param details is the specifics of the activity, for example, "distance driven: 1.5 km"
     * @param co2e is the total co2e for this activity
     */
    public ActivityItem(@Nullable String activityType, String activityName, @Nullable String co2e, @Nullable Map<String, String> details) {
        this.activityType = activityType;
        this.activityName = activityName;
        this.co2e = co2e;
        this.details = details;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getActivityKey() {
        return activityName.replace(" ", "_");
    }

    public String getActivityName() {
        return activityName;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public String getCo2e() {
        return co2e;
    }
}
