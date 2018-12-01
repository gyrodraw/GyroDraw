package ch.epfl.sweng.SDP;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;

import ch.epfl.sweng.SDP.auth.LoginActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    // Add a monitor for the login activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(LoginActivity.class.getName(), null, false);

    @Test
    public void testCanOpenLoginActivity() {
        onView(withId(R.id.login_button)).perform(click());
        Activity loginActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(loginActivity);
    }

    @Test
    public void testCloneAccountFromFirebase() {
        DataSnapshot snapshot = Mockito.mock(DataSnapshot.class);
        when(snapshot.getValue()).thenReturn(new HashMap<String, HashMap<String, Object>>());
        mActivityRule.getActivity().cloneAccountFromFirebase(snapshot);
    }

}