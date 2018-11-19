package ch.epfl.sweng.SDP.game.drawing;

import android.os.SystemClock;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import android.support.test.rule.ActivityTestRule;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.game.drawing.items.AddStarsItem;
import ch.epfl.sweng.SDP.game.drawing.items.BumpingItem;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.game.drawing.items.SlowdownItem;
import ch.epfl.sweng.SDP.game.drawing.items.SpeedupItem;
import ch.epfl.sweng.SDP.game.drawing.items.SwapAxisItem;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DrawingOfflineItemsTest {

    RelativeLayout paintViewHolder;
    PaintView paintView;

    @Rule
    public final ActivityTestRule<DrawingOfflineItems> activityRule =
            new ActivityTestRule<>(DrawingOfflineItems.class);

    /**
     * Initializes variables.
     */
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
        paintView.setCircle(item.getX(), item.getY());
        assertThat(paintViewHolder.getChildCount(), is(equalTo(viewsBefore)));
    }

    @Test
    public void testItemsGetRemovedAfterCollision() {
        SystemClock.sleep(10000);
        Item item = (Item)activityRule.getActivity().getDisplayedItems()
                .keySet().toArray()[0];
        paintView.setCircle(item.getX(), item.getY());
        SystemClock.sleep(1000);
        assertFalse(activityRule.getActivity().getDisplayedItems().containsKey(item));
    }

    @Test
    public void testSpeedupItemSpeedsUpPaintView() throws Throwable {
        double init = paintView.getSpeed();
        activateItem(SpeedupItem.createSpeedupItem(20, 20, 10));
        assertThat(paintView.getSpeed(), is(equalTo(init*2)));
    }

    @Test
    public void testSlowdownItemSlowsDownPaintView() throws Throwable {
        double initSpeed = paintView.getSpeed();
        activateItem(SlowdownItem.createSlowdownItem(21, 21, 11));
        assertThat(paintView.getSpeed(), is(equalTo(initSpeed/2)));
    }

    @Test
    public void testSwapAxisItemSwapsSpeedPaintView() throws Throwable {
        double initSpeed = paintView.getSpeed();
        activateItem(SwapAxisItem.createSwapAxisItem(22, 22, 12));
        assertThat(paintView.getSpeed(), is(equalTo(-initSpeed)));
    }

    @Test
    public void testAddStarsItemAddsStarsToAccount() throws Throwable {
        int initStars = Account.getInstance(activityRule.getActivity()
                .getApplicationContext()).getStars();
        activateItem(AddStarsItem.createAddStarsItem(20, 20, 10));
        assertThat(Account.getInstance(activityRule.getActivity()
                .getApplicationContext()).getStars(), is(equalTo(initStars+10)));
    }

    @Test
    public void testBumpingItemReplacesPaintViewCoordinatesCorrectly() throws Throwable {
        paintView.setCircle(201, 201);
        activateItem(BumpingItem.createBumpingItem(200, 200, 10));
        double angle = Math.atan2(1, 1);
        int newX = 200 + (int) (Math.cos(angle) * (10 + paintView.getCircleRadius() + 5));
        int newY = 200 + (int) (Math.sin(angle) * (10 + paintView.getCircleRadius() + 5));
        assertThat(paintView.getCircleX(), is(equalTo(newX)));
        assertThat(paintView.getCircleY(), is(equalTo(newY)));
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