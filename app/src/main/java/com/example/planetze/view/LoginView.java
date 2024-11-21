package com.example.planetze.view;

public interface LoginView {
    void showUsernameError();
    void showPasswordError();
    void showLoginSuccess();
    void showLoginFailure();
    void showProgress();
    void hideProgress();
}
