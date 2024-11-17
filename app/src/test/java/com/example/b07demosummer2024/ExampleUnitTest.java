package com.example.b07demosummer2024;

import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void testPresenter() {
        when(loginView.getUsername()).thenReturn("test1");
        when(loginModel.isValidUsername("test1")).thenReturn(true);)
    }
}