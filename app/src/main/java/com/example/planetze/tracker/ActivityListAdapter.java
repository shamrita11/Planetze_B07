package com.example.planetze.tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planetze.R;

import java.util.List;
import java.util.Map;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListViewHolder> {
    private final List<ActivityItem> activityList;
    private final OnDetailButtonClickListener listener;

    public interface OnDetailButtonClickListener {
        void onDetailButtonClicked(String activityType, String activityKey, String detailKey);
    }

    public ActivityListAdapter(List<ActivityItem> activityList, OnDetailButtonClickListener listener) {
        this.activityList = activityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivityListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list_item, parent, false);
        return new ActivityListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityListViewHolder holder, int position) {
        ActivityItem item = activityList.get(position);

        holder.activityName.setText(item.getActivityName());

        String co2e = item.getCo2e() + " kg CO2e";
        holder.co2e.setText(co2e);

        // Dynamically add details and buttons
        holder.detailsContainer.removeAllViews();
        if(item.getDetails() != null) {
            for (Map.Entry<String, String> detail : item.getDetails().entrySet()) {
                String detailKey = detail.getKey();
                String detailValue = detail.getValue();

                // Create a horizontal layout for the detail and its button
                // Create a horizontal layout for the detail and its button
                View detailView = LayoutInflater.from(holder.itemView.getContext())
                        .inflate(R.layout.activity_list_item_detail, holder.detailsContainer
                                , false);
                TextView detailText = detailView.findViewById(R.id.detail_text);
                Button deleteButton = detailView.findViewById(R.id.detail_delete_button);

                detailText.setText(String.format("%s: %s", detailKey.replace("_", " ")
                        , detailValue));

                deleteButton.setOnClickListener(v -> {
                    String activityType = item.getActivityType();
                    String activityKey = item.getActivityKey();
                    if (listener != null) {
                        listener.onDetailButtonClicked(activityType, activityKey, detailKey);
                    }
                });

                // Add the row to the details container
                holder.detailsContainer.addView(detailView);
            }
        } else {
            // Optional: Handle case where there are no details
            TextView noDetailsText = new TextView(holder.itemView.getContext());
            noDetailsText.setText(R.string.no_detail);
            noDetailsText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dark_grey));
            holder.detailsContainer.addView(noDetailsText);
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}
