package com.example.planetze.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

public class LoginModelImpl implements LoginModel {
    FirebaseAuth database;

    public LoginModelImpl() {
        this.database = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password, OnListener listener){
        if (email.isEmpty()) {
            listener.onFailure("Username cannot be empty");
        }
        if (password.isEmpty()) {
            listener.onFailure("Password cannot be empty");
        }

        // if user verified their email and user cannot be null
        if (Objects.requireNonNull(database.getCurrentUser()).isEmailVerified()) {
            database.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Successful login
                            listener.onSuccess();
                        } else {
                            // Failed login
                            String errorMessage = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Authentication failed";
                            listener.onFailure(errorMessage);
                        }
                    });
        } else {
            listener.onFailure("Please verify your email first");
        }
    }

    @Override
    public void sendPasswordResetEmail(String email, OnListener listener){
        database.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }
}