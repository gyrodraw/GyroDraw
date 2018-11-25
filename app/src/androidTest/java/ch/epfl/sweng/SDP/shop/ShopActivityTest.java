package ch.epfl.sweng.SDP.shop;

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
import static ch.epfl.sweng.SDP.game.WaitingPageActivityTest.waitForVisibility;
import static org.hamcrest.CoreMatchers.is;

public class ShopActivityTest {

    @Rule
    public final ActivityTestRule<ShopActivity> mActivityRule =
            new ActivityTestRule<ShopActivity>(ShopActivity.class);

    @Test
    public void testPressItemAndCancel() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.ShopItems), View.VISIBLE);
        onView(withTagValue(is((Object) "blue"))).perform(click());
        onView(withId(R.id.cancelButton)).perform(click());
        onView(withId(R.id.buyButton)).check(doesNotExist());
    }

    @Test
    public void testPressBuyItemNoStars() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.ShopItems), View.VISIBLE);
        onView(withTagValue(is((Object) "red"))).perform(click());
        onView(withId(R.id.buyButton)).perform(click());
        onView(withId(R.id.okButton)).check(matches(isDisplayed()));
        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());
    }

    @Test
    public void testPressBuyItemSuccess() {
        Account.getInstance(mActivityRule.getActivity().getApplicationContext()).setStars(500);
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.ShopItems), View.VISIBLE);
        onView(withTagValue(is((Object) "red"))).perform(click());
        onView(withId(R.id.buyButton)).perform(click());
        onView(withId(R.id.okButton)).check(matches(isDisplayed()));
        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());
    }
}
