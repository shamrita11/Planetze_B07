package com.example.planetze.model;

public interface LoginModel {
    void login(String username, String password, OnListener listener);
    void sendPasswordResetEmail(String email, OnListener callback);

    void fetchOnBoardingStatus(String userId, OnListener onListener);

    interface OnListener {
        void onSuccess();
        void onFailure(String errorMsg);

        void onOnBoardingStatusFetched(Boolean onBoarded);
    }
}
