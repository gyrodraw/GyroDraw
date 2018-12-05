package ch.epfl.sweng.SDP.shop;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {

    private static final String USER_ID = "no_user";
    private static DatabaseReference usersRef = Database.getReference("users."
            + USER_ID + ".boughtItems");

    @Rule
    public final ActivityTestRule<ShopActivity> mActivityRule =
            new ActivityTestRule<ShopActivity>(ShopActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    ShopActivity.disableAnimations();
                    usersRef.removeValue();
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
        Database.constructBuilder(usersRef).addChildren("123456789.boughtItems.blue")
                .build().removeValue();
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

    private void setStarsAndRefresh() {
        onView(withId(R.id.exitButton)).perform(click());
        Account.getInstance(mActivityRule.getActivity()).setStars(1000);
        onView(withId(R.id.shopButton)).perform(click());
    }
}
