package ch.epfl.sweng.SDP.home;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import static android.support.test.espresso.Espresso.onView;

import android.support.test.espresso.action.ViewActions;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.support.test.espresso.intent.Intents;

import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.SDP.R;

import com.google.firebase.FirebaseApp;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    @Rule
    public final ActivityTestRule<LeaderboardActivity> activityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    @Test
    public void testSearchFieldClickable() {
        onView(withId(R.id.searchField))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.searchField)).check(matches(isClickable()));
    }

    @Test
    public void testClickOnExitButtonOpensHomeActivity() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.exitButton)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
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
