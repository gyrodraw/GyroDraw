package ch.epfl.sweng.SDP;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingActivityTest {
    @Rule
    public final ActivityTestRule<DrawingActivity> mActivityRule =
            new ActivityTestRule<>(DrawingActivity.class);

    @Test
    public void testCanvas() {
        onView(withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testDrawToggleIsClickable() {
        onView(withId(R.id.flyOrDraw)).check(matches(isClickable()));
    }

    @Test
    public void testDrawToggleIsCheckedAfterClicking() {
        onView(withId(R.id.flyOrDraw)).perform(click());
        onView(withId(R.id.flyOrDraw)).check(matches(isChecked()));
    }

    @Test
    public void testClearButtonIsClickable() {
        onView(withId(R.id.clearCanvas)).check(matches(isClickable()));
        onView(withId(R.id.clearCanvas)).perform(click());
    }

    @Test
    public void testPaintViewFullyDisplayed() {
        onView(withId(R.id.paintView)).check(matches(isCompletelyDisplayed()));
    }
}