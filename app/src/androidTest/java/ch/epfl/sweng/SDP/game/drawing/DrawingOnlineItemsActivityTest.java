package ch.epfl.sweng.SDP.game.drawing;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.game.drawing.items.AddStarsItem;
import ch.epfl.sweng.SDP.game.drawing.items.BumpingItem;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.shop.ColorsShop;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class DrawingOnlineItemsActivityTest {

    private static final String USER_ID = "123456789";
    private static final String USERNAME = "testUser";
    private static final String EMAIL = "testUser@gyrodraw.ch";

    private RelativeLayout paintViewHolder;
    private PaintView paintView;
    private DrawingOnlineItemsActivity activity;
    private Account account;

    @Rule
    public final ActivityTestRule<DrawingOnlineItemsActivity> activityRule =
            new ActivityTestRule<>(DrawingOnlineItemsActivity.class);

    /**
     * Initializes variables.
     */
    @Before
    public void init() {
        activity = activityRule.getActivity();
        paintViewHolder = activity.getDrawingItems().getPaintViewHolder();
        paintView = activity.getDrawingItems().getPaintView();
        paintView.setCircle(0, 0);
        account = Account.getInstance(activityRule.getActivity());
        account.setUserId(USER_ID);
        account.setUsername(USERNAME);
        account.setEmail(EMAIL);
        account.updateItemsBought(new ShopItem(ColorsShop.BLUE, 200));
        account.updateItemsBought(new ShopItem(ColorsShop.RED, 100));
    }

    @Test
    public void testItemsGetAdded() {
        addRandomItem();
        SystemClock.sleep(5000);
        assertThat(paintViewHolder.getChildCount(), greaterThan(1));
    }

    @Test
    public void testTextFeedbackGetsDisplayed() {
        int viewsBefore = paintViewHolder.getChildCount();
        Item item;
        do {
            addRandomItem();
            SystemClock.sleep(5000);
            Map<Item, ImageView> displayedItems = activity.getDrawingItems().getDisplayedItems();
            item = (Item) displayedItems.keySet().toArray()[0];
        }
        while (item instanceof BumpingItem);
        paintView.setCircle(item.getX(), item.getY());
        assertThat(paintViewHolder.getChildCount(), greaterThanOrEqualTo(viewsBefore));
    }

    @Test
    public void testAddStarsItemAddsStarsToAccount() {
        int initStars = account.getStars();
        activateItem(AddStarsItem.createAddStarsItem(20, 20, 10));
        assertThat(account.getStars(), is(initStars + 3));
    }

    private void activateItem(final Item item) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    item.activate(paintView);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void addRandomItem() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getDrawingItems().addRandomItem();
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}