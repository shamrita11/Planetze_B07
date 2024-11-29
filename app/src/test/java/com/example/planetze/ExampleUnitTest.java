package com.example.planetze;

import com.example.planetze.model.LoginModel;
import com.example.planetze.presenter.LoginPresenter;
import com.example.planetze.presenter.LoginPresenterImpl;
import com.example.planetze.view.LoginView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.widget.ProgressBar;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    LoginView loginView;

    @Mock
    LoginModel loginModel;

    private LoginPresenter loginPresenter;

    @Before
    public void setUp() {
//        FirebaseApp.initializeApp(Mockito.mock(Context.class));  // Initialize Firebase App
//        MockitoAnnotations.openMocks(this);  // Initialize mocks
//        loginPresenter = new LoginPresenterImpl(loginView);  // Create the presenter

    }

    // Test: Successful login
    @Test
    public void testValidateCredentials_successfulLogin() {
        // Arrange
        String username = "shisnow2005@gmail.com";
        String password = "test1234";
        loginPresenter = new LoginPresenterImpl(loginView);
//        doAnswer(invocation -> {
//            LoginModel.OnListener listener = invocation.getArgument(2);
//            listener.onSuccess();  // Simulate successful login
//            return null;
//        }).when(loginModel).login(eq(username), eq(password), any(LoginModel.OnListener.class));

        // Act
//        loginPresenter.validateCredentials(username, password);

        // Assert
//        verify(loginView).showProgress();
//        verify(loginView).hideProgress();
//        verify(loginView).showLoginSuccess();
    }

    // Test: Failed login due to empty username
//    @Test
//    public void testValidateCredentials_emptyUsername() {
//        // Arrange
//        String username = "";
//        String password = "test1234";
//
//        // Act
//        loginPresenter.validateCredentials(username, password);
//
//        // Assert
////        verify(loginView).showProgress();
////        verify(loginView).hideProgress();
//        verify(loginView).showUsernameError();
//    }
//
//    // Test: Failed login due to empty password
//    @Test
//    public void testValidateCredentials_emptyPassword() {
//        // Arrange
//        String username = "janedoe@mail.com";
//        String password = "";
//
//        // Act
//        loginPresenter.validateCredentials(username, password);
//
//        // Assert
////        verify(loginView).showProgress();
////        verify(loginView).hideProgress();
//        verify(loginView).showPasswordError();
//    }
//
//    // Test: Failed login due to empty username and password
//    @Test
//    public void testValidateCredentials_emptyUsernameAndPassword() {
//        // Arrange
//        String username = "";
//        String password = "";
//
//        // Act
//        loginPresenter.validateCredentials(username, password);
//
//        // Assert
////        verify(loginView).showProgress();
////        verify(loginView).hideProgress();
//        verify(loginView).showUsernameError();
//    }
//
//    // Test: Forgot password clicked with empty email
//    @Test
//    public void testOnForgotPasswordClicked_emptyEmail() {
//        // Arrange
//        String email = "";
//
//        // Act
//        loginPresenter.onForgotPasswordClicked(email);
//
//        // Assert
//        verify(loginView).showUsernameError();  // Email is empty, so show error
//    }
//
//    // Test: Forgot password clicked with valid email
//    @Test
//    public void testOnForgotPasswordClicked_validEmail() {
//        // Arrange
//        String email = "janedoe@mail.com";
//        doAnswer(invocation -> {
//            LoginModel.OnListener listener = invocation.getArgument(1);
//            listener.onSuccess();  // Simulate success in password reset
//            return null;
//        }).when(loginModel).sendPasswordResetEmail(eq(email), any(LoginModel.OnListener.class));
//
//        // Act
//        loginPresenter.onForgotPasswordClicked(email);
//
//        // Assert
//        verify(loginView).showForgotPasswordSuccess();
//    }
//
//    // Test: Presenter onDestroy
//    @Test
//    public void testOnDestroy() {
//        // Act
//        loginPresenter.onDestroy();
//
//        // Assert
////        assertNull(loginPresenter.loginView);  // Check that the view is cleared to avoid memory leaks
//    }
}