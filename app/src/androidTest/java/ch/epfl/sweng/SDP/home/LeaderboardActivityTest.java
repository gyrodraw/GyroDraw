package ch.epfl.sweng.SDP.home;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import static android.support.test.espresso.Espresso.onView;

import android.support.test.espresso.action.ViewActions;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;

import android.support.test.espresso.intent.Intents;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

import com.google.firebase.FirebaseApp;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    private static final String USER_ID = "123456789";
    private static final String USERNAME = "username";

    @Rule
    public final ActivityTestRule<LeaderboardActivity> activityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    private Account account;

    @Before
    public void init() {
        account = Account.getInstance(activityRule.getActivity());
        account.setUserId(USER_ID);
        account.setUsername(USERNAME);
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
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object)"friendsButton0"))).perform(click());
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object)"friendsButton0"))).perform(click());
    }

    @Test
    public void testFilterButtonChangesTextOnClick() {
        onView(withId(R.id.friendsFilter)).perform(click());
        TextView filter = activityRule.getActivity().findViewById(R.id.friendsFilter);
        assertThat(filter.getText().toString(), is(equalTo(activityRule.getActivity()
                .getResources().getString(R.string.removeFriendsFilter))));
        onView(withId(R.id.friendsFilter)).perform(click());
        assertThat(filter.getText().toString(), is(equalTo(activityRule.getActivity()
                .getResources().getString(R.string.friendsFilter))));
    }

    @Test
    public void testFilterButtonFiltersForFriends() {
        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .setValue(FriendsRequestState.FRIENDS.ordinal());
        LinearLayout linearLayout = activityRule.getActivity().findViewById(R.id.leaderboard);
        SystemClock.sleep(2000);
        int before = linearLayout.getChildCount();
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(2000);
        assertTrue(linearLayout.getChildCount() < before);
    }

    /**
     * Body of a test that tests if an exit button opens the home page.
     */
    public static void testExitButtonBody() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.exitButton)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }
}
