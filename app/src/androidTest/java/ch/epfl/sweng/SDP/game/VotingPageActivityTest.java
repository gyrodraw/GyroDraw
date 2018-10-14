package ch.epfl.sweng.SDP.game;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.RatingBar;
import ch.epfl.sweng.SDP.R;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matcher;
import org.junit.Ignore;
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
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(1)));
        isViewVisible(R.id.drawing);
    }

    @Test
    public void testPlayerNameIsVisible() {
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(1)));
        isViewVisible(R.id.playerNameView);
    }

    @Test
    public void testRatingBarIsVisible() {
        isViewVisible(R.id.ratingBar);
    }

    @Test
    public void ratingUsingRatingBarShouldBeSaved() {
        ((RatingBar) mActivityRule.getActivity().findViewById(R.id.ratingBar)).setRating(3);
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(1)));
        assertThat(mActivityRule.getActivity().getRatings()[0], is(3));
    }

    /**
     * Check if the view is displayed.
     *
     * @param id Id of the view
     */
    @Ignore
    public void isViewVisible(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isDisplayed()));
    }

    /**
     * Check if the views are clickable.
     *
     * @param id Id of the view
     */
    @Ignore
    public void isViewClickable(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isClickable()));
    }


    /**
     * Perform action of waiting for a specific time.
     */
    @Ignore
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    @Ignore
    public void waitForVisibility(final View view, final int visibility) {
        Espresso.registerIdlingResources(new ViewVisibilityIdlingResource(view, visibility));
    }
}
