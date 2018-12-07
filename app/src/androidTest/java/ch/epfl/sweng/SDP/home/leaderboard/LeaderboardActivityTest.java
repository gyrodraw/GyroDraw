package ch.epfl.sweng.SDP.home.leaderboard;

import android.content.Context;
import android.os.SystemClock;

import static android.support.test.espresso.Espresso.onView;

import android.support.test.espresso.action.ViewActions;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.support.test.espresso.intent.Intents;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    private static final String USER_ID = "123456789";
    private static final String TEST_EMAIL = "testEmail";
    private static final String USERNAME = "username";

    @Rule
    public final ActivityTestRule<LeaderboardActivity> activityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    private Account account;
    private Context context;

    /**
     * Sets all necessary values in account for testing purposes.
     */
    @Before
    public void initialize() {
        context = activityRule.getActivity().getApplicationContext();
        account = Account.getInstance(context);
        account.setEmail(TEST_EMAIL);
        account.setUsername(USERNAME);
        account.setUserId(USER_ID);
    }

    @Test
    public void testSearchFieldClickable() {
        onView(withId(R.id.searchField))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.searchField)).check(matches(isClickable()));
    }

    @Test
    public void testClickOnExitButtonOpensHomeActivity() {
        testExitButtonBody();
    }

    @Test
    public void testFriendsButtonsClickable() {
        String buttonTag = "friendsButton0";
        SystemClock.sleep(2000);
        onView(withTagValue(is((Object) buttonTag))).check(matches(isClickable()));
        onView(withTagValue(is((Object) buttonTag))).perform(click());
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object) buttonTag))).check(matches(isClickable()));
        onView(withTagValue(is((Object) buttonTag))).perform(click());
    }

    @Test
    public void testFilterButtonBehavesCorrectly() {
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.searchField)).perform(typeText("P"));
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(1000);
        TextView filterText = activityRule.getActivity().findViewById(R.id.friendsFilterText);
        assertThat(filterText.getText().toString(),
                is(activityRule.getActivity().getResources().getString(R.string.friendsFilter)));
    }

    @Test
    public void testLeaderboardIsSearchable() {
        onView(withId(R.id.searchField)).perform(typeText("PICASSO"));
        SystemClock.sleep(1000);
        assertThat(((LinearLayout) activityRule.getActivity().findViewById(R.id.leaderboard))
                .getChildCount(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testFriendsAreSearchable() {
        friendsTest(FriendsRequestState.FRIENDS.ordinal(), 1);
    }

    @Test
    public void testReceivedRequestsDontAppearInFriends() {
        friendsTest(FriendsRequestState.RECEIVED.ordinal(), 0);
    }

    private void friendsTest(int state, int expected) {
        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .setValue(state);
        SystemClock.sleep(2000);
        activityRule.getActivity().initLeaderboard();
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.searchField)).perform(typeText("PICASSO"));
        SystemClock.sleep(2000);
        assertThat(((LinearLayout) activityRule.getActivity()
                .findViewById(R.id.leaderboard)).getChildCount(), is(expected));
    }

    /**
     * Body of a test that tests if an exit button opens the home page.
     */
    public static void testExitButtonBody() {
        Intents.init();
        onView(withId(R.id.exitButton)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }
}
