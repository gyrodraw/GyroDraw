package ch.epfl.sweng.SDP.game.withoutItems;

import android.graphics.Color;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.game.drawing.PaintView;
import ch.epfl.sweng.SDP.game.drawing.withoutItems.DrawingOffline;
import ch.epfl.sweng.SDP.shop.ColorsShop;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class DrawingOfflineTest {

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
        assertEquals(layoutId, R.layout.activity_drawing_offline);
    }

    @Test
    public void testExitClick() {
        onView(ViewMatchers.withId(R.id.exit)).perform(click());
        assertTrue(activityRule.getActivity().isFinishing());
    }

    @Test
    public void testBlackButton() {
        onView(ViewMatchers.withId(R.id.blackButton)).perform(click());
        assertEquals(Color.BLACK, paintView.getColor());
    }

    @Test
    public void testPencilTool() {
        onView(ViewMatchers.withId(R.id.eraserButton)).perform(click());
        onView(ViewMatchers.withId(R.id.pencilButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertEquals(Color.WHITE,
                paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()));
    }

    @Test
    public void testEraserTool() {
        onView(ViewMatchers.withId(R.id.eraserButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertEquals(Color.WHITE,
                paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()));
    }

    @Test
    public void testBucketTool() {
        activityRule.getActivity().clear(null);
        onView(ViewMatchers.withId(R.id.bucketButton)).perform(click());
        onView(ViewMatchers.withId(R.id.paintView)).perform(click());
        assertEquals(paintView.getColor(),
                paintView.getBitmap().getPixel(paintView.getCircleX(), paintView.getCircleY()));
    }
}