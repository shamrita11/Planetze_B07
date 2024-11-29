package com.example.planetze.presenter;

import android.util.Patterns;

import com.example.planetze.view.LoginView;
import com.example.planetze.model.LoginModel;
import com.example.planetze.model.LoginModelImpl;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPresenterImpl implements LoginPresenter, LoginModel.OnListener {
    private LoginView loginView;
    private final LoginModel loginModel;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginModel = new LoginModelImpl();
    }

    @Override
    public void validateCredentials(String username, String password) {
        if (loginView != null) {
            loginView.showProgress();
        }

        if (username.isEmpty()) {
            assert loginView != null;
            loginView.hideProgress();
            loginView.showUsernameError();
            return;
        }

        if (password.isEmpty()) {
            assert loginView != null;
            loginView.hideProgress();
            loginView.showPasswordError();
            return;
        }
        loginModel.login(username, password, this);
    }

    @Override
    public void onDestroy() {
        loginView = null; // to avoid memory leaks
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

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                loginView.hideProgress();
                loginView.showUsernameError();
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
}
