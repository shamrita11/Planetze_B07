package com.example.planetze.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginModelImpl implements LoginModel {
    FirebaseDatabase database;

    @Override
    public void login(String username, String password, OnLoginListener listener){
        if (username.isEmpty()) {
            listener.onLoginError("Username cannot be empty");
        } else if (password.isEmpty()) {
            listener.onLoginError("Password cannot be empty");
        } else {
            database = FirebaseDatabase.getInstance("https://planetze-g16-default-rtdb.firebaseio.com/");
            DatabaseReference usersRef = database.getReference("users");

            usersRef.child(username).child("password").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String passwordFromDatabase = task.getResult().getValue(String.class);
                    if (passwordFromDatabase != null && passwordFromDatabase.equals(password)) {
                        listener.onLoginSuccess();
                    } else {
                        listener.onLoginError("Invalid username or password");
                    }
                } else {
                    listener.onLoginError("Failed to retrieve data");
                }
            });
        }
    }
}