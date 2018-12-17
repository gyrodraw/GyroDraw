package ch.epfl.sweng.SDP.game.drawing;

import android.graphics.Color;
import android.os.SystemClock;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.SeekBar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.shop.ColorsShop;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static ch.epfl.sweng.SDP.game.drawing.DrawingActivity.CURR_WIDTH;
import static ch.epfl.sweng.SDP.game.drawing.DrawingActivity.MIN_WIDTH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DrawingOfflineActivityNoItemsTest {

    private PaintView paintView;

    @Rule
    public final ActivityTestRule<DrawingOfflineActivity> activityRule =
            new ActivityTestRule<>(DrawingOfflineActivity.class);

    /**
     * Initialise mock elements and get UI elements.
     */
    @Before
    public void init() {
        paintView = activityRule.getActivity().findViewById(R.id.paintView);
        Account.getInstance(activityRule.getActivity().getApplicationContext())
                .updateItemsBought(new ShopItem(ColorsShop.BLUE, 200));
        Account.getInstance(activityRule.getActivity().getApplicationContext())
                .updateItemsBought(new ShopItem(ColorsShop.RED, 100));
    }

    @Test
    public void testCorrectLayout() {
        int layoutId = activityRule.getActivity().getLayoutId();
        assertThat(layoutId, is(R.layout.activity_drawing_offline));
    }

    @Test
    public void testExitClick() {
        onView(ViewMatchers.withId(R.id.exit)).perform(click());
        assertThat(activityRule.getActivity().isFinishing(), is(true));
    }

    @Test
    public void testBlackButton() {
        onView(ViewMatchers.withId(R.id.blackButton)).perform(click());
        assertThat(paintView.getColor(), is(Color.BLACK));
    }

    @Test
    public void testPencilTool() {
        onView(ViewMatchers.withId(R.id.eraserButton)).perform(click());
        onView(ViewMatchers.withId(R.id.pencilButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertThat(paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()),
                is(Color.WHITE));
    }

    @Test
    public void testEraserTool() {
        onView(ViewMatchers.withId(R.id.eraserButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertThat(paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()),
                is(Color.WHITE));
    }

    @Test
    public void testBucketTool() {
        activityRule.getActivity().clear(null);
        onView(ViewMatchers.withId(R.id.bucketButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertThat(paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()),
                is(paintView.getColor()));
    }

    @Test
    public void testChangeBrushWidth() {
        final int initWidth = paintView.getDrawWidth();
        SeekBar brushWidthBar = activityRule.getActivity().findViewById(R.id.brushWidthBar);

        brushWidthBar.setProgress(0);
        SystemClock.sleep(2000);
        assertThat(paintView.getDrawWidth(), is(MIN_WIDTH + 1));

        brushWidthBar.setProgress(100);
        SystemClock.sleep(2000);
        assertThat(paintView.getDrawWidth(), is((int) Math.pow(CURR_WIDTH, 2) + MIN_WIDTH));

        brushWidthBar.setProgress(50);
        SystemClock.sleep(2000);
        assertThat(paintView.getDrawWidth(), is(initWidth));
    }
}