package ch.epfl.sweng.SDP.home;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.WaitingPageActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.SDP.home.HomeActivity.disableBackgroundAnimation;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    disableBackgroundAnimation();
                }
            };

    @Test
    public void testDrawButtonOpensWaitingPageActivity() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.drawButton)).perform(click());
        intended(hasComponent(WaitingPageActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testClickOnLeagueImageOpensLeaguesActivity() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.leagueImage)).perform(click());
        intended(hasComponent(LeaguesActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testTrophiesButtonIsClickable() {
        onView(withId(R.id.trophiesButton)).check(matches(isClickable()));
    }

    @Test
    public void testStarsButtonIsClickable() {
        onView(withId(R.id.starsButton)).check(matches(isClickable()));
    }

    @Test
    public void testLeagueImageIsVisible() {
        onView(withId(R.id.leagueImage)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testUsernameOpensPopUp() {
        onView(withId(R.id.usernameButton)).perform(click());
        onView(withId(R.id.usernamePopUp)).check(matches(isDisplayed()));
    }

    @Test
    public void testCrossClosesPopUp() {
        openAndClosePopUp(R.id.crossText);
    }

    @Test
    public void testCanSignOutAccount() {
        openAndClosePopUp(R.id.signOutButton);
    }
 
    @Test
    public void testCanDeleteAccount() {
        openAndClosePopUp(R.id.deleteButton);
    }

    private void openAndClosePopUp(int view){
        onView(withId(R.id.usernameButton)).perform(click());
        onView(withId(view)).perform(click());
        onView(withId(R.id.usernamePopUp)).check(doesNotExist());
    }
}