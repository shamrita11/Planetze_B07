package com.example.planetze.presenter;

public interface LoginPresenter {
    void validateCredentials(String username, String password);
    void onForgotPasswordClicked(String email);
    void checkOnBoardingStatus(String userId);
    void onSuccess();
    void onFailure(String errorMsg);
    void onOnBoardingStatusFetched(Boolean onBoarded);
    void onDestroy(); // Optional, to clean up resources
}
