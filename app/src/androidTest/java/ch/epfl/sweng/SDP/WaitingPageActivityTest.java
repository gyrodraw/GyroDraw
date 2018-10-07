package ch.epfl.sweng.SDP;

import static android.support.test.espresso.Espresso.onIdle;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(AndroidJUnit4.class)
public class WaitingPageActivityTest {

    @Rule
    public final ActivityTestRule<WaitingPageActivity> mActivityRule =
            new ActivityTestRule<>(WaitingPageActivity.class);

    @Test
    public void testButton1ChooseWords() {
        Intents.init();
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.buttonWord1)).perform(click());
        intended(hasComponent(DrawingActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testButton2ChooseWords() {
        Intents.init();
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(10)));
        onView(withId(R.id.buttonWord2)).perform(click());
        intended(hasComponent(DrawingActivity.class.getName()));
        Intents.release();
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
}
