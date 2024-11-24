package com.example.planetze.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class LoginModelImpl implements LoginModel {
    FirebaseAuth database;

    public LoginModelImpl() {
        this.database = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password, OnLoginListener listener){
        if (email.isEmpty()) {
            listener.onLoginError("Username cannot be empty");
        }
        if (password.isEmpty()) {
            listener.onLoginError("Password cannot be empty");
        }

        database.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successful login
                        listener.onLoginSuccess();
                    } else {
                        // Failed login
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Authentication failed";
                        listener.onLoginError(errorMessage);
                    }
                });
    }
}