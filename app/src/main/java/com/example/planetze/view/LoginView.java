package com.example.planetze.view;

public interface LoginView {
    void showUsernameError();
    void showPasswordError();
    void showLoginSuccess();
    void showLoginFailure();
    void showSuccess(String message);
    void backClicked();
    void eyeIconClicked();
    void showProgress();
    void hideProgress();
    void showForgotPasswordSuccess();
    void showForgotPasswordFailure(String errorMsg);
}
