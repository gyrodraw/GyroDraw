package ch.epfl.sweng.SDP.game;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
    public void testRatingBarIsVisible() {
        onView(withId(R.id.ratingBar)).check(matches(isDisplayed()));
    }

    @Test
    public void ratingUsingRatingBarShouldBeSaved() {
        ((RatingBar) mActivityRule.getActivity().findViewById(R.id.ratingBar)).setRating(3);
        SystemClock.sleep(1000);
        assertThat(mActivityRule.getActivity().getRatings()[0], is(3));
    }

    @Test
    public void progressBarTest() {
        Drawable notAnimatedDrawable = ContextCompat.getDrawable(mActivityRule.getActivity(),
                                        R.drawable.draw_button);
        ((ProgressBar) mActivityRule.getActivity().findViewById(R.id.progressBar))
                .setIndeterminateDrawable(notAnimatedDrawable);

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
    }

    @Test
    public void changeImageButtonIsClickable() {
        onView(withId(R.id.buttonChangeImage)).check(matches(isClickable()));
        onView(withId(R.id.rankingButton)).perform(click());
    }

    @Test
    public void changeImageButtonIsDisplayed() {
        onView(withId(R.id.buttonChangeImage)).check(matches(isDisplayed()));
    }

    @Test
    public void rankingButtonIsClickable() {
        onView(withId(R.id.rankingButton)).check(matches(isClickable()));
        onView(withId(R.id.rankingButton)).perform(click());
    }

    @Test
    public void rankingButtonIsDisplayed() {
        onView(withId(R.id.rankingButton)).check(matches(isDisplayed()));
    }
}
