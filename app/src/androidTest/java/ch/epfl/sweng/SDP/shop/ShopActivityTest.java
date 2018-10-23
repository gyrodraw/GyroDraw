package ch.epfl.sweng.SDP.shop;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {

    @Rule
    public final ActivityTestRule<ShopActivity> activityTestRule =
            new ActivityTestRule<>(ShopActivity.class);

    @Test
    public void returnIsClickable() {
        onView(ViewMatchers.withId(R.id.returnFromShop)).check(matches(isClickable()));
    }
}
