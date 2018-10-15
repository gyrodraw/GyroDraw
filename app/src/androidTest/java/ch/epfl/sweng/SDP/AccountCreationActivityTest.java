package ch.epfl.sweng.SDP;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static android.support.test.espresso.Espresso.onView;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityTest {

    @Rule
    public final ActivityTestRule<AccountCreationActivity> activityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

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

    @Test(expected = DatabaseException.class)
    public void testGetStars() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.getStars();
        testAccount.subtractStars(10);
        //assertEquals("Adding stars does not yield right result", stars+20, testAccount.getStars());
    }

    @Test(expected = DatabaseException.class)
    public void testAddStars() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.getStars();
        testAccount.addStars(20);
        //assertEquals("Adding stars does not yield right result", stars+20, testAccount.getStars());
    }

    @Test(expected = DatabaseException.class)
    public void testSubtractStars() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.subtractStars(10);
        //assertEquals("Adding stars does not yield right result", stars+20, testAccount.getStars());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeTrophies() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.addStars(-10);
    }

    @Test(expected = DatabaseException.class)
    public void testChangeTrophies() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.changeTrophies(20);
        testAccount.getTrophies();
        //assertEquals("Adding stars does not yield right result", 20, testAccount.getTrophies());
    }

    @Test(expected = DatabaseException.class)
    public void testChangeUsername() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.changeUsername("newName");
        testAccount.getUsername();
        //assertEquals("Adding stars does not yield right result", 20, testAccount.getTrophies());
    }

    @Test(expected = NullPointerException.class)
    public void testNullUserName() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.changeUsername(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullAddFriend() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.addFriend(null);
    }

    @Test(expected = DatabaseException.class)
    public void testAddFriend() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.addFriend("123456789");
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNullFriend() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.removeFriend(null);
    }

    @Test(expected = DatabaseException.class)
    public void testRemoveFriend() {
        Account testAccount = activityRule.getActivity().getAccount();
        testAccount.removeFriend("123456789");
    }
}