package ch.epfl.sweng.SDP.auth;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.firebase.database.DataSnapshot;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.HashMap;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(JUnit4.class)
public class LoginActivityTest {

    @Rule
    public final ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    // Add a monitor for the accountCreation activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(AccountCreationActivity.class.getName(), null, false);

    private IdpResponse mockIdpResponse;
    private LoginActivity loginActivity;

    /**
     * Initializes the mock objects.
     */
    @Before
    public void init() {
        mockIdpResponse = Mockito.mock(IdpResponse.class);
        loginActivity = activityRule.getActivity();
    }

    @Test
    public void testFailedLoginNullResponse() {
        Intent mockIntent = Mockito.mock(Intent.class);
        Mockito.when(mockIntent.getParcelableExtra(ExtraConstants.IDP_RESPONSE))
                .thenReturn(null);
        loginActivity.onActivityResult(42, 0, mockIntent);
        assertThat(loginActivity.isFinishing(), is(true));
    }

    @Test
    public void testSuccessfulLoginNewUser() {
        Intent mockIntent = Mockito.mock(Intent.class);
        Mockito.when(mockIntent.getParcelableExtra(ExtraConstants.IDP_RESPONSE))
                .thenReturn(mockIdpResponse);
        Mockito.when(mockIdpResponse.isNewUser()).thenReturn(true);
        loginActivity.onActivityResult(42, -1, mockIntent);
        assertTrue(loginActivity.isFinishing());
        Activity accountCreationActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        assertThat(accountCreationActivity, is(not(nullValue())));
    }

    @Test
    public void testCloneAccountFromFirebase() {
        DataSnapshot mockSnapshot = Mockito.mock(DataSnapshot.class);
        HashMap<String, HashMap<String, Object>> userEntry = new HashMap<>();
        Mockito.when(mockSnapshot.getValue())
                .thenReturn(userEntry);
        assertThat(mockSnapshot.getValue(), CoreMatchers.<Object>is(userEntry));
    }
}
