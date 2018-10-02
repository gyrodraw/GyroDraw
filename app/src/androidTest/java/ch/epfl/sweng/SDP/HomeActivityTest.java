package ch.epfl.sweng.SDP;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {
    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void testDrawButtonIsClickable() {
        onView(withId(R.id.drawButton)).perform(click());
        onView(withId(R.id.drawButton)).check(matches(isClickable()));
    }

    @Test
    public void testTrophiesButtonIsClickable() {
        onView(withId(R.id.trophiesButton)).perform(click());
        onView(withId(R.id.trophiesButton)).check(matches(isClickable()));
    }

    @Test
    public void testStarsButtonIsClickable() {
        onView(withId(R.id.starsButton)).perform(click());
        onView(withId(R.id.starsButton)).check(matches(isClickable()));
    }

    @Test
    public void testLeagueImageIsVisible() {
        onView(withId(R.id.leagueImage)).check(matches(isCompletelyDisplayed()));
    }
}