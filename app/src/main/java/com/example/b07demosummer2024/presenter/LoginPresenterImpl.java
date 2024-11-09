package com.example.b07demosummer2024.presenter;

import com.example.b07demosummer2024.view.LoginView;
import com.example.b07demosummer2024.model.LoginModel;
import com.example.b07demosummer2024.model.LoginModelImpl;

public class LoginPresenterImpl implements LoginPresenter, LoginModel.OnLoginListener {
    private LoginView loginView;
    private LoginModel loginModel;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        this.loginModel = new LoginModelImpl();
    }

    @Override
    public void validateCredentials(String username, String password) {
        if (loginView != null) {
            loginView.showProgress();
        }
        loginModel.login(username, password, this);
    }

    @Override
    public void onDestroy() {
        loginView = null; // Avoid memory leaks
    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginError(String errorMsg) {

    }
}
