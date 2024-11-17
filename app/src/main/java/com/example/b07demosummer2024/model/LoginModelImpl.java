package com.example.b07demosummer2024.model;

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
            DatabaseReference query = usersRef.child(username);

            if (usersRef.child(username).child("password").getValue(String.class).equals(password)) {
                listener.onLoginSuccess();
            } else {
                listener.onLoginError("Invalid username or password");
            }
        }
    }
}