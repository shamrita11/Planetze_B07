package com.example.planetze.model;

import androidx.annotation.NonNull;

import com.example.planetze.UserSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public void fetchOnBoardingStatus(String userId, OnListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
        ref.child("on_boarded").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean onBoarded = snapshot.getValue(Boolean.class);
                listener.onOnBoardingStatusFetched(onBoarded);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure("Error fetching on_boarded status: " + error.getMessage());
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