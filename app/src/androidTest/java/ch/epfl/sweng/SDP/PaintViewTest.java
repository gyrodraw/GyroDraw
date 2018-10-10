package ch.epfl.sweng.SDP;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PaintViewTest {
    @Rule
    public final PaintViewRule<PaintView> mActivityRule =
            new PaintViewRule<>(PaintView.class);

    @Test
    public void testCanvas() {
        onView(withId(R.id.paintView)).perform(click());
    }

    @Test
    public void tesDrawToggleIsClickable() {
        onView(withId(R.id.fly_or_draw)).perform(click());
        onView(withId(R.id.fly_or_draw)).check(matches(isClickable()));
    }

    @Test
    public void testClearButtonIsClickable() {
        onView(withId(R.id.clear_canvas)).perform(click());
        onView(withId(R.id.clear_canvas)).check(matches(isClickable()));
    }

    @Test
    public void testPaintViewFullyDisplayed() {
        onView(withId(R.id.paintView)).check(matches(isCompletelyDisplayed()));
    }


}