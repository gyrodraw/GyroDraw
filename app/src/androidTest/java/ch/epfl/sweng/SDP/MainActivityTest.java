package ch.epfl.sweng.SDP;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    // Add a monitor for the login activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(LoginActivity.class.getName(), null, false);
    @Test
    public void testCanLogin() {
        onView(ViewMatchers.withId(R.id.login)).perform(click());
        Activity loginActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        assertNotNull(loginActivity);
    }

}