package com.example.planetze.presenter;

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
        loginModel.login(username, password, this);
    }

    @Override
    public void onDestroy() {
        loginView = null; // to avoid memory leaks
    }

    @Override
    public void onLoginSuccess() {

    }

    @Override
    public void onLoginError(String errorMsg) {

    }
}
