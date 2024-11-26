package com.example.b07demosummer2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private Context context;
    private List<String> habits;
    private OnItemClickListener onItemClickListener;

    // Constructor to initialize context and the list of habits
    public HabitAdapter(Context context, List<String> habits) {
        this.context = context;
        this.habits = habits;
    }

    @Override
    public HabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item (habit_item.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HabitViewHolder holder, int position) {
        // Bind the data (habit) to the view holder (TextView)
        String habit = habits.get(position);
        holder.habitTextView.setText(habit);

        // Set click listener on the adopt habit button
        holder.btnAdoptHabit.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(habit); // Notify the activity with the selected habit
            }
        });
    }

    @Override
    public int getItemCount() {
        return habits.size();  // Return the size of the habits list
    }

    // Define the ViewHolder which holds the views for each item in the list
    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitTextView;
        ImageButton btnAdoptHabit;

        public HabitViewHolder(View itemView) {
            super(itemView);
            habitTextView = itemView.findViewById(R.id.textHabit);  // Bind the TextView from habit_item.xml
            btnAdoptHabit = itemView.findViewById(R.id.btn_adopt_habit); // Bind the adopt button from habit_item.xml
        }
    }

    // Interface to handle item click
    public interface OnItemClickListener {
        void onItemClick(String habit); // Method to be called when an item (habit) is clicked
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
