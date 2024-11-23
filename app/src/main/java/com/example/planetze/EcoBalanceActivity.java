package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EcoBalanceActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<EcoBalanceProject> projects = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_eco_balance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projects = loadProjectsFromCSV();

        // Set up RecyclerView Adapter
        EcoProjectAdapter adapter = new EcoProjectAdapter(projects, this::onProjectClicked);
        recyclerView.setAdapter(adapter);
    }

    private List<EcoBalanceProject> loadProjectsFromCSV() {
        List<EcoBalanceProject> projects = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(R.raw.eco_balance_projects);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            boolean isHeader = true; // Skip header row
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] columns = line.split(",");
                if (columns.length >= 5) {
                    String name = columns[0].trim();
                    String description = columns[1].trim();
                    String location = columns[2].trim();
                    String impactMetrics = columns[3].trim();
                    double costPerTon = Double.parseDouble(columns[4].trim());

                    projects.add(new EcoBalanceProject(name, description, location, impactMetrics, costPerTon));
                } else {
                    Log.e("CSV", "Skipping malformed line: " + line);
                }
            }
        } catch (Exception e) {
            Log.e("CSV", "Error reading CSV file", e);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                Log.e("CSV", "Error closing reader", e);
            }
        }
        return projects;
    }

    private void onProjectClicked(EcoBalanceProject project) {
        Intent intent = new Intent(this, PurchaseProjectActivity.class);
        intent.putExtra("projectName", project.getName());
        intent.putExtra("projectDescription", project.getDescription());
        intent.putExtra("projectLocation", project.getLocation());
        intent.putExtra("projectImpact", project.getImpactMetrics());
        intent.putExtra("projectCost", project.getCostPerTon());
        startActivity(intent);
    }
}


