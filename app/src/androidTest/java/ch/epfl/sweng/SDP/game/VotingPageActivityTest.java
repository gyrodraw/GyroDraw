package ch.epfl.sweng.SDP.game;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RatingBar;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.SDP.game.VotingPageActivity.disableAnimations;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class VotingPageActivityTest {

    @Rule
    public final ActivityTestRule<VotingPageActivity> mActivityRule =
            new ActivityTestRule<VotingPageActivity>(VotingPageActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    disableAnimations();
                }

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("RoomID", "0123457890");

                    return intent;
                }
            };

    // Add a monitor for the home activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    @Test
    public void ratingUsingRatingBarShouldBeSaved() {
        ((RatingBar) mActivityRule.getActivity().findViewById(R.id.ratingBar)).setRating(3);
        SystemClock.sleep(1000);
        assertThat(mActivityRule.getActivity().getRatings()[0], is(3));
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

    @Test
    public void addStarsHandlesBigNumber() {
        StarAnimationView starsAnimation = mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
        starsAnimation.onSizeChanged(100, 100, 100, 100);
        Canvas canvas = new Canvas();
        starsAnimation.onDraw(canvas);
        starsAnimation.addStars(1000);
        starsAnimation.updateState(1000);
        starsAnimation.onDraw(canvas);
        int stars = starsAnimation.getNumStars();
        assert (5 == stars);
    }

    @Test
    public void addStarsHandlesNegativeNumber() {
        StarAnimationView starsAnimation = mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
        starsAnimation.onSizeChanged(100, 100, 100, 100);
        Canvas canvas = new Canvas();
        starsAnimation.onDraw(canvas);
        starsAnimation.addStars(-10);
        starsAnimation.updateState(1000);
        starsAnimation.onDraw(canvas);
        assert (0 == starsAnimation.getNumStars());
    }

    @Test
    public void startHomeActivityStartsHomeActivity(){
        mActivityRule.getActivity().startHomeActivity(null);
        SystemClock.sleep(2000);
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(homeActivity);
        assertTrue(mActivityRule.getActivity().isFinishing());
    }
}
