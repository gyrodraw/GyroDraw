package ch.epfl.sweng.SDP.game;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.SDP.game.WaitingPageActivity.disableAnimations;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class WaitingPageActivityTest {

    private static final String ROOM_ID_TEST = "0123457890";
    
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
                    intent.putExtra("roomID", ROOM_ID_TEST);
                    intent.putExtra("word1", "word1Mock");
                    intent.putExtra("word2", "word2Mock");

                    return intent;
                }
            };

    @Test
    public void testRadioButton1() {
        clickButtonsTest(R.id.buttonWord1);
    }

    @Test
    public void testRadioButton2() {
        clickButtonsTest(R.id.buttonWord2);
    }

    @Test
    public void testButtonIncreasePeople() {
        Intents.init();
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.incrementButton),
                View.VISIBLE);
        for (int i = 0; i < 4; i++) {
            onView(withId(R.id.incrementButton)).perform(click());
        }

        intended(hasComponent(DrawingActivity.class.getName()));
        Espresso.pressBack();
        Intents.release();
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

        assert(mActivityRule.getActivity().getWord1Votes() == 1);
        assert(mActivityRule.getActivity().getWord2Votes() == 0);
    }

    @Test
    public void areButtonWordsLockedAfterVoteWord2() {
        onView(withId(R.id.buttonWord2)).perform(click());
        onView(withId(R.id.buttonWord2)).perform(click());
        onView(withId(R.id.buttonWord2)).perform(click());

        onView(withId(R.id.buttonWord1)).perform(click());
        onView(withId(R.id.buttonWord1)).perform(click());
        onView(withId(R.id.buttonWord1)).perform(click());

        assert(mActivityRule.getActivity().getWord1Votes() == 0);
        assert(mActivityRule.getActivity().getWord2Votes() == 1);
    }

    @Test
    public void isUserCounterViewVisible() {
        isViewVisible(R.id.playersCounterText);
    }

    @Test
    public void incrementingUsersCountShouldChangeProgressBarAndTextView() {
        waitForVisibility(mActivityRule.getActivity().findViewById(R.id.incrementButton),
                View.VISIBLE);

        onView(withId(R.id.incrementButton)).perform(click());
        onView(withId(R.id.playersCounterText)).check(matches(withText("2/5")));

        onView(withId(R.id.incrementButton)).perform(click());
        onView(withId(R.id.playersCounterText)).check(matches(withText("3/5")));
    }

    @Test
    public void pressBackTest() {
        Intents.init();
        Espresso.pressBack();
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void getWinningWordTest() {
        String[] words = {"cat", "dog"};
        String winningWord = WaitingPageActivity.getWinningWord(2,1,
                words);
        assertEquals("cat", winningWord);

        winningWord = WaitingPageActivity.getWinningWord(2,2,
                words);
        assertEquals("cat", winningWord);

        winningWord = WaitingPageActivity.getWinningWord(2,3,
                words);
        assertEquals("dog", winningWord);
    }

    @Test
    public void testGettersSettersWords() {
        mActivityRule.getActivity().setWord1Votes(10);
        assertEquals(10, mActivityRule.getActivity().getWord1Votes());

        mActivityRule.getActivity().setWord2Votes(10);
        assertEquals(10, mActivityRule.getActivity().getWord2Votes());
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

    @Ignore
    public void waitForVisibility(final View view, final int visibility) {
        Espresso.registerIdlingResources(new ViewVisibilityIdlingResource(view, visibility));
    }
}
