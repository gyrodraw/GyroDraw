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

    @Test(expected = DatabaseException.class)
    public void testGetStars() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.getStars();
        testAccount.subtractStars(10);
    }

    @Test(expected = DatabaseException.class)
    public void testAddStars() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.getStars();
        testAccount.addStars(20);
    }

    @Test(expected = DatabaseException.class)
    public void testSubtractStars() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.subtractStars(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeTrophies() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.addStars(-10);
    }

    @Test(expected = DatabaseException.class)
    public void testChangeTrophies() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.changeTrophies(20);
        testAccount.getTrophies();
    }

    @Test(expected = DatabaseException.class)
    public void testChangeUsername() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();
        testAccount.changeUsername(timestamp);
        testAccount.getUsername();
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

    @Test(expected = DatabaseException.class)
    public void testAddFriend() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.addFriend("123456789");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullFriend() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.removeFriend(null);
    }

    @Test(expected = DatabaseException.class)
    public void testRemoveFriend() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        testAccount.removeFriend("123456789");
    }
}