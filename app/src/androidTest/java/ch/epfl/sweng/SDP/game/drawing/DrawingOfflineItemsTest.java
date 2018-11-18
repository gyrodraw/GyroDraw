package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ch.epfl.sweng.SDP.R;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

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
        assertEquals(paintViewHolder.getChildCount(), 2);
        SystemClock.sleep(1000);
        assertEquals(paintViewHolder.getChildCount(), 1);
    }

}