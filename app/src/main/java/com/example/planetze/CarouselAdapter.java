package com.example.planetze;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    private List<ResourceType> items;
    private Context context;

    public CarouselAdapter(List<ResourceType> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        ResourceType item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        // Dynamically load the image based on the icon field
        String iconName = item.getIcon();
        int iconResId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());

        if (iconResId != 0) { // Check if the resource exists
            holder.imageView.setImageResource(iconResId); // Set the image to the ImageView
        } else {
            holder.imageView.setImageResource(R.drawable.hub_default); // Fallback icon if not found
        }

        holder.itemView.setOnClickListener(v -> {
            // Open the website URL when clicked
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class CarouselViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView imageView;

        public CarouselViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.carousel_item_title);
            description = itemView.findViewById(R.id.carousel_item_description);
            imageView = itemView.findViewById(R.id.carousel_item_icon);
        }
    }
}