package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template);

        ImageButton trackerButton = findViewById(R.id.trackerButton);
        ImageButton gaugeButton = findViewById(R.id.gaugeButton);
        ImageButton hubButton = findViewById(R.id.hubButton);
        ImageButton balanceButton = findViewById(R.id.balanceButton);
        ImageButton userButton = findViewById(R.id.userButton);

        gaugeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EcoGaugeActivity.class));
            }
        });

        balanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EcoBalanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
