package ch.epfl.sweng.SDP.game;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.google.firebase.database.DataSnapshot;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.SDP.game.LoadingScreenActivity.disableLoadingAnimations;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LoadingScreenActivityTest {

    @Rule
    public final ActivityTestRule<LoadingScreenActivity> mActivityRule =
            new ActivityTestRule<LoadingScreenActivity>(LoadingScreenActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    disableLoadingAnimations();
                }

            };

    @Test
    public void testWordsReady() {
        ArrayList<String> words = new ArrayList<>();
        mActivityRule.getActivity().areWordsReady(words);
    }

    /*@Test
    public void testActivityLoadingStartsWaitingPage() {
        Intents.init();
        Espresso.pressBack();
        //intended(hasComponent(HomeActivity.class.getName()));
        //onView(withId(R.id.drawButton)).perform(click());
        //intended(hasComponent(LoadingScreenActivity.class.getName()));
        //onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(10)));
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }*/

    /**
     * Perform action of waiting for a specific time.
     */
    /*@Ignore
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
    }*/
}
