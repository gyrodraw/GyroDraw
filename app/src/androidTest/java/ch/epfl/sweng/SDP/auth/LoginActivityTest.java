package ch.epfl.sweng.SDP.auth;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static junit.framework.TestCase.assertTrue;

@RunWith(JUnit4.class)
public class LoginActivityTest {

    @Rule
    public final ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    // Add a monitor for the login activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    Intent mockIntent;
    IdpResponse mockIdpResponse;
    FirebaseUiException mockError;
    LoginActivity loginActivity;

    /**
     * Initializes the mock objects. 
     */
    @Before
    public void init(){
        mockIntent = Mockito.mock(Intent.class);
        mockIdpResponse = Mockito.mock(IdpResponse.class);
        mockError = Mockito.mock(FirebaseUiException.class);
        loginActivity = activityRule.getActivity();
        Mockito.when(mockIntent.getParcelableExtra(ExtraConstants.IDP_RESPONSE))
                .thenReturn(mockIdpResponse);
    }

    @Test
    public void testExistingUser(){
        getDefaultSharedPreferences(activityRule.getActivity()).edit()
                .putBoolean("hasAccount", true).apply();
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(false);
        loginActivity.onActivityResult(42, -1, mockIntent);
        assertTrue(loginActivity.isFinishing());
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(homeActivity);
    }

    /**
     * Try with Powermock in future.
     */
    //@Test
    //public void testNoInternetConnection(){
    //    Mockito.when(mockIdpResponse.getError()).thenReturn(mockError);
    //    Mockito.when(mockError.getErrorCode()).thenReturn(1);
    //    loginActivity.onActivityResult(42, 0, mockIntent);
    //    assertEquals(((TextView)loginActivity.findViewById(R.id.error_message))
    //          .getText(), "No Internet connection");
    //}

    @Test
    public void testFailedLoginNullResponse(){
        mockIntent = Mockito.mock(Intent.class);
        mockIdpResponse = null;
        loginActivity.onActivityResult(42, 0, mockIntent);
        assertTrue(loginActivity.isFinishing());
    }

    @Test
    public void testSuccessfulLogin(){
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(true);
        loginActivity.onActivityResult(42, -1, mockIntent);
        assertTrue(loginActivity.isFinishing());
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(homeActivity);
    }
}
