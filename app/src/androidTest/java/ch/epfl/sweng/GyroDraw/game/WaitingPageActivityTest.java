package ch.epfl.sweng.GyroDraw.game;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.game.drawing.DrawingOnlineActivity;
import ch.epfl.sweng.GyroDraw.home.HomeActivity;
import ch.epfl.sweng.GyroDraw.utils.network.ConnectivityWrapper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.ROOM_ID;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.WORD_1;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.WORD_2;
import static ch.epfl.sweng.GyroDraw.game.WaitingPageActivity.disableAnimations;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class WaitingPageActivityTest {

    private static final String ROOM_ID_TEST = "0123457890";

    private DataSnapshot dataSnapshotMock;
    private DatabaseError databaseErrorMock;
    private Intent intentMock;
    private ConnectivityManager connectivityManagerMock;
    private Context spyContext;

    @Rule
    public final ActivityTestRule<WaitingPageActivity> mActivityRule =
            new ActivityTestRule<WaitingPageActivity>(WaitingPageActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    disableAnimations();
                }

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(ROOM_ID, ROOM_ID_TEST);
                    intent.putExtra(WORD_1, "word1Mock");
                    intent.putExtra(WORD_2, "word2Mock");

                    return intent;
                }
            };

    @Before
    public void init() {
        dataSnapshotMock = Mockito.mock(DataSnapshot.class);
        databaseErrorMock = Mockito.mock(DatabaseError.class);
        intentMock = Mockito.mock(Intent.class);
        connectivityManagerMock = Mockito.mock(ConnectivityManager.class);
        spyContext = Mockito.spy(mActivityRule.getActivity());
    }

    /**
     * Perform action of waiting for a specific time.
     */
    @Ignore
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    @Test
    public void testLeaveButtonClick() {
        Intents.init();
        clickButtonsTest(R.id.leaveButton);
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testLeaveButtonDisplayed() {
        isViewVisible(R.id.leaveButton);
    }

    @Test
    public void testLeaveButtonClickable() {
        isViewVisible(R.id.leaveButton);
    }

    @Test
    public void testRadioButton1() {
        clickButtonsTest(R.id.buttonWord1);
    }

    @Test
    public void testRadioButton2() {
        clickButtonsTest(R.id.buttonWord2);
    }

    @Test
    public void isButtonWord1Visible() {
        isViewVisible(R.id.buttonWord1);
    }

    @Test
    public void isButtonWord2Visible() {
        isViewVisible(R.id.buttonWord2);
    }

    @Test
    public void isButtonWord1Clickable() {
        isViewClickable(R.id.buttonWord1);
    }

    @Test
    public void isButtonWord2Clickable() {
        isViewClickable(R.id.buttonWord2);
    }

    @Test
    public void areButtonWordsLockedAfterVoteWord1() {
        onView(withId(R.id.buttonWord1)).perform(click());
        onView(withId(R.id.buttonWord1)).perform(click());
        onView(withId(R.id.buttonWord1)).perform(click());

        onView(withId(R.id.buttonWord2)).perform(click());
        onView(withId(R.id.buttonWord2)).perform(click());
        onView(withId(R.id.buttonWord2)).perform(click());

        assertThat(mActivityRule.getActivity().getWord1Votes(), is(1));
        assertThat(mActivityRule.getActivity().getWord2Votes(), is(0));
    }

    @Test
    public void areButtonWordsLockedAfterVoteWord2() {
        onView(withId(R.id.buttonWord2)).perform(click());
        onView(withId(R.id.buttonWord2)).perform(click());
        onView(withId(R.id.buttonWord2)).perform(click());

        onView(withId(R.id.buttonWord1)).perform(click());
        onView(withId(R.id.buttonWord1)).perform(click());
        onView(withId(R.id.buttonWord1)).perform(click());

        assertThat(mActivityRule.getActivity().getWord1Votes(), is(0));
        assertThat(mActivityRule.getActivity().getWord2Votes(), is(1));
    }

    @Test
    public void isUserCounterViewVisible() {
        isViewVisible(R.id.playersCounterText);
    }

    @Test
    public void getWinningWordTest() {
        String[] words = {"cat", "dog"};
        String winningWord = WaitingPageActivity.getWinningWord(2, 1,
                words);
        assertThat(winningWord, is("cat"));

        winningWord = WaitingPageActivity.getWinningWord(2, 2,
                words);
        assertThat(winningWord, is("cat"));

        winningWord = WaitingPageActivity.getWinningWord(2, 3,
                words);
        assertThat(winningWord, is("dog"));
    }

    @Test
    public void testGettersSettersWords() {
        mActivityRule.getActivity().setWord1Votes(10);
        assertThat(mActivityRule.getActivity().getWord1Votes(), is(10));

        mActivityRule.getActivity().setWord2Votes(10);
        assertThat(mActivityRule.getActivity().getWord2Votes(), is(10));
    }

    @Test
    public void testOnState0Change() {
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(0);
        mActivityRule.getActivity().callOnDataChange(dataSnapshotMock);
    }

    @Test
    public void testOnState1Change() {
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(1);
        mActivityRule.getActivity().callOnDataChange(dataSnapshotMock);
    }

    @Test
    public void testOnState2Change() {
        Intents.init();

        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(2);
        mActivityRule.getActivity().listenerState.onDataChange(dataSnapshotMock);

        intended(hasComponent(DrawingOnlineActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testListenerTimer() {
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(5);
        mActivityRule.getActivity().callOnDataChange(dataSnapshotMock);

        onView(withId(R.id.waitingTime)).check(matches(withText("5")));
    }

    /**
     * Check if the view is displayed.
     *
     * @param id Id of the view
     */
    @Ignore
    public void isViewVisible(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isDisplayed()));
    }

    /**
     * Check if the views are clickable.
     *
     * @param id Id of the view
     */
    @Ignore
    public void isViewClickable(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id), View.VISIBLE);
        onView(withId(id)).check(matches(isClickable()));
    }

    /**
     * Check if we can click the buttons.
     *
     * @param id Id of the view
     */
    @Ignore
    public void clickButtonsTest(final int id) {
        waitForVisibility(mActivityRule.getActivity().findViewById(id),
                View.VISIBLE);
        onView(withId(id)).perform(click());
    }

    @Ignore
    public static void waitForVisibility(final View view, final int visibility) {
        IdlingRegistry.getInstance().register(new ViewVisibilityIdlingResource(view, visibility));
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerState() {
        when(databaseErrorMock.toException()).thenReturn(
                new DatabaseException("Cancelled listener state"));
        mActivityRule.getActivity().listenerState.onCancelled(databaseErrorMock);
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerTimer() {
        when(databaseErrorMock.toException()).thenReturn(
                new DatabaseException("Cancelled listener timer"));
        mActivityRule.getActivity().listenerTimer.onCancelled(databaseErrorMock);
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerWord1() {
        when(databaseErrorMock.toException()).thenReturn(
                new DatabaseException("Cancelled listener word1"));
        mActivityRule.getActivity().listenerWord1.onCancelled(databaseErrorMock);
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerWord2() {
        when(databaseErrorMock.toException()).thenReturn(
                new DatabaseException("Cancelled listener word2"));
        mActivityRule.getActivity().listenerWord2.onCancelled(databaseErrorMock);
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerUsers() {
        when(databaseErrorMock.toException()).thenReturn(
                new DatabaseException("Cancelled listener users"));
        mActivityRule.getActivity().listenerCountUsers.onCancelled(databaseErrorMock);
    }

    @Test
    public void testVoteAndRemoveVote() {
        Intents.init();
        clickButtonsTest(R.id.buttonWord1);
        clickButtonsTest(R.id.leaveButton);
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testOnReceiveNetworkDisabled() {
        when(connectivityManagerMock.getActiveNetworkInfo()).thenReturn(null);
        when(((ConnectivityManager) spyContext.getSystemService(Context.CONNECTIVITY_SERVICE)))
                .thenReturn(connectivityManagerMock);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return null;
            }
        }).when(spyContext).startActivity(any(Intent.class));

        when(intentMock.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE))
                .thenReturn(true);
        when(intentMock.getExtras()).thenReturn(new Bundle());

        ConnectivityWrapper.callOnReceiveNetwork(spyContext, intentMock);
        SystemClock.sleep(1500);

        onView(withId(R.id.okDisconnectedButton)).check(matches(isDisplayed()));
        onView(withId(R.id.okDisconnectedButton)).perform(click());

    }
}
