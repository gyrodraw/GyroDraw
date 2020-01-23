package ch.epfl.sweng.GyroDraw.shop;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.home.HomeActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.BOUGHT_ITEMS;

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
