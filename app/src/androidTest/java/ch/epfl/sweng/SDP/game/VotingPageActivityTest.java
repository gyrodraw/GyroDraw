package ch.epfl.sweng.SDP.game;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.SDP.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class VotingPageActivityTest {

    @Rule
    public final ActivityTestRule<VotingPageActivity> mActivityRule =
            new ActivityTestRule<>(VotingPageActivity.class);

    @Test
    public void testDrawingIsVisible() {
        onView(ViewMatchers.withId(R.id.drawing)).check(matches(isDisplayed()));
    }

    @Test
    public void testPlayerNameIsVisible() {
        onView(withId(R.id.playerNameView)).check(matches(isDisplayed()));
    }

    @Test
    public void testRatingBarIsVisible() {
        onView(withId(R.id.ratingBar)).check(matches(isDisplayed()));
    }
}
