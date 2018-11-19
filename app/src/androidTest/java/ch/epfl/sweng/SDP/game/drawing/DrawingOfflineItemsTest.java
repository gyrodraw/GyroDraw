package ch.epfl.sweng.SDP.game.drawing;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import ch.epfl.sweng.SDP.auth.Account;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
        activateItem(SpeedupItem.createSpeedupItem(20, 20, 10));
        assertThat(initSpeed*2, is(equalTo(paintView.speed)));
    }

    @Test
    public void testSlowdownItemSlowsDownPaintView() throws Throwable {
        double initSpeed = paintView.speed;
        activateItem(SlowdownItem.createSlowdownItem(20, 20, 10));
        assertThat(initSpeed/2, is(equalTo(paintView.speed)));
    }

    @Test
    public void testSwapAxisItemSwapsSpeedPaintView() throws Throwable {
        double initSpeed = paintView.speed;
        activateItem(SwapAxisItem.createSwapAxisItem(20, 20, 10));
        assertThat(-initSpeed, is(equalTo(paintView.speed)));
    }

    @Test
    public void testAddStarsItemAddsStarsToAccount() throws Throwable {
        int initStars = Account.getInstance(activityRule.getActivity()
                .getApplicationContext()).getStars();
        activateItem(AddStarsItem.createAddStarsItem(20, 20, 10));
        assertThat(initStars+10, is(equalTo(Account.getInstance(activityRule.getActivity()
                .getApplicationContext()).getStars())));
    }

    @Test
    public void testBumpingItemReplacesPaintViewCoordinatesCorrectly() throws Throwable {
        double angle = Math.atan2(1, 1);
        int newX = 200 + (int) (Math.cos(angle) * (10 + paintView.getCircleRadius() + 5));
        int newY = 200 + (int) (Math.sin(angle) * (10 + paintView.getCircleRadius() + 5));
        paintView.setCircle(201, 201);
        activateItem(BumpingItem.createBumpingItem(200, 200, 10));
        assertThat(newX, is(equalTo(paintView.getCircleX())));
        assertThat(newY, is(equalTo(paintView.getCircleY())));
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