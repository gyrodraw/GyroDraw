package ch.epfl.sweng.SDP.auth;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.changeOnlineStatus;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class LoginActivityTest {

    private static final int RANDOM_UNKNOWN_ERROR_CODE = 1201234908;

    @Rule
    public final ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    // Add a monitor for the AccountCreationActivity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(AccountCreationActivity.class.getName(), null, false);

    // Add a monitor for the HomeActivity
    private final Instrumentation.ActivityMonitor homeMonitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    private IdpResponse mockIdpResponse;
    private Intent mockIntent;
    private LoginActivity loginActivity;

    /**
     * Initializes the mock objects.
     */
    @Before
    public void init() {
        loginActivity = activityRule.getActivity();
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
        loginActivity.onActivityResult(42, 0, mockIntent);
        assertThat(loginActivity.isFinishing(), is(true));
    }

    @Test
    public void testSuccessfulLoginNewUser() {
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(true);
        loginActivity.onActivityResult(42, -1, mockIntent);
        assertThat(loginActivity.isFinishing(), is(true));
        Activity accountCreationActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        assertThat(accountCreationActivity, is(not(nullValue())));
    }

    @Test
    public void testSuccessfulLoginExistingUser() {
        changeOnlineStatus(Account.getInstance(loginActivity).getUserId(), OFFLINE);
        Account.deleteAccount();
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(false);
        loginActivity.onActivityResult(42, -1, mockIntent);
        SystemClock.sleep(3000);
        assertThat(loginActivity.isFinishing(), is(true));
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(homeMonitor, 5000);
        assertThat(homeActivity, is(not(nullValue())));
    }

    @Test
    public void testSuccessfulLoginWithoutAccount() {
        Mockito.when(mockIdpResponse.getEmail()).thenReturn("weirdEmail");
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(false);
        loginActivity.onActivityResult(42, -1, mockIntent);
        Activity accountCreationActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        assertThat(accountCreationActivity, is(not(nullValue())));
    }

    @Test
    public void testFailedLoginNoNetwork() {
        failedSignInHelper(R.string.no_internet, ErrorCodes.NO_NETWORK);
    }

    @Test
    public void testFailedLoginUnknownError() {
        failedSignInHelper(R.string.unknown_error, RANDOM_UNKNOWN_ERROR_CODE);
    }

    private void failedSignInHelper(int expectedErrorMessageId, final int errorCode) {
        executeOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().handleFailedSignIn(errorCode);
            }
        });

        TextView feedbackView = loginActivity.findViewById(R.id.errorMessageLogin);
        ViewMatchers.assertThat(feedbackView.getText().toString(), is(equalTo(
                loginActivity.getResources().getString(expectedErrorMessageId))));
    }

    private void executeOnUiThread(Runnable runnable) {
        try {
            runOnUiThread(runnable);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testPressingBackButtonDoesNothing() {
        loginActivity.onBackPressed();
        assertThat(loginActivity.isFinishing(), is(false));
    }
}