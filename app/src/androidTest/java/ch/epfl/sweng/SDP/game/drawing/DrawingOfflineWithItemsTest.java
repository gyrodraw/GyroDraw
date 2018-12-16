package ch.epfl.sweng.SDP.game.drawing;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.game.drawing.items.BumpingItem;
import ch.epfl.sweng.SDP.game.drawing.items.Item;
import ch.epfl.sweng.SDP.game.drawing.items.RandomItemGenerator;
import ch.epfl.sweng.SDP.game.drawing.items.SlowdownItem;
import ch.epfl.sweng.SDP.game.drawing.items.SpeedupItem;
import ch.epfl.sweng.SDP.game.drawing.items.SwapAxisItem;
import ch.epfl.sweng.SDP.shop.ColorsShop;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class DrawingOfflineWithItemsTest {

    private static final String USER_ID = "123456789";
    private static final String USERNAME = "testUser";
    private static final String EMAIL = "testUser@gyrodraw.ch";

    private RelativeLayout paintViewHolder;
    private PaintView paintView;
    private DrawingOffline activity;
    private Account account;

    @Rule
    public final ActivityTestRule<DrawingOffline> activityRule =
            new ActivityTestRule<>(DrawingOffline.class);

    /**
     * Initializes variables.
     */
    @Before
    public void init() {
        activity = activityRule.getActivity();
        toggleMysteryMode();
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
    public void testItemsGetRemoved() {
        toggleMysteryMode();
        assertThat(paintViewHolder.getChildCount(), lessThanOrEqualTo(1));
        toggleMysteryMode();
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
    public void testSpeedupItemSpeedsUpPaintView() {
        checkItemHasCorrectBehaviourOnPaintView(
                new SpeedupItem(20, 20, 10), 2);
    }

    @Test
    public void testSlowdownItemSlowsDownPaintView() {
        checkItemHasCorrectBehaviourOnPaintView(
                new SlowdownItem(20, 20, 10), .5);
    }

    @Test
    public void testSwapAxisItemSwapsSpeedPaintView() {
        checkItemHasCorrectBehaviourOnPaintView(
                new SwapAxisItem(20, 20, 10), -1);
    }

    @Test
    public void testBumpingItemReplacesPaintViewCoordinatesCorrectly() {
        paintView.setCircle(202, 202);
        collisionItem(new BumpingItem(200, 200, RandomItemGenerator.ITEM_RADIUS));
        int dx = paintView.getCircleX() - 200;
        int dy = paintView.getCircleY() - 200;
        double radius = Math.sqrt(dx * dx + dy * dy) + paintView.getCircleRadius();
        assertThat((int) radius, is(greaterThanOrEqualTo(RandomItemGenerator.ITEM_RADIUS)));
    }

    @Test
    public void testBumpingItemChangesItsDrawable() {
        paintView.setCircle(200, 200);
        BumpingItem item = new BumpingItem(200, 200, RandomItemGenerator.ITEM_RADIUS);
        ImageView view = new ImageView(activity);
        view.setX(item.getX() - item.getRadius());
        view.setY(item.getY() - item.getRadius());
        view.setLayoutParams(new RelativeLayout.LayoutParams(
                2 * item.getRadius(),
                2 * item.getRadius()));
        view.setImageResource(R.drawable.mystery_box);
        item.setImageView(view);
        collisionItem(item);
        assertThat(item.getImageView().getDrawable(),
                is(not(activity.getDrawable(R.drawable.mystery_box))));
    }

    private void checkItemHasCorrectBehaviourOnPaintView(Item item, double factor) {
        double init = paintView.getSpeed();
        activateItem(item);
        assertThat(paintView.getSpeed(), is(init * factor));
    }

    private void toggleMysteryMode() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.toggleMysteryMode(null);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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

    private void collisionItem(final Item item) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    item.collision(paintView);
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
                    activity.getDrawingItems().addRandomItemForOfflineMode();
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}