package ch.epfl.sweng.SDP.auth;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityTest {

    @Rule
    public final ActivityTestRule<AccountCreationActivity> mActivityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    @Test
    public void testCanOpenHomeActivity() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.create_account_button)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
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