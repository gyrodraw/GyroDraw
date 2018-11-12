package ch.epfl.sweng.SDP.game;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;

import ch.epfl.sweng.SDP.R;

import com.google.firebase.FirebaseApp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    @Rule
    public final ActivityTestRule<LeaderboardActivity> mActivityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    @Test
    public void testSearchFieldVisible() {
        onView(withId(R.id.searchField)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testScrollViewVisible() {
        onView(withId(R.id.scrollLeaderboard)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testModifySearchField() {
        onView(withId(R.id.searchField))
                .perform(typeText("M"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.scrollLeaderboard)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testFriendsButtonsClickable() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        onView(withId(R.id.searchField))
                .perform(typeText(""), ViewActions.closeSoftKeyboard());

        SystemClock.sleep(3000);
        onView(withTagValue(is((Object)"friendsButton0"))).perform(click());
        onView(withId(R.id.searchField))
                .perform(typeText("M"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.searchField))
                .perform(typeText(""), ViewActions.closeSoftKeyboard());
        SystemClock.sleep(3000);
        onView(withTagValue(is((Object)"friendsButton0"))).perform(click());
    }
}
