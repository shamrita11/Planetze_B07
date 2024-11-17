package com.example.b07demosummer2024.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.b07demosummer2024.R;
import com.example.b07demosummer2024.presenter.LoginPresenter;
import com.example.b07demosummer2024.presenter.LoginPresenterImpl;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends Activity implements LoginView {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;

    private LoginPresenter loginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
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
        // Redirect to the next screen or activity
    }

    @Override
    public void showLoginFailure() {
        Toast.makeText(this, "Login Failed. Check credentials.", Toast.LENGTH_SHORT).show();
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
