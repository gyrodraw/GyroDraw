package ch.epfl.sweng.SDP.game.drawing;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;

import android.graphics.Point;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;
import ch.epfl.sweng.SDP.game.drawing.PaintView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingActivityTest {
    @Rule
    public final ActivityTestRule<DrawingActivity> mActivityRule =
            new ActivityTestRule<>(DrawingActivity.class);

    private PaintView paintView;

    @Before
    public void init(){
        paintView = mActivityRule.getActivity().findViewById(R.id.paintView);
    }

    @Test
    public void testCanvas() {
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testDrawToggleReactsCorrectlyToClicking(){
        onView(withId(R.id.flyOrDraw)).check(matches(isNotChecked()));
        onView(withId(R.id.flyOrDraw)).perform(click());
        onView(withId(R.id.flyOrDraw)).check(matches(isChecked()));
        onView(withId(R.id.flyOrDraw)).perform(click());
        onView(withId(R.id.flyOrDraw)).check(matches(isNotChecked()));
    }

    @Test
    public void testClearButtonIsClickable() {
        onView(withId(R.id.clearCanvas)).perform(click());
    }

    @Test
    public void testPaintViewFullyDisplayed() {
        onView(withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testPaintViewGettersSetters(){
        paintView.setCircleX(10);
        paintView.setCircleY(15);
        paintView.setCircleRadius(12);
        paintView.setDraw(true);
        assertEquals(paintView.getCircleX()==10,true);
        assertEquals(paintView.getCircleY()==15, true);
        assertEquals(paintView.getCircleRadius()==12, true);
        assertEquals(paintView.getDraw(), true);
    }

    @Test
    public void testInitWorks(){
        Point point = new Point(100, 100);
        paintView.setSizeAndInit(point);
        paintView.setCircleRadius(10);
        assertEquals(paintView.getCircleX()==40.0, true);
    }
}