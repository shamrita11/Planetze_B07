package com.example.planetze;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import com.example.planetze.presenter.LoginPresenter;
import com.example.planetze.presenter.LoginPresenterImpl;
import com.example.planetze.view.LoginView;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
//    @Test
//    public void testLoginEmptyEmail() {
//        LoginPresenter presenter = new LoginPresenterImpl(loginView);
//        presenter.validateCredentials("", "password"); // email is empty
//
//        verify(loginView).showProgress();
//        verify(loginView).showLoginFailure();
//        verify(loginView).hideProgress();
//    }
}