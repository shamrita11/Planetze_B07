package com.example.planetze.presenter;

import android.util.Patterns;

import com.example.planetze.view.LoginView;
import com.example.planetze.model.LoginModel;
import com.example.planetze.model.LoginModelImpl;
import com.google.firebase.auth.FirebaseAuth;

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
    public void onSuccess() {
        loginView.showLoginSuccess();
    }

    @Override
    public void onFailure(String errorMsg) {
        loginView.showLoginFailure();
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
                loginView.showForgotPasswordFailure("Invalid email format.");
                return;
            }

            loginModel.sendPasswordResetEmail(email, new LoginModel.OnListener() {
                @Override
                public void onSuccess() {
                    loginView.showForgotPasswordSuccess();
                }

                @Override
                public void onFailure(String errorMsg) {
                    loginView.showForgotPasswordFailure(errorMsg);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        loginView = null;
    }
}
