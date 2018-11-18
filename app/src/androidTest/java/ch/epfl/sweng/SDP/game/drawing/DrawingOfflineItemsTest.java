package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.os.SystemClock;
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
            new ActivityTestRule<DrawingOfflineItems>(DrawingOfflineItems.class){};

    @Test
    public void testItemsGetAdded() {
        RelativeLayout paintViewHolder = activityRule.getActivity().paintViewHolder;
        assertEquals(paintViewHolder.getChildCount(), 1);
        SystemClock.sleep(11000);
        assertEquals(paintViewHolder.getChildCount(), 2);
    }

    @Test
    public void testItemsGetCollected() {
        RelativeLayout paintViewHolder = activityRule.getActivity().paintViewHolder;
        PaintView paintView = activityRule.getActivity().paintView;
        SystemClock.sleep(11000);
        HashMap<Item, ImageView> displayedItems = activityRule.getActivity().getDisplayedItems();
        Item item = (Item)displayedItems.keySet().toArray()[0];
        paintView.setCircle(item.x, item.y);
        SystemClock.sleep(400);
        assertEquals("Assert 1, should be 2, is: "+paintViewHolder.getChildCount(),
                paintViewHolder.getChildCount(), 2);
        SystemClock.sleep(800);
        assertEquals("Assert 2, should be 1, is: "+paintViewHolder.getChildCount(),
                paintViewHolder.getChildCount(), 1);
    }

}