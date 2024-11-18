package com.example.planetze;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// PLACEHOLDER CLASS
// Snow's creating this class

public class Login extends AppCompatActivity {

    private EditText editTextPassword;
    private Button buttonSignUp, buttonBack;
    private ImageView eyeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignUp = findViewById(R.id.btn_signup);
        buttonBack = findViewById(R.id.btn_back);
        eyeIcon = findViewById(R.id.eyeIcon);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        });

        // Initially set the input type to password
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        // Set the listener for the eye icon to toggle password visibility
        eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // Show password
                    editTextPassword.setTransformationMethod(null);
                    eyeIcon.setImageResource(R.drawable.icon_open_eye);  // Change to the 'eye open' icon
                } else {
                    // Hide password
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyeIcon.setImageResource(R.drawable.icon_closed_eye);  // Change back to the 'eye closed' icon
                }
            }
        });
    }
}