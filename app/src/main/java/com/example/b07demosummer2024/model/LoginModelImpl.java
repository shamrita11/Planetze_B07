package com.example.b07demosummer2024.model;
public class LoginModelImpl implements LoginModel {
    @Override
    public void login(String username, String password, OnLoginListener listener){
        if (username.isEmpty()) {
            listener.onLoginError("Username cannot be empty");
        } else if (password.isEmpty()) {
            listener.onLoginError("Password cannot be empty");
        } else {
            if (username.equals("user") && password.equals("password")) {
                listener.onLoginSuccess();
            } else {
                listener.onLoginError("Invalid username or password");
            }
        }
    }
}