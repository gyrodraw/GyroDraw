package ch.epfl.sweng.SDP.shop;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static ch.epfl.sweng.SDP.game.VotingPageActivity.disableAnimations;
import static ch.epfl.sweng.SDP.game.WaitingPageActivityTest.waitForVisibility;
import static org.hamcrest.CoreMatchers.is;

public class ShopActivityTest {

    @Rule
    public final ActivityTestRule<ShopActivity> mActivityRule =
            new ActivityTestRule<ShopActivity>(ShopActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    ShopActivity.disableAnimations();
                }
            };

    @Test
    public void testPressItemAndCancel() {
        SystemClock.sleep(3000);
        //waitForVisibility(mActivityRule.getActivity().findViewById(R.id.shopItems), View.VISIBLE);
        onView(withTagValue(is((Object) "blue"))).perform(click());
        onView(withId(R.id.cancelButton)).perform(click());
        onView(withId(R.id.buyButton)).check(doesNotExist());
    }

    /*@Test
    public void testPressBuyItemNoStars() {
        SystemClock.sleep(3000);
        //waitForVisibility(mActivityRule.getActivity().findViewById(R.id.shopItems), View.VISIBLE);
        onView(withTagValue(is((Object) "yellow"))).perform(click());
        onView(withId(R.id.buyButton)).perform(click());
        onView(withId(R.id.okButton)).check(matches(isDisplayed()));
        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());
    }

    @Test
    public void testPressBuyItemSuccess() {
        Account.getInstance(mActivityRule.getActivity().getApplicationContext()).setStars(100);
        SystemClock.sleep(3000);
        //waitForVisibility(mActivityRule.getActivity().findViewById(R.id.shopItems), View.VISIBLE);
        onView(withTagValue(is((Object) "red"))).perform(click());
        onView(withId(R.id.buyButton)).perform(click());
        onView(withId(R.id.okButton)).check(matches(isDisplayed()));
        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());
    }*/
}
