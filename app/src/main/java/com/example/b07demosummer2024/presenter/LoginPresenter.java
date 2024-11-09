package com.example.b07demosummer2024.presenter;

public interface LoginPresenter {
    void validateCredentials(String username, String password);
    void onDestroy(); // Optional, to clean up resources
}
