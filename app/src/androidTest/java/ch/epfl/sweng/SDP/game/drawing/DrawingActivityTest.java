package ch.epfl.sweng.SDP.game.drawing;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.SDP.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DrawingActivityTest {
    @Rule
    public final ActivityTestRule<DrawingActivity> activityRule =
            new ActivityTestRule<>(DrawingActivity.class);

    private PaintView paintView;

    @Before
    public void init() {
        paintView = activityRule.getActivity().findViewById(R.id.paintView);
    }

    @Test
    public void testCanvas() {
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testPaintViewFullyDisplayed() {
        onView(withId(R.id.paintView)).perform(click());
    }

    @Test
    public void testPaintViewGettersSetters() {
        paintView.setCircleX(10);
        paintView.setCircleY(15);
        paintView.setCircleRadius(12);
        paintView.setDraw(true);
        assertTrue(paintView.getCircleX() == 10);
        assertTrue(paintView.getCircleY() == 15);
        assertTrue(paintView.getCircleRadius() == 12);
        assertTrue(paintView.getDraw());
    }

    @Test
    public void testInitWorks() {
        paintView.setSizeAndInit(100, 100);
        paintView.setCircleRadius(30);
        assertEquals(50.0, paintView.getCircleX());
    }
}