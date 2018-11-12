package ch.epfl.sweng.SDP.game;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.SDP.game.LoadingScreenActivity.disableLoadingAnimations;
import static ch.epfl.sweng.SDP.game.WaitingPageActivityTest.waitFor;
import static java.util.EnumSet.allOf;
import static junit.framework.Assert.assertTrue;
import static net.bytebuddy.implementation.bind.annotation.IgnoreForBinding.Verifier.check;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    @Rule
    public final ActivityTestRule<LeaderboardActivity> mActivityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    @Test
    public void testSearchFieldVisible() {
        onView(withId(R.id.searchField)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testScrollViewVisible() {
        onView(withId(R.id.scrollLeaderboard)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testModifySearchField() {
        onView(withId(R.id.searchField))
                .perform(typeText("M"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.scrollLeaderboard)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testFriendsButtonsClickable() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        onView(withId(R.id.searchField))
                .perform(typeText(""), ViewActions.closeSoftKeyboard());

        SystemClock.sleep(3000);
        onView(withTagValue(is((Object)"friendsButton0"))).perform(click());
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object)"friendsButton0"))).perform(click());
    }
}
