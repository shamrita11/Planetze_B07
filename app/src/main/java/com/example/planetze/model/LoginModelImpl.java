package com.example.planetze.model;

import com.example.planetze.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

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

        // if the email is verified, then login
        database.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Successful login
                    FirebaseUser user = database.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        UserSession.setUserId(user.getUid());
                        listener.onSuccess();
                    } else {
                        database.signOut();
                        listener.onFailure("Email not verified");
                    }
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
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            listener.onFailure("Email not found.");
                        } else {
                            listener.onFailure(e.getMessage());
                        }
                    }
                });
    }
}