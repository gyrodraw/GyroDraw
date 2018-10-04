package ch.epfl.sweng.SDP;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityTest {

    @Rule
    public final ActivityTestRule<AccountCreationActivity> mActivityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    // Add a monitor for the login activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    @Test
    public void testCanOpenLoginActivity() {
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(homeActivity);
    }

    @Test
    public void testCreateAccountButtonIsClickable() {
        onView(withId(R.id.create_account_button)).check(matches(isClickable()));
    }

    @Test
    public void preferencesAreUpdatedOnAccountCreation() {
        onView(withId(R.id.create_account_button)).perform(click());
        assertTrue(getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
                .getBoolean("hasAccount", false));
    }
}