package com.example.planetze;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EcoProjectAdapter extends RecyclerView.Adapter<EcoProjectAdapter.ProjectViewHolder> {

    private final List<EcoBalanceProject> projects;
    private final OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(EcoBalanceProject project);
    }

    public EcoProjectAdapter(List<EcoBalanceProject> projects, OnProjectClickListener listener) {
        this.projects = projects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        EcoBalanceProject project = projects.get(position);

        holder.projectName.setText(project.getName());
        holder.projectDescription.setText(project.getDescription());
        holder.projectImpact.setText(project.getImpactMetrics());
        holder.projectLocation.setText(project.getLocation());
        holder.projectCost.setText("$" + String.format("%.2f", project.getCostPerTon()));

        holder.itemView.setOnClickListener(v -> listener.onProjectClick(project));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView projectName, projectDescription, projectImpact, projectLocation, projectCost;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectName);
            projectDescription = itemView.findViewById(R.id.projectDescription);
            projectImpact = itemView.findViewById(R.id.projectImpact);
            projectLocation = itemView.findViewById(R.id.projectLocation);
            projectCost = itemView.findViewById(R.id.projectCost);
        }
    }
}



