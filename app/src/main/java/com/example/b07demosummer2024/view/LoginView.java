package com.example.b07demosummer2024.view;

public interface LoginView {
    void showUsernameError();
    void showPasswordError();
    void showLoginSuccess();
    void showLoginFailure();
    void showProgress();
    void hideProgress();
}
