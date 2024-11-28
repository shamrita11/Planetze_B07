package com.example.planetze.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

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