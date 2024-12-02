package com.example.planetze.presenter;

import com.example.planetze.view.LoginView;
import com.example.planetze.model.LoginModel;

import java.util.regex.Pattern;

public class LoginPresenterImpl implements LoginPresenter, LoginModel.OnListener {
    private LoginView loginView;
    private final LoginModel loginModel;
    private static final Pattern EMAIL_ADDRESS_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public LoginPresenterImpl(LoginView loginView, LoginModel loginModel) {
        this.loginView = loginView;
        this.loginModel = loginModel;
    }

    @Override
    public void validateCredentials(String username, String password) {
        if (loginView != null) {
            loginView.showProgress();
        }

        if (username.isEmpty()) {
            if (loginView != null) {
                loginView.hideProgress();
                loginView.showUsernameError();
                return;
            }
        }

        if (password.isEmpty()) {
            if (loginView != null) {
                loginView.hideProgress();
                loginView.showPasswordError();
                return;
            }
        }
        loginModel.login(username, password, this);
    }

    @Override
    public void checkOnBoardingStatus(String userId) {
        loginModel.fetchOnBoardingStatus(userId, new LoginModel.OnListener() {
            @Override
            public void onOnBoardingStatusFetched(Boolean onBoarded) {
                if (loginView != null) {
                    if (onBoarded) {
                        loginView.navigateToTracker();
                    } else {
                        loginView.navigateToQuestionnaire();
                    }
                }
            }

            @Override
            public void onSuccess() {
                // Handle success if needed
                if (loginView != null) {
                    loginView.showSuccess("onBoardingStatus fetched successfully.");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                if (loginView != null) {
                    loginView.showFailureMessage(errorMsg);
                }
            }
        });
    }

    @Override
    public void onSuccess() {
        loginView.showLoginSuccess();
    }

    @Override
    public void onFailure(String errorMsg) {
        loginView.showLoginFailure();
    }

    @Override
    public void onOnBoardingStatusFetched(Boolean onBoarded) {
        loginView.hideProgress();
    }

    @Override
    public void onForgotPasswordClicked(String email) {
        if (loginView != null) {
            if (email.isEmpty()) {
                loginView.showUsernameError();
                return;
            }

            if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
                loginView.hideProgress();
                loginView.showFailureMessage("Invalid email format.");
                return;
            }

            loginModel.sendPasswordResetEmail(email, new LoginModel.OnListener() {
                @Override
                public void onSuccess() {
                    loginView.showForgotPasswordSuccess();
                }

                @Override
                public void onFailure(String errorMsg) {
                    loginView.showFailureMessage(errorMsg);
                }

                @Override
                public void onOnBoardingStatusFetched(Boolean onBoarded) {
                    loginView.hideProgress();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        loginView = null;
    }
}
