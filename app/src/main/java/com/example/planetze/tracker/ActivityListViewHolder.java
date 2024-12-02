package com.example.planetze.tracker;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planetze.R;

public class ActivityListViewHolder extends RecyclerView.ViewHolder {
    TextView activityName, co2e;
    LinearLayout detailsContainer;

    public ActivityListViewHolder(@NonNull View itemView) {
        super(itemView);
        activityName = itemView.findViewById(R.id.activity_name);
        co2e = itemView.findViewById(R.id.activity_co2e);
        detailsContainer = itemView.findViewById(R.id.details_container);
    }
}
