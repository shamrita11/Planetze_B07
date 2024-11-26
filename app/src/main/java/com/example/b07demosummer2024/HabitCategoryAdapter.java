package com.example.b07demosummer2024;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HabitCategoryAdapter extends RecyclerView.Adapter<HabitCategoryAdapter.HabitCategoryViewHolder> {

    private Context context;
    private List<String> habitCategories;

    // Constructor to initialize context and the list of habit categories
    public HabitCategoryAdapter(Context context, List<String> habitCategories) {
        this.context = context;
        this.habitCategories = habitCategories;
    }

    @Override
    public HabitCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate your custom layout for each item (habit_category.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.habit_category, parent, false);
        return new HabitCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HabitCategoryViewHolder holder, int position) {
        // Bind the data (habit category name) to the view holder (TextView)
        String habitCategory = habitCategories.get(position);
        holder.habitCategoryTextView.setText(habitCategory.toUpperCase());

        // Set up click listener to navigate to the HabitListActivity when a category is clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HabitListActivity.class);  // Navigate to the HabitListActivity
            intent.putExtra("habitCategory", habitCategory);  // Pass the selected habit category name
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return habitCategories.size();  // Return the size of the habit categories list
    }

    // Define the ViewHolder which holds the views for each item in the list
    public static class HabitCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView habitCategoryTextView;

        public HabitCategoryViewHolder(View itemView) {
            super(itemView);
            habitCategoryTextView = itemView.findViewById(R.id.textCategory);  // Bind the TextView from habit_category.xml
        }
    }
}
