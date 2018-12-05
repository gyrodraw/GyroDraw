package ch.epfl.sweng.SDP.game.drawing;

import android.graphics.Color;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.view.KeyEvent;
import android.widget.SeekBar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.shop.ColorsShop;
import ch.epfl.sweng.SDP.shop.ShopItem;

public class DrawingOfflineNoItemsTest {

    private PaintView paintView;

    @Rule
    public final ActivityTestRule<DrawingOffline> activityRule =
            new ActivityTestRule<>(DrawingOffline.class);

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
        int initWidth = paintView.getDrawWidth();
        SeekBar brushWidthBar = activityRule.getActivity().findViewById(R.id.brushWidthBar);
        activityRule.getActivity().onKeyDown(
                KeyEvent.KEYCODE_VOLUME_DOWN, new KeyEvent(-1, 1));
        assertThat(paintView.getDrawWidth(), is(initWidth-10));
        activityRule.getActivity().onKeyDown(
                KeyEvent.KEYCODE_VOLUME_UP, new KeyEvent(-1, 1));
        assertThat(paintView.getDrawWidth(), is(initWidth));
    }

    @Test
    public void testChangeBrushWidthBelowZero() {
        paintView.setDrawWidth(10);
        activityRule.getActivity().onKeyDown(
                KeyEvent.KEYCODE_VOLUME_DOWN, new KeyEvent(-1, 1));
        assertThat(paintView.getDrawWidth(), is(10));
    }
}