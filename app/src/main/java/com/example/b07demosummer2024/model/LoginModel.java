package com.example.b07demosummer2024.model;

public interface LoginModel {
    void login(String username, String password, OnLoginListener listener);

    interface OnLoginListener {
        void onLoginSuccess();
        void onLoginError(String errorMsg);
    }
}
