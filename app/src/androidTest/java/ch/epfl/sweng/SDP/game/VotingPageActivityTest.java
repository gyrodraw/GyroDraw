package ch.epfl.sweng.SDP.game;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.RatingBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.SDP.game.VotingPageActivity.disableAnimations;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class VotingPageActivityTest {

    private DataSnapshot dataSnapshotMock;
    private DatabaseError databaseErrorMock;

    @Rule
    public final ActivityTestRule<VotingPageActivity> mActivityRule =
            new ActivityTestRule<VotingPageActivity>(VotingPageActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    disableAnimations();
                }

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra("RoomID", "0123457890");

                    return intent;
                }
            };

    // Add a monitor for the home activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(HomeActivity.class.getName(), null, false);

    @Before
    public void init() {
        dataSnapshotMock = Mockito.mock(DataSnapshot.class);
        databaseErrorMock = Mockito.mock(DatabaseError.class);
    }

    @Test
    public void ratingUsingRatingBarShouldBeSaved() {
        SystemClock.sleep(2000);
        ((RatingBar) mActivityRule.getActivity().findViewById(R.id.ratingBar)).setRating(3);

        SystemClock.sleep(2000);
        assertThat(mActivityRule.getActivity().getRatings()[0], is(3));
    }

    @Test
    public void addStarsHandlesBigNumber() {
        StarAnimationView starsAnimation = mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
        starsAnimation.onSizeChanged(100, 100, 100, 100);
        Canvas canvas = new Canvas();
        starsAnimation.onDraw(canvas);
        starsAnimation.addStars(1000);
        starsAnimation.updateState(1000);
        starsAnimation.onDraw(canvas);
        int stars = starsAnimation.getNumStars();
        assert (5 == stars);
    }

    @Test
    public void addStarsHandlesNegativeNumber() {
        StarAnimationView starsAnimation = mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
        starsAnimation.onSizeChanged(100, 100, 100, 100);
        Canvas canvas = new Canvas();
        starsAnimation.onDraw(canvas);
        starsAnimation.addStars(-10);
        starsAnimation.updateState(1000);
        starsAnimation.onDraw(canvas);
        assert (0 == starsAnimation.getNumStars());
    }

    @Test
    public void startHomeActivityStartsHomeActivity(){
        mActivityRule.getActivity().startHomeActivity(null);
        SystemClock.sleep(2000);
        Activity homeActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        Assert.assertNotNull(homeActivity);
        assertTrue(mActivityRule.getActivity().isFinishing());
    }

    @Test
    public void testStateChange() {
        SystemClock.sleep(1000);
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(4);
        mActivityRule.getActivity().callOnStateChange(dataSnapshotMock);
        SystemClock.sleep(2000);
        RankingFragment myFragment = (RankingFragment) mActivityRule.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.votingPageLayout);
        assertTrue(myFragment.isVisible());
    }

    @Test
    public void testShowDrawingImage() {
        Bitmap image = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        image.eraseColor(android.graphics.Color.GREEN);
        mActivityRule.getActivity().callShowWinnerDrawing(image, "Champion");
    }

    @Test
    public void testChangeImage() {
        short counter = mActivityRule.getActivity().getChangeDrawingCounter();
        SystemClock.sleep(1000);
        mActivityRule.getActivity().callChangeImage();

        SystemClock.sleep(1000);
        assertEquals(counter + 1, mActivityRule.getActivity().getChangeDrawingCounter());
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerState() {
        when(databaseErrorMock.toException()).thenReturn(new DatabaseException("Cancelled"));
        mActivityRule.getActivity().listenerState.onCancelled(databaseErrorMock);
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerCounter() {
        when(databaseErrorMock.toException()).thenReturn(new DatabaseException("Cancelled"));
        mActivityRule.getActivity().listenerCounter.onCancelled(databaseErrorMock);
    }

    @Test
    public void testOnListenerTimerEqualsTimeForVoting() {
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(24);
        mActivityRule.getActivity().callOnCounterChange(dataSnapshotMock);
        SystemClock.sleep(1000);
        assertEquals((mActivityRule.getActivity().findViewById(R.id.playerNameView))
                        .getVisibility(), View.VISIBLE);
    }
}
