package com.example.planetze;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EcoHub extends AppCompatActivity {

    private RecyclerView carouselRecyclerView;
    private Button btnBooks, btnArticles, btnVideos, btnMovies, btnPodcasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_hub);

        btnBooks = findViewById(R.id.btnBooks);
        btnArticles = findViewById(R.id.btnArticles);
        btnVideos = findViewById(R.id.btnVideos);
        btnMovies = findViewById(R.id.btnMovies);
        btnPodcasts = findViewById(R.id.btnPodcasts);
        carouselRecyclerView = findViewById(R.id.carouselRecyclerView);

        // Set up RecyclerView (Horizontal orientation)
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Default is Books
        showBooks();

        // Button Click Listeners
        btnBooks.setOnClickListener(v -> showBooks());
        btnArticles.setOnClickListener(v -> showArticles());
        btnVideos.setOnClickListener(v -> showVideos());
        btnMovies.setOnClickListener(v -> showMovies());
        btnPodcasts.setOnClickListener(v -> showPodcasts());
    }

    private void showBooks() {
        setButtonColor(btnBooks);
        // Load books data and display
        List<ResourceType> books = loadCSVData(R.raw.books);  // Load articles.csv
        CarouselAdapter adapter = new CarouselAdapter(books, this);
        carouselRecyclerView.setAdapter(adapter);
    }

    private void showArticles() {
        setButtonColor(btnArticles);
        List<ResourceType> articles = loadCSVData(R.raw.articles);
        CarouselAdapter adapter = new CarouselAdapter(articles, this);
        carouselRecyclerView.setAdapter(adapter);
    }

    private void showVideos() {
        setButtonColor(btnVideos);
        List<ResourceType> articles = loadCSVData(R.raw.videos);
        CarouselAdapter adapter = new CarouselAdapter(articles, this);
        carouselRecyclerView.setAdapter(adapter);
    }
    private void showMovies() {
        setButtonColor(btnMovies);
        List<ResourceType> articles = loadCSVData(R.raw.movies);
        CarouselAdapter adapter = new CarouselAdapter(articles, this);
        carouselRecyclerView.setAdapter(adapter);
    }
    private void showPodcasts() {
        setButtonColor(btnPodcasts);
        List<ResourceType> articles = loadCSVData(R.raw.podcasts);
        CarouselAdapter adapter = new CarouselAdapter(articles, this);
        carouselRecyclerView.setAdapter(adapter);
    }

    // reset button colors
    private void setButtonColor(Button button) {
        // button is the one clicked - make it slate blue
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_btn));
        // all other buttons should be teal
        if (button != btnBooks) {
            btnBooks.setBackgroundDrawable(getResources().getDrawable(R.drawable.teal_btn));
        }
        if (button != btnArticles) {
            btnArticles.setBackgroundDrawable(getResources().getDrawable(R.drawable.teal_btn));
        }
        if (button != btnMovies) {
            btnMovies.setBackgroundDrawable(getResources().getDrawable(R.drawable.teal_btn));
        }
        if (button != btnVideos) {
            btnVideos.setBackgroundDrawable(getResources().getDrawable(R.drawable.teal_btn));
        }
        if (button != btnPodcasts) {
            btnPodcasts.setBackgroundDrawable(getResources().getDrawable(R.drawable.teal_btn));
        }
    }

    // Method to load CSV file and return a list of BookOrArticle
    private List<ResourceType> loadCSVData(int resourceId) {
        List<ResourceType> items = new ArrayList<>();
        try {
            // Open the raw resource (CSV file)
            InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(resourceId));
            CSVReader reader = new CSVReader(inputStreamReader);

            // Read all records from the CSV file
            List<String[]> records = reader.readAll();

            // Parse each record into a BookOrArticle object
            for (String[] record : records) {
                String title = record[0];        // Assuming 1st column is Title
                String description = record[1];  // Assuming 2nd column is Description
                String link = record[2];      // Assuming 3rd column is Link URL
                String icon = record[3];      // Assuming 4th column is icon name
                items.add(new ResourceType(title, description, link, icon));  // Add to list
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}