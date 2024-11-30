package com.example.planetze.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planetze.Dashboard;
import com.example.planetze.QuestionnaireWelcomeActivity;
import com.example.planetze.R;
import com.example.planetze.SignUp;
import com.example.planetze.UserSession;
import com.example.planetze.Welcome;
import com.example.planetze.model.LoginModelImpl;
import com.example.planetze.presenter.LoginPresenter;
import com.example.planetze.presenter.LoginPresenterImpl;
import com.example.planetze.tracker.TrackerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin, buttonBack, buttonSignUp;
    private TextView forgotPasswordLink;
    private ProgressBar progressBar;
    private ImageView eyeIcon;

    private LoginPresenter loginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.email_input);
        editTextPassword = findViewById(R.id.password_input);
        buttonLogin = findViewById(R.id.login_button);
        buttonSignUp = findViewById(R.id.btn_signup);
        buttonBack = findViewById(R.id.btn_back);
        forgotPasswordLink = findViewById(R.id.forgot_password);
        progressBar = findViewById(R.id.progressBar);
        eyeIcon = findViewById(R.id.eyeIcon);

        loginPresenter = new LoginPresenterImpl(this, new LoginModelImpl());

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            loginPresenter.validateCredentials(username, password);
        });

        buttonBack.setOnClickListener(v -> {
            backClicked();
        });

        buttonSignUp.setOnClickListener(v -> {
            signUpClicked();
        });

        forgotPasswordLink.setOnClickListener(v -> {
            String email = editTextUsername.getText().toString().trim();
            loginPresenter.onForgotPasswordClicked(email);
        });

        eyeIcon.setOnClickListener(v -> eyeIconClicked());
    }

    @Override
    public void showUsernameError() {
        editTextUsername.setError("Username cannot be empty");
    }

    @Override
    public void showPasswordError() {
        editTextPassword.setError("Password cannot be empty");
    }

    @Override
    public void showLoginSuccess() {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            // Retrieve the current user's ID from UserSession
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UserSession.setUserId(userId); // Store it in UserSession for global access

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Retrieve the `on_boarded` variable from Firebase

            ref.child("on_boarded").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean onBoarded = snapshot.getValue(Boolean.class);

                    if (onBoarded != null && onBoarded) {
                        // Navigate to TrackerActivity if on_boarded is true
                        Intent intent = new Intent(getApplicationContext(), TrackerActivity.class);
                        startActivity(intent);
                    } else {
                        // Navigate to QuestionnaireActivity if on_boarded is false
                        Intent intent = new Intent(getApplicationContext(), QuestionnaireWelcomeActivity.class);
                        startActivity(intent);
                    }
                    finish(); // Close the current activity
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential database errors
                    Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Database error: " + error.getMessage());
                }
            });
        }

    @Override
    public void showLoginFailure() {
        Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show();

        hideProgress();
    }

    @Override
    public void navigateToTracker() {
        Intent intent = new Intent(getApplicationContext(), TrackerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToQuestionnaire() {
        Intent intent = new Intent(getApplicationContext(), QuestionnaireWelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void backClicked() {
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void signUpClicked() {
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void eyeIconClicked() {
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

    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showForgotPasswordSuccess() {
        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFailureMessage(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        loginPresenter.onDestroy();
        super.onDestroy();
    }
}
