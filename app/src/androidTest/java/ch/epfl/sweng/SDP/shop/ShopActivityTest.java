package ch.epfl.sweng.SDP.shop;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.SDP.game.WaitingPageActivityTest.waitForVisibility;

public class ShopActivityTest {

    private static String USER_ID = "123456789";
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

    @Test
    public void testPressItemAndCancel() {
        SystemClock.sleep(5000);
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.cancelButton)).perform(click());
        onView(withId(R.id.buyButton)).check(doesNotExist());
    }

    @Test
    public void testPressBuyItemNoStars() {
        //waitForVisibility(mActivityRule.getActivity().findViewById(R.id.shopItems), View.VISIBLE);
        SystemClock.sleep(5000);
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.buyButton)).perform(click());

        onView(withId(R.id.confirmationText)).check(matches(withText("Error")));
        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());


    }

    @Test
    public void testPressBuyItemSuccess() {
        Account.getInstance(mActivityRule.getActivity().getApplicationContext()).setStars(200);
        SystemClock.sleep(5000);
        //waitForVisibility(mActivityRule.getActivity().findViewById(R.id.shopItems), View.VISIBLE);
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.buyButton)).perform(click());

        onView(withId(R.id.confirmationText)).check(matches(withText("Success")));

        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());
    }
}
