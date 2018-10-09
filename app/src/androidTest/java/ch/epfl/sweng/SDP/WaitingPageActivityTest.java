package ch.epfl.sweng.SDP;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ProgressBar;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class WaitingPageActivityTest {

    @Rule
    public final ActivityTestRule<WaitingPageActivity> mActivityRule =
            new ActivityTestRule<>(WaitingPageActivity.class);

    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(WaitingPageActivity.class.getName(), null, false);

    @Test
    public void testRadioButton1() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.buttonWord1),
                            View.VISIBLE);
        onView(withId(R.id.buttonWord1)).perform(click());

    }

    @Test
    public void testRadioButton2() {
       waitForVisibility(mActivityRule.getActivity().findViewById(R.id.buttonWord2),
                        View.VISIBLE);
       onView(withId(R.id.buttonWord2)).perform(click());
    }

    @Test
    public void testButtonIncreasePeople() {
        Intents.init();
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.incrementButton), View.VISIBLE);
        for(int i = 0; i < 4; i++) {
            onView(withId(R.id.incrementButton)).perform(click());
        }

        intended(hasComponent(DrawingActivity.class.getName()));
    }

    @Ignore
    public void isButtonWordVisible(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isDisplayed()));
    }

    @Ignore
    public void isButtonWordClickable(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isClickable()));
    }

    @Ignore
    public void isProgressBarVisible(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isDisplayed()));
    }

    @Test
    public void isButtonWord1Visible() {
        isButtonWordVisible(R.id.buttonWord1);
    }

    @Test
    public void isButtonWord2Visible() {
        isButtonWordVisible(R.id.buttonWord2);
    }

    @Test
    public void isButtonWord1Clickable() {
        isButtonWordClickable(R.id.buttonWord1);
    }

    @Test
    public void isButtonWord2Clickable() {
        isButtonWordClickable(R.id.buttonWord2);
    }

    @Test
    public void isProgressBarVisible() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.usersProgressBar), View.VISIBLE);
        onView(withId(R.id.usersProgressBar)).check(matches(isDisplayed()));
    }

    @Test
    public void isUserCounterViewVisible() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.usersTextView),
                        View.VISIBLE);
        onView(withId(R.id.usersTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void incrementingUsersCountShouldChangeProgressBarAndTextView() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.incrementButton),
                            View.VISIBLE);

        Activity currentActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        ProgressBar progressBar = currentActivity.findViewById(R.id.usersProgressBar);

        onView(withId(R.id.incrementButton)).perform(click());
        onView(withId(R.id.usersTextView)).check(matches(withText("2/5 users ready")));
        assertThat(progressBar.getProgress(), is(2));

        onView(withId(R.id.incrementButton)).perform(click());
        onView(withId(R.id.usersTextView)).check(matches(withText("3/5 users ready")));
        assertThat(progressBar.getProgress(), is(3));
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

    public void waitForVisibility(final View view, final int visibility) {
        Espresso.registerIdlingResources(new ViewVisibilityIdlingResource(view, visibility));
    }
}
