package ch.epfl.sweng.GyroDraw.home;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity;
import ch.epfl.sweng.GyroDraw.game.drawing.DrawingOfflineActivity;
import ch.epfl.sweng.GyroDraw.home.leaderboard.LeaderboardActivity;
import ch.epfl.sweng.GyroDraw.home.leagues.LeaguesActivity;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForAccount;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.GyroDraw.shop.ShopActivity;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.disableLoadingAnimations;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.setOnTest;
import static ch.epfl.sweng.GyroDraw.home.HomeActivity.disableBackgroundAnimation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import androidx.test.rule.ActivityTestRule;
import ch.epfl.sweng.GyroDraw.home.gallery.GalleryActivity;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    private static final String FRIEND_ACCOUNT = "FriendAccount";
    private static final String FRIEND_ID = "FriendId123ForTesting";

    @Rule
    public final ActivityTestRule<HomeActivity> activityRule =
            new ActivityTestRule<HomeActivity>(HomeActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    disableBackgroundAnimation();
                    disableLoadingAnimations();
                    setOnTest();
                }
            };

    @Before
    public void init() {
        Intents.init();
    }

    @After
    public void release() {
        Intents.release();
    }

    @Test
    public void testLocalDb() {
        LocalDbForAccount localDbHandler = new LocalDbHandlerForAccount(
                activityRule.getActivity(), null, 1);
        localDbHandler.saveAccount(Account.getInstance(activityRule.getActivity()));
        localDbHandler.retrieveAccount(Account.getInstance(activityRule.getActivity()));
    }

    @Test
    public void testDrawButtonOpensWaitingPageActivity() {
        onView(ViewMatchers.withId(R.id.drawButton)).perform(click());
        intended(hasComponent(LoadingScreenActivity.class.getName()));
    }

    @Test
    public void testClickOnLeagueImageOpensLeaguesActivity() {
        onView(ViewMatchers.withId(R.id.leagueImage)).perform(click());
        intended(hasComponent(LeaguesActivity.class.getName()));
    }

    @Test
    public void testClickOnLeaderboardButtonOpensLeaderboardActivity() {
        onView(ViewMatchers.withId(R.id.leaderboardButton)).perform(click());
        intended(hasComponent(LeaderboardActivity.class.getName()));
    }

    @Test
    public void testClickOnPracticeButtonOpensDrawingOffline() {
        onView(ViewMatchers.withId(R.id.practiceButton)).perform(click());
        intended(hasComponent(DrawingOfflineActivity.class.getName()));
    }

    @Test
    public void testClickOnMysteryButtonOpensWaitingPageActivity() {
        onView(ViewMatchers.withId(R.id.mysteryButton)).perform(click());
        intended(hasComponent(LoadingScreenActivity.class.getName()));
    }

    @Test
    public void testTrophiesButtonIsClickable() {
        onView(withId(R.id.trophiesButton)).perform(click());
        onView(withId(R.id.trophiesButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testStarsButtonIsClickable() {
        onView(withId(R.id.starsButton)).perform(click());
        onView(withId(R.id.starsButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testLeagueImageIsVisible() {
        onView(withId(R.id.leagueImage)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void testUsernameOpensPopUp() {
        activityRule.getActivity().getFriendRequestWindow().dismiss();
        onView(withId(R.id.usernameButton)).perform(click());
        onView(withId(R.id.usernamePopUp)).check(matches(isDisplayed()));
    }

    @Test
    public void testFriendsRequestAccept() {
        activityRule.getActivity().getFriendRequestWindow().dismiss();
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().showFriendRequestPopup(FRIEND_ACCOUNT, FRIEND_ID);
            }
        });
        onView(withId(R.id.acceptButton)).perform(click());
        onView(withId(R.id.friendRequestPopUp)).check(doesNotExist());
    }

    @Test
    public void testFriendsRequestPopUpGetsDisplayed() {
        activityRule.getActivity().getFriendsUsernameAndShowPopUp("123456789");
        SystemClock.sleep(2000);
        onView(withId(R.id.friendRequestPopUp)).check(matches(isDisplayed()));
    }

    @Test
    public void testFriendsRequestReject() {
        activityRule.getActivity().getFriendRequestWindow().dismiss();
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().showFriendRequestPopup(FRIEND_ACCOUNT, FRIEND_ID);
            }
        });
        onView(withId(R.id.rejectButton)).perform(click());
        onView(withId(R.id.friendRequestPopUp)).check(doesNotExist());
    }

    @Test
    public void testLaunchShop() {
        onView(withId(R.id.shopButton)).perform(click());
        intended(hasComponent(ShopActivity.class.getName()));
    }

    @Test
    public void testLaunchGallery() {
        onView(withId(R.id.galleryButton)).perform(click());
        intended(hasComponent(GalleryActivity.class.getName()));
    }

    @Test
    public void testCrossClosesPopUp() {
        openAndClosePopUp(R.id.crossText);
    }

    @Test
    public void testCanSignOutAccount() {
        openAndClosePopUp(R.id.signOutButton);
    }

    private void openAndClosePopUp(int view) {
        onView(withId(R.id.usernameButton)).perform(click());
        onView(withId(view)).perform(click());
        onView(withId(R.id.usernamePopUp)).check(doesNotExist());
    }

    // Add a monitor for the home activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    @Test
    public void testClickingOnBackButtonDoesNothing() {
        pressBack();
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 3000);
        assertThat(homeActivity, is(not(nullValue())));
    }
}
