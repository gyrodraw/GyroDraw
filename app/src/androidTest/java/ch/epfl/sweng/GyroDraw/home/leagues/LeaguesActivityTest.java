package ch.epfl.sweng.GyroDraw.home.leagues;

import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.GyroDraw.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LeaguesActivityTest {
    @Rule
    public final ActivityTestRule<LeaguesActivity> mActivityRule =
            new ActivityTestRule<LeaguesActivity>(LeaguesActivity.class);

    @Test
    public void testLeaguesListIsDisplayed() {
        onView(withId(R.id.scrollView)).check(matches(isDisplayed()));
    }

    @Test
    public void testLeaguesListIsScrollableDown() {
        onView(withId(R.id.scrollView)).perform(ViewActions.swipeUp());
        onView(withId(R.id.league1Image)).check(matches(isDisplayed()));
    }

    @Test
    public void testLeaguesListIsScrollableUp() {
        onView(withId(R.id.scrollView)).perform(ViewActions.swipeDown());
        onView(withId(R.id.scrollView)).perform(ViewActions.swipeDown());
        onView(withId(R.id.league3Image)).check(matches(isDisplayed()));
    }

}
