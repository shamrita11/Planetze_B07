package com.example.planetze.tracker;

import androidx.annotation.Nullable;

import java.util.Map;

public class ActivityItem {
    private final String activityType;
    private final String activityName;
    private final String co2e;
    private final Map<String, String> details;

    /**
     * Take in the appropriate variables and initialize the fields
     * @param activityType is the type of activity, for example, "transportation"
     * @param activityName is the activity name, for example, "drive personal vehicle"
     * @param details is the specifics of the activity, where the keys are the name of the detail, and
     *                the values are the logged values for this detail. For example, distance_drive: 1.5 km
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
