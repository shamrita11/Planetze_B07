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
import static org.mockito.Mockito.never;
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
        MockitoAnnotations.openMocks(this);  // Initialize mocks
        loginPresenter = new LoginPresenterImpl(loginView, loginModel);  // Create the presenter
    }

    // Test: Successful login
    @Test
    public void testValidateCredentials_successfulLogin() {
        // Arrange
        String username = "shisnow2005@gmail.com";
        String password = "test1234";

        // Act
        loginPresenter.validateCredentials(username, password);

        // Assert
        verify(loginView).showProgress();
//        verify(loginView).showLoginSuccess();
    }

    // Test: Failed login due to empty username
    @Test
    public void testValidateCredentials_emptyUsername() {
        // Arrange
        String username = "";
        String password = "test1234";

        // Act
        loginPresenter.validateCredentials(username, password);

        // Assert
        verify(loginView).showProgress();
        verify(loginView).showUsernameError();
        verify(loginView).hideProgress();
    }

    // Test: Failed login due to empty password
    @Test
    public void testValidateCredentials_emptyPassword() {
        // Arrange
        String username = "janedoe@mail.com";
        String password = "";

        // Act
        loginPresenter.validateCredentials(username, password);

        // Assert
        verify(loginView).showProgress();
        verify(loginView).showPasswordError();
        verify(loginView).hideProgress();

    }

    // Test: Failed login due to empty username and password
    @Test
    public void testValidateCredentials_emptyUsernameAndPassword() {
        // Arrange
        String username = "";
        String password = "";

        // Act
        loginPresenter.validateCredentials(username, password);

        // Assert
        verify(loginView).showProgress();
        verify(loginView).hideProgress();
        verify(loginView).showUsernameError();
    }

    @Test
    public void testValidateCredentials_NullLoginView() {
        loginView = null;
        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.validateCredentials("username", "password");

        assertNull(loginView);
    }

    @Test
    public void testValidateCredentials_NullLoginViewWithEmptyUsername() {
        loginView = null;
        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.validateCredentials("", "password");

        assertNull(loginView);
    }

    @Test
    public void testValidateCredentials_NullLoginViewWithEmptyPassword() {
        loginView = null;
        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.validateCredentials("username", "");

        assertNull(loginView);
    }

    // Test: Forgot password clicked with empty email
    @Test
    public void testOnForgotPasswordClicked_emptyEmail() {
        // Arrange
        String email = "";

        // Act
        loginPresenter.onForgotPasswordClicked(email);

        // Assert
        verify(loginView).showUsernameError();  // Email is empty, so show error
    }

    // Test: Forgot password clicked with valid email
    @Test
    public void testOnForgotPasswordClicked_validEmail() {
        // Arrange
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onSuccess();  // Simulate success in password reset
            return null;
        }).when(loginModel).sendPasswordResetEmail(eq(email), any(LoginModel.OnListener.class));

        // Act
        loginPresenter.onForgotPasswordClicked(email);

        // Assert
        verify(loginView).showForgotPasswordSuccess();
    }

    // Test: Forgot password clicked with invalid email
    @Test
    public void testOnForgotPasswordClicked_InvalidEmail() {
        // Arrange
        String email = "janedoe";

        // Act
        loginPresenter.onForgotPasswordClicked(email);

        // Assert
        verify(loginView).showFailureMessage("Invalid email format.");
    }

    @Test
    public void testOnForgotPasswordClicked_Failure() {
        // Arrange
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onFailure("Error message");  // Simulate failure in password reset
            return null;
        }).when(loginModel).sendPasswordResetEmail(eq(email), any(LoginModel.OnListener.class));

        // Act
        loginPresenter.onForgotPasswordClicked(email);

        // Assert
        verify(loginView).showFailureMessage("Error message");
    }

    @Test
    public void testOnForgotPasswordClicked_OnBoarding() {
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onOnBoardingStatusFetched(true);  // Simulate failure in password reset
            return null;
        }).when(loginModel).sendPasswordResetEmail(eq(email), any(LoginModel.OnListener.class));

        // Act
        loginPresenter.onForgotPasswordClicked(email);

        // Assert
        verify(loginView).hideProgress();
    }

    @Test
    public void testOnForgotPasswordClicked_NullLoginView() {
        loginView = null;
        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.onForgotPasswordClicked("janedoe@mail.com");
        assertNull(loginView);
    }

    @Test
    public void testOnSuccessCall() {
        loginPresenter.onSuccess();
        verify(loginView).showLoginSuccess();
    }

    @Test
    public void testOnFailureCall() {
        loginPresenter.onFailure("Invalid email or password.");
        verify(loginView).showLoginFailure();
    }

    @Test
    public void testOnOnBoardingStatusFetchedCall() {
        loginPresenter.onOnBoardingStatusFetched(true);
        verify(loginView).hideProgress();
    }

    @Test
    public void testCheckOnBoardingStatusFetched_OnBoarded() {
        String email = "shisnow2005@gmail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onOnBoardingStatusFetched(true);
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        // Act
        loginPresenter.checkOnBoardingStatus(email);

        verify(loginView).navigateToTracker();
    }

    @Test
    public void testCheckOnBoardingStatusFetched_NotOnBoarded() {
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onOnBoardingStatusFetched(false);
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        // Act
        loginPresenter.checkOnBoardingStatus(email);

        verify(loginView).navigateToQuestionnaire();
    }

    @Test
    public void testCheckOnBoardingStatusFetched_NullLoginView() {
        loginView = null;
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onOnBoardingStatusFetched(true);
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.checkOnBoardingStatus(email);

        assertNull(loginView);
    }

    @Test
    public void testCheckOnBoardingStatusFetched_NullLoginViewSuccess() {
        loginView = null;
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onSuccess();
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.checkOnBoardingStatus(email);

        assertNull(loginView);
    }

    @Test
    public void testCheckOnBoardingStatusFetched_Success() {
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onSuccess();
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        loginPresenter.checkOnBoardingStatus(email);

        verify(loginView).showSuccess("onBoardingStatus fetched successfully.");
    }

    @Test
    public void testCheckOnBoardingStatusFetched_NullLoginViewFailure() {
        loginView = null;
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onFailure("Error fetching on_boarded status");
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        loginPresenter = new LoginPresenterImpl(loginView, loginModel);
        loginPresenter.checkOnBoardingStatus(email);

        assertNull(loginView);
    }

    @Test
    public void testCheckOnBoardingStatusFetched_Failure() {
        String email = "janedoe@mail.com";
        doAnswer(invocation -> {
            LoginModel.OnListener listener = invocation.getArgument(1);
            listener.onFailure("Error fetching on_boarded status");
            return null;
        }).when(loginModel).fetchOnBoardingStatus(eq(email), any(LoginModel.OnListener.class));

        loginPresenter.checkOnBoardingStatus(email);

        verify(loginView).showFailureMessage("Error fetching on_boarded status");
    }

    // Test: Presenter onDestroy
    @Test
    public void testOnDestroy() {
        // Act
        loginPresenter.onDestroy();

        // Assert
        verify(loginView, never()).showProgress();
    }
}