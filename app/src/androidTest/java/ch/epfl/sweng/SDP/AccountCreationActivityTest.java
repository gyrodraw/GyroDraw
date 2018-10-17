package ch.epfl.sweng.SDP;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.Espresso.onView;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityTest {

    Account testAccount;

    @Rule
    public final ActivityTestRule<AccountCreationActivity> activityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    @Before
    public void initialize(){
        testAccount = new Account("testAccount", 100, 100);
        testAccount.setUserId("1234567890");
    }

    @Test
    public void testCreateAccIsClickable() {
        onView(withId(R.id.createAcc)).check(matches(isClickable()));
    }

    @Test
    public void testUsernameInputInputsCorrectly() {
        onView(withId(R.id.usernameInput)).perform(typeText("Max Muster"), closeSoftKeyboard())
                .check(matches(withText(R.string.test_name)));
    }

    @Test
    public void testAccountGetsCreated() {
        onView(withId(R.id.usernameInput)).perform(typeText("Max Muster"), closeSoftKeyboard());
        onView(withId(R.id.createAcc)).perform(click());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeTrophies() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.addStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUserName() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.changeUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAddFriend() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.addFriend(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullFriend() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.removeFriend(null);
    }
}