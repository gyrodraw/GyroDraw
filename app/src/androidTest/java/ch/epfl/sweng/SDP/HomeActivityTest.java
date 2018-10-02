package ch.epfl.sweng.SDP;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {
    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule<>(HomeActivity.class);

    // Add a monitor for the main activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(MainActivity.class.getName(), null, false);

    @Test
    public void testCanSignOut() {
        onView(withId(R.id.sign_out)).perform(click());
        Activity mainActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(mainActivity);
    }
}