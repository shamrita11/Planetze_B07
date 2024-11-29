package com.example.planetze;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class EcoAgent extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_eco_agent; // Ensure this matches your XML layout file name
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView ivGif = findViewById(R.id.gif_eco_agent);
        Glide.with(this).load(R.drawable.gif_recycle).into(ivGif);
    }
}