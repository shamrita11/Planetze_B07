package com.example.planetze.model;

public interface LoginModel {
    void login(String username, String password, OnListener listener);
    void sendPasswordResetEmail(String email, OnListener callback);

    interface OnListener {
        void onSuccess();
        void onFailure(String errorMsg);
    }
}
