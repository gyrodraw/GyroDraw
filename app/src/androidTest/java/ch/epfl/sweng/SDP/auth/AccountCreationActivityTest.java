package ch.epfl.sweng.SDP.auth;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotEquals;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.SDP.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityTest {

    @Rule
    public final ActivityTestRule<AccountCreationActivity> activityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    @Test
    public void testCreateAccIsClickable() {
        onView(ViewMatchers.withId(R.id.createAcc)).check(matches(isClickable()));
    }

    @Test
    public void testCreateAccountWithNullName() {
        onView(ViewMatchers.withId(R.id.createAcc)).perform(click());
        onView(ViewMatchers.withId(R.id.usernameTaken))
                .check(matches(withText("Username must not be empty.")));
    }

    @Test
    public void testUsernameInputInputsCorrectly() {
        onView(withId(R.id.usernameInput))
                .perform(typeText("Max Muster"), closeSoftKeyboard())
                .check(matches(withText(R.string.test_name)));
    }

    @Test
    public void testCreateAccountWithValidInput() {
        onView(withId(R.id.usernameInput))
                .perform(typeText("Max Muster"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.createAcc)).perform(click());
        assertNotEquals(null, Account.getInstance(activityRule.getActivity()));
    }
}