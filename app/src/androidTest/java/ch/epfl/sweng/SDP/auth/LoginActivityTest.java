package ch.epfl.sweng.SDP.auth;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.matcher.ViewMatchers;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;

import static junit.framework.TestCase.assertTrue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class LoginActivityTest {

    @Rule
    public final ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    // Add a monitor for the accountCreation activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(AccountCreationActivity.class.getName(), null, false);

    // Add a monitor for the HomeActivity
    private final Instrumentation.ActivityMonitor homeMonitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    private IdpResponse mockIdpResponse;
    private Intent mockIntent;

    /**
     * Initializes the mock objects.
     */
    @Before
    public void init() {
        mockIdpResponse = Mockito.mock(IdpResponse.class);
        mockIntent = Mockito.mock(Intent.class);
        Mockito.when(mockIdpResponse.getEmail()).thenReturn("testEmail");
        Mockito.when(mockIntent.getParcelableExtra(ExtraConstants.IDP_RESPONSE))
                .thenReturn(mockIdpResponse);
    }

    @Test
    public void testFailedLoginNullResponse() {
        Mockito.when(mockIntent.getParcelableExtra(ExtraConstants.IDP_RESPONSE))
                .thenReturn(null);
        activityRule.getActivity().onActivityResult(42, 0, mockIntent);
        assertTrue(activityRule.getActivity().isFinishing());
    }

    @Test
    public void testSuccessfulLoginNewUser() {
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(true);
        activityRule.getActivity().onActivityResult(42, -1, mockIntent);
        assertTrue(activityRule.getActivity().isFinishing());
        Activity accountCreationActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(accountCreationActivity);
    }

    @Test
    public void testSuccessfulLoginExistingUser() {
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(false);
        activityRule.getActivity().onActivityResult(42, -1, mockIntent);
        SystemClock.sleep(3000);
        assertTrue(activityRule.getActivity().isFinishing());
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(homeMonitor, 5000);
        Assert.assertNotNull(homeActivity);
    }

    @Test
    public void testSuccessfulLoginWithoutAccount() {
        Mockito.when(mockIdpResponse.getEmail()).thenReturn("weirdEmail");
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(false);
        activityRule.getActivity().onActivityResult(42, -1, mockIntent);
        Activity accountCreationActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(accountCreationActivity);
    }

    @Test
    public void testFailedLoginNoNetwork() {
        failedSignInHelper(R.string.no_internet, ErrorCodes.NO_NETWORK);
    }

    @Test
    public void testFailedLoginUnknownError() {
        failedSignInHelper(R.string.unknown_error, 1234567891);
    }

    private void failedSignInHelper(int expectedErrorMessageId, final int errorCode) {
        executeOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().handleFailedSignIn(errorCode);
            }
        });

        TextView feedbackView = activityRule.getActivity().findViewById(R.id.error_message);
        ViewMatchers.assertThat(feedbackView.getText().toString(), is(equalTo(
                activityRule.getActivity().getResources().getString(expectedErrorMessageId))));
    }

    private void executeOnUiThread(Runnable runnable) {
        try {
            runOnUiThread(runnable);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}