package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DrawingOfflineItemsTest {

    @Rule
    public final ActivityTestRule<DrawingOfflineItems> activityRule =
            new ActivityTestRule<DrawingOfflineItems>(DrawingOfflineItems.class){

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    return intent;
                }
            };

    @Test
    public void testInitialStateOfLayout() {
        RelativeLayout paintViewHolder = activityRule.getActivity().paintViewHolder;
        assertEquals(paintViewHolder.getChildCount(), 1);
    }

    @Test
    public void testItemsGetAdded() {
        RelativeLayout paintViewHolder = activityRule.getActivity().paintViewHolder;
        SystemClock.sleep(10500);
        assertEquals(paintViewHolder.getChildCount(), 2);
    }

    @Test
    public void testTextFeedbackgetsDisplayed() {
        RelativeLayout paintViewHolder = activityRule.getActivity().paintViewHolder;
        PaintView paintView = activityRule.getActivity().paintView;
        SystemClock.sleep(10000);
        int viewsBefore = paintViewHolder.getChildCount();
        HashMap<Item, ImageView> displayedItems = activityRule.getActivity().getDisplayedItems();
        Item item = (Item)displayedItems.keySet().toArray()[0];
        paintView.setCircle(item.x, item.y);
        assertEquals(paintViewHolder.getChildCount(), viewsBefore);
    }

    @Test
    public void testItemsGetRemovedAfterCollision() {
        RelativeLayout paintViewHolder = activityRule.getActivity().paintViewHolder;
        PaintView paintView = activityRule.getActivity().paintView;
        SystemClock.sleep(10000);
        int viewsBefore = paintViewHolder.getChildCount();
        HashMap<Item, ImageView> displayedItems = activityRule.getActivity().getDisplayedItems();
        Item item = (Item)displayedItems.keySet().toArray()[0];
        paintView.setCircle(item.x, item.y);
        SystemClock.sleep(1000);
        assertEquals(paintViewHolder.getChildCount(), viewsBefore-1);
    }

}