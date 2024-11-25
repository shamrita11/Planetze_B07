package com.example.planetze.presenter;

public interface LoginPresenter {
    void validateCredentials(String username, String password);
    void onForgotPasswordClicked(String email);
    void onBackClicked();
    void onDestroy(); // Optional, to clean up resources
}
