package com.example.planetze.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planetze.Dashboard;
import com.example.planetze.R;
import com.example.planetze.Welcome;
import com.example.planetze.presenter.LoginPresenter;
import com.example.planetze.presenter.LoginPresenterImpl;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private LoginPresenter loginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.email_input);
        editTextPassword = findViewById(R.id.password_input);
        buttonLogin = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        loginPresenter = new LoginPresenterImpl(this);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            loginPresenter.validateCredentials(username, password);
        });
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
        Toast.makeText(this, "Login Failed. Check credentials.", Toast.LENGTH_SHORT).show();

        hideProgress();
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
    protected void onDestroy() {
        loginPresenter.onDestroy();
        super.onDestroy();
    }
}
