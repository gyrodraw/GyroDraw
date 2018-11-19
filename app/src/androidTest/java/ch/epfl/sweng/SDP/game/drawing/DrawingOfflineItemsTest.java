package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.nsd.NsdManager;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.game.WaitingPageActivity;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.sweng.SDP.game.drawing.Items.SLOWDOWN;
import static ch.epfl.sweng.SDP.game.drawing.Items.SPEEDUP;
import static ch.epfl.sweng.SDP.game.drawing.Items.SWAPAXIS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DrawingOfflineItemsTest {

    RelativeLayout paintViewHolder;
    PaintView paintView;

    @Rule
    public final ActivityTestRule<DrawingOfflineItems> activityRule =
            new ActivityTestRule<>(DrawingOfflineItems.class);

    @Before
    public void init() {
        paintViewHolder = activityRule.getActivity().paintViewHolder;
        paintView = activityRule.getActivity().paintView;
        paintView.setCircle(0, 0);
    }

    @Test
    public void testItemsGetAdded() {
        SystemClock.sleep(10000);
        assertTrue(1 < paintViewHolder.getChildCount());
    }

    @Test
    public void testTextFeedbackGetsDisplayed() {
        SystemClock.sleep(10000);
        int viewsBefore = paintViewHolder.getChildCount();
        HashMap<Item, ImageView> displayedItems = activityRule.getActivity().getDisplayedItems();
        Item item = (Item)displayedItems.keySet().toArray()[0];
        paintView.setCircle(item.x, item.y);
        assertThat(viewsBefore, is(equalTo(paintViewHolder.getChildCount())));
    }

    @Test
    public void testItemsGetRemovedAfterCollision() {
        SystemClock.sleep(10000);
        Item item = (Item)activityRule.getActivity().getDisplayedItems()
                .keySet().toArray()[0];
        paintView.setCircle(item.x, item.y);
        SystemClock.sleep(1000);
        assertFalse(activityRule.getActivity().getDisplayedItems().containsKey(item));
    }

    @Test
    public void testSpeedupItemSpeedsUpPaintView() throws Throwable {
        double initSpeed = paintView.speed;
        activateItem(SpeedupItem.createSpeedupItem(20, 20, 10, 10));
        assertThat(initSpeed*2, is(equalTo(paintView.speed)));
    }

    @Test
    public void testSlowdownItemSlowsDownPaintView() throws Throwable {
        double initSpeed = paintView.speed;
        activateItem(SlowdownItem.createSlowdownItem(20, 20, 10, 10));
        assertThat(initSpeed/2, is(equalTo(paintView.speed)));
    }

    @Test
    public void testSwapAxisItemSwapsSpeedPaintView() throws Throwable {
        double initSpeed = paintView.speed;
        activateItem(SwapAxisItem.createSwapAxisItem(20, 20, 10, 10));
        assertThat(-initSpeed, is(equalTo(paintView.speed)));
    }

    private void activateItem(final Item item) throws Throwable {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                item.activate(paintView);
            }
        });
    }

}