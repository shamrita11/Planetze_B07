package com.example.planetze.presenter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.planetze.Dashboard;
import com.example.planetze.view.LoginView;
import com.example.planetze.model.LoginModel;
import com.example.planetze.model.LoginModelImpl;

public class LoginPresenterImpl implements LoginPresenter, LoginModel.OnLoginListener {
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
    public void onLoginSuccess() {
        loginView.showLoginSuccess();
    }

    @Override
    public void onLoginError(String errorMsg) {
        loginView.showLoginFailure();
    }
}
