package ch.epfl.sweng.SDP.shop;

import android.os.SystemClock;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.BOUGHT_ITEMS;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {

    private static final String USER_ID = "no_user";

    @Rule
    public final ActivityTestRule<ShopActivity> mActivityRule =
            new ActivityTestRule<ShopActivity>(ShopActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    ShopActivity.disableAnimations();
                    FbDatabase.removeAccountAttribute(USER_ID, BOUGHT_ITEMS);
                }
            };

    @After
    public void afterEachTest() {
        Account.deleteAccount();
    }

    @Test
    public void testPressBuyItemNoStars() {
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.confirmButton)).check(doesNotExist());
        onView(withId(R.id.cancelButton)).check(doesNotExist());
    }

    @Test
    public void testPressBuyItemSuccess() {
        FbDatabase.getReference("users.123456789.boughtItems.blue").removeValue();
        SystemClock.sleep(2000);
        setStarsAndRefresh();

        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.confirmButton)).perform(click());

        onView(withId(id)).check(doesNotExist());
    }

    @Test
    public void testSwipeLeftOpensHome() {
        Intents.init();
        final ShopActivity activity = mActivityRule.getActivity();
        executeOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setVisibility(View.GONE, R.id.scrollShop);
            }
        });

        onView(withId(R.id.backgroundAnimation)).perform(swipeRight());
        onView(withId(R.id.backgroundAnimation)).perform(swipeLeft());
        intended(hasComponent(HomeActivity.class.getName()));

        executeOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setVisibility(View.VISIBLE, R.id.scrollShop);
            }
        });
        Intents.release();
    }

    private void executeOnUiThread(Runnable runnable) {
        try {
            runOnUiThread(runnable);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void setStarsAndRefresh() {
        onView(withId(R.id.exitButton)).perform(click());
        Account.getInstance(mActivityRule.getActivity()).setStars(1000);
        onView(withId(R.id.shopButton)).perform(click());
    }
}
