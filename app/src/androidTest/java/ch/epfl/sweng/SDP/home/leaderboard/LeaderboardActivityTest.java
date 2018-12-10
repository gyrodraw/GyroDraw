package ch.epfl.sweng.SDP.home.leaderboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

import static android.support.test.espresso.Espresso.onView;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.support.test.espresso.intent.Intents;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.home.HomeActivity;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LeaderboardActivityTest {

    private static final String USER_ID = "123456789";
    private static final String TEST_EMAIL = "testEmail";
    private static final String USERNAME = "username";

    @Rule
    public final ActivityTestRule<LeaderboardActivity> activityRule =
            new ActivityTestRule<>(LeaderboardActivity.class);

    private Account account;
    private Context context;

    /**
     * Sets all necessary values in account for testing purposes.
     */
    @Before
    public void initialize() {
        context = activityRule.getActivity().getApplicationContext();
        account = Account.getInstance(context);
        account.setEmail(TEST_EMAIL);
        account.setUsername(USERNAME);
        account.setUserId(USER_ID);
    }

    @Test
    public void testSearchFieldClickable() {
        onView(withId(R.id.searchField))
                .perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.searchField)).check(matches(isClickable()));
    }

    @Test
    public void testClickOnExitButtonOpensHomeActivity() {
        testExitButtonBody();
    }

    @Test
    public void testFriendsButtonReceivedIsInitializedCorrectly() {
        Player player = new Player(context, USER_ID, USERNAME,
                0L, "leagueOne", false);
        FriendsButton friendsButton = new FriendsButton(
                context, player, 1, false);
        friendsButton.initializeImageCorrespondingToFriendsState(
                FriendsRequestState.RECEIVED.ordinal());
        assertDrawablesAreIdentical(friendsButton.getDrawable(),
                context.getDrawable(R.drawable.add_friend));
    }

    @Test
    public void testFriendsButtonReceivedIsUpdatedCorrectly() {
        FriendsButton friendsButton = new FriendsButton(
                context, new Player(context, USER_ID, USERNAME,
                0L, "leagueThree", false), 2, false);
        friendsButton.setImageAndUpdateFriendsState(
                FriendsRequestState.RECEIVED.ordinal());
        assertDrawablesAreIdentical(friendsButton.getDrawable(),
                context.getDrawable(R.drawable.remove_friend));
    }

    @Test
    public void testFriendsButtonChangesDrawableCorrectly() {
        String buttonTag = "friendsButton0";
        SystemClock.sleep(2000);
        ImageView imageView = (ImageView) (getViewsByTag(
                (LinearLayout) activityRule.getActivity().findViewById(R.id.leaderboard),
                buttonTag)).get(0);
        Drawable image = imageView.getDrawable();
        onView(withTagValue(is((Object) buttonTag))).perform(click());
        SystemClock.sleep(1000);
        assertDrawablesAreIdentical(imageView.getDrawable(), image);
        SystemClock.sleep(1000);
        onView(withTagValue(is((Object) buttonTag))).perform(click());
        assertDrawablesAreIdentical(imageView.getDrawable(), image);
    }

    @Test
    public void testFilterButtonBehavesCorrectly() {
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.searchField)).perform(typeText("P"));
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(1000);
        TextView filterText = activityRule.getActivity().findViewById(R.id.friendsFilterText);
        assertThat(filterText.getText().toString(),
                is(activityRule.getActivity().getResources().getString(R.string.friendsFilter)));
    }

    @Test
    public void testLeaderboardIsSearchable() {
        onView(withId(R.id.searchField)).perform(typeText("PICASSO"));
        SystemClock.sleep(1000);
        assertThat(((LinearLayout) activityRule.getActivity().findViewById(R.id.leaderboard))
                .getChildCount(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testFriendsAreSearchable() {
        friendsTest(FriendsRequestState.FRIENDS.ordinal(), 1);
    }

    @Test
    public void testSentRequestsDontAppearInFriends() {
        friendsTest(FriendsRequestState.SENT.ordinal(), 0);
    }

    private void friendsTest(int state, int expected) {
        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .setValue(state);
        SystemClock.sleep(2000);
        activityRule.getActivity().initLeaderboard();
        onView(withId(R.id.friendsFilter)).perform(click());
        SystemClock.sleep(2000);
        onView(withId(R.id.searchField)).perform(typeText("PICASSO"));
        SystemClock.sleep(2000);
        assertThat(((LinearLayout) activityRule.getActivity()
                .findViewById(R.id.leaderboard)).getChildCount(), is(expected));
    }

    /**
     * Body of a test that tests if an exit button opens the home page.
     */
    public static void testExitButtonBody() {
        Intents.init();
        onView(withId(R.id.exitButton)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    /**
     * Searches a LinearLayout for all childs with the given tag value.
     *
     * @param root  LinearLayout to search in
     * @param tag   Tag value to search
     * @return      List of all childs with given tag
     */
    private static ArrayList<View> getViewsByTag(LinearLayout root, String tag){
        ArrayList<View> views = new ArrayList<>();

        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((LinearLayout) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }

    /**
     * Compares two drawables to check if they are identical.
     * Code from https://spotandroid.com/2017/02/15/android-tricks-how-to-compare-two-drawables/
     *
     * @param drawableA first drawable
     * @param drawableB second drawable
     */
    private static void assertDrawablesAreIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        assertThat(stateA != null && stateB != null && stateA.equals(stateB)
                || getBitmap(drawableA).sameAs(getBitmap(drawableB)), is(true));
    }

    private static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            // Some drawables have no intrinsic width - e.g. solid colours.
            int width = Math.max(1, drawable.getIntrinsicWidth());
            int height = Math.max(1, drawable.getIntrinsicHeight());

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }
}
