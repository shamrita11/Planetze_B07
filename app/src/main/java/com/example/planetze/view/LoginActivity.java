package com.example.planetze.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planetze.Dashboard;
import com.example.planetze.R;
import com.example.planetze.SignUp;
import com.example.planetze.Welcome;
import com.example.planetze.presenter.LoginPresenter;
import com.example.planetze.presenter.LoginPresenterImpl;

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
        buttonBack = findViewById(R.id.btn_back);
        buttonSignUp = findViewById(R.id.btn_signup);
        forgotPasswordLink = findViewById(R.id.forgot_password);
        progressBar = findViewById(R.id.progressBar);
        eyeIcon = findViewById(R.id.eyeIcon);

        loginPresenter = new LoginPresenterImpl(this);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            loginPresenter.validateCredentials(username, password);
        });

        buttonBack.setOnClickListener(v -> {
            loginPresenter.onBackClicked();
        });

        buttonSignUp.setOnClickListener(v -> {
            loginPresenter.onSignUpClicked();
        });

        forgotPasswordLink.setOnClickListener(v -> {
            String email = editTextUsername.getText().toString().trim();
            loginPresenter.onForgotPasswordClicked(email);
        });

        eyeIcon.setOnClickListener(v -> loginPresenter.onEyeIconClicked());
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

        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginFailure() {
        Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show();

        hideProgress();
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
    public void showForgotPasswordFailure(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        loginPresenter.onDestroy();
        super.onDestroy();
    }
}
