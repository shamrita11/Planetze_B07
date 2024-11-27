package com.example.planetze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class AdoptedHabitsAdapter extends RecyclerView.Adapter<AdoptedHabitsAdapter.AdoptedHabitsViewHolder> {

    private Context context;
    private List<AdoptedHabit> adoptedHabits;
    private String userId;

    public AdoptedHabitsAdapter(Context context, List<AdoptedHabit> adoptedHabits, String userId) {
        this.context = context;
        this.adoptedHabits = adoptedHabits;
        this.userId = userId;
    }

    @Override
    public AdoptedHabitsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adopted_habit_item, parent, false);
        return new AdoptedHabitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdoptedHabitsViewHolder holder, int position) {
        AdoptedHabit adoptedHabit = adoptedHabits.get(position);
        holder.habitTextView.setText(adoptedHabit.getHabit());
        holder.daysCompletedCounter.setText(String.valueOf(adoptedHabit.getDaysCompleted()));

        DatabaseReference adoptedHabitsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("adopted_habits")
                .child(adoptedHabit.getCategory());

        holder.decrementButton.setOnClickListener(v -> {
            int currentCount = adoptedHabit.getDaysCompleted();
            if (currentCount > 0) {
                adoptedHabit.setDaysCompleted(currentCount - 1);
                holder.daysCompletedCounter.setText(String.valueOf(adoptedHabit.getDaysCompleted()));
                updateDaysCompletedInDatabase(adoptedHabit, adoptedHabitsRef);
            }
        });

        holder.incrementButton.setOnClickListener(v -> {
            int currentCount = adoptedHabit.getDaysCompleted();
            adoptedHabit.setDaysCompleted(currentCount + 1);
            holder.daysCompletedCounter.setText(String.valueOf(adoptedHabit.getDaysCompleted()));
            updateDaysCompletedInDatabase(adoptedHabit, adoptedHabitsRef);
        });

        holder.btnDeleteHabit.setOnClickListener(v -> {
            // Remove the habit from Firebase under the correct user and category
            deleteHabitFromDatabase(adoptedHabit, adoptedHabitsRef, position);
            // Show confirmation to the user
            Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return adoptedHabits.size();
    }

    private void updateDaysCompletedInDatabase(AdoptedHabit adoptedHabit, DatabaseReference adoptedHabitsRef) {
        adoptedHabitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot habitSnapshot : dataSnapshot.getChildren()) {
                    String habitName = habitSnapshot.child("habit").getValue(String.class);
                    if (habitName != null && habitName.equals(adoptedHabit.getHabit())) {
                        String index = habitSnapshot.getKey();
                        adoptedHabitsRef.child(index).child("days_completed").setValue(adoptedHabit.getDaysCompleted());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                // Handle database errors here
            }
        });
    }

    private void deleteHabitFromDatabase(AdoptedHabit adoptedHabit, DatabaseReference adoptedHabitsRef, int position) {
        adoptedHabitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot habitSnapshot : dataSnapshot.getChildren()) {
                    String habitName = habitSnapshot.child("habit").getValue(String.class);
                    if (habitName != null && habitName.equals(adoptedHabit.getHabit())) {
                        // Remove the habit from the database
                        String index = habitSnapshot.getKey(); // Get the habit's index
                        adoptedHabitsRef.child(index).removeValue(); // Delete habit from Firebase

                        // Remove the habit from the list and notify the adapter
                        adoptedHabits.remove(position);  // Remove the habit from the list
                        notifyItemRemoved(position);  // Notify the adapter that the item was removed

                        // If all items are deleted, notify the adapter to refresh the UI
                        if (adoptedHabits.isEmpty()) {
                            // Ensure that the RecyclerView is notified if all items are deleted
                            notifyDataSetChanged();
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError databaseError) {
                // Handle database errors here
            }
        });
    }

    public static class AdoptedHabitsViewHolder extends RecyclerView.ViewHolder {
        TextView habitTextView;
        TextView daysCompletedCounter;
        Button decrementButton;
        Button incrementButton;
        ImageView btnDeleteHabit;

        public AdoptedHabitsViewHolder(View itemView) {
            super(itemView);
            habitTextView = itemView.findViewById(R.id.habitTextView);
            daysCompletedCounter = itemView.findViewById(R.id.daysCompletedCounter);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            btnDeleteHabit = itemView.findViewById(R.id.btnDeleteHabit); // Binding delete ImageView
        }
    }
}
