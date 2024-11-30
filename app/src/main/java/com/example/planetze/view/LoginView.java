package com.example.planetze.view;

public interface LoginView {
    void showUsernameError();
    void showPasswordError();
    void showLoginSuccess();
    void showLoginFailure();
    void showSuccess(String message);
    void navigateToTracker();
    void navigateToQuestionnaire();
    void backClicked();
    void signUpClicked();
    void eyeIconClicked();
    void showProgress();
    void hideProgress();
    void showForgotPasswordSuccess();
    void showFailureMessage(String errorMsg);
}
