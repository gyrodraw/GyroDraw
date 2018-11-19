package ch.epfl.sweng.SDP.game;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static ch.epfl.sweng.SDP.game.VotingPageActivity.disableAnimations;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class VotingPageActivityTest {

    private DataSnapshot dataSnapshotMock;
    private DatabaseError databaseErrorMock;
    private StarAnimationView starsAnimation;

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

//    // Add a monitor for the home activity
//    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
//            .addMonitor(HomeActivity.class.getName(), null, false);

    @Before
    public void init() {
        dataSnapshotMock = Mockito.mock(DataSnapshot.class);
        databaseErrorMock = Mockito.mock(DatabaseError.class);
        starsAnimation = mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
    }

    @After
    public void freeResources() {
        mActivityRule.getActivity().freeResources();
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
        int previousStars = starsAnimation.getNumStars();
        starsAnimation.onSizeChanged(100, 100, 100, 100);
        Canvas canvas = new Canvas();
        starsAnimation.onDraw(canvas);
        starsAnimation.addStars(1000);
        starsAnimation.updateState(1000);
        starsAnimation.onDraw(canvas);
        int stars = starsAnimation.getNumStars();
        assertThat(stars, is(previousStars + 5));
    }

    @Test
    public void addStarsHandlesNegativeNumber() {
        int previousStars = starsAnimation.getNumStars();
        starsAnimation.onSizeChanged(100, 100, 100, 100);
        Canvas canvas = new Canvas();
        starsAnimation.onDraw(canvas);
        starsAnimation.addStars(-10);
        starsAnimation.updateState(1000);
        starsAnimation.onDraw(canvas);
        assertThat(starsAnimation.getNumStars(), is(previousStars));
    }

    @Test
    public void startHomeActivityStartsHomeActivity(){
        Intents.init();
        SystemClock.sleep(2000);
        mActivityRule.getActivity().startHomeActivity(null);
        SystemClock.sleep(2000);
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testStateChange() {
        SystemClock.sleep(1000);
        when(dataSnapshotMock.getValue(Integer.class)).thenReturn(5);
        mActivityRule.getActivity().callOnStateChange(dataSnapshotMock);
        SystemClock.sleep(2000);

        RankingFragment myFragment = (RankingFragment) mActivityRule.getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.votingPageLayout);
        assertThat(myFragment.isVisible(), is(true));
    }

    @Test
    public void testShowDrawingImage() {
        Bitmap image = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        image.eraseColor(android.graphics.Color.GREEN);
        mActivityRule.getActivity().callShowWinnerDrawing(image, "Champion");
        SystemClock.sleep(3000);

        TextView playerNameView = mActivityRule.getActivity()
                .findViewById(R.id.playerNameView);
        assertThat(playerNameView.getText().toString(), is("Champion"));
    }

    @Test
    public void testChangeImage() {
        short counter = mActivityRule.getActivity().getChangeDrawingCounter();
        SystemClock.sleep(1000);
        mActivityRule.getActivity().callChangeImage();
        SystemClock.sleep(2000);

        assertThat((int) mActivityRule.getActivity().getChangeDrawingCounter(), is(counter + 1));
    }

    @Test(expected = DatabaseException.class)
    public void testOnCancelledListenerState() {
        when(databaseErrorMock.toException()).thenReturn(new DatabaseException("Cancelled"));
        mActivityRule.getActivity().listenerState.onCancelled(databaseErrorMock);
    }

//    @Test(expected = DatabaseException.class)
//    public void testOnCancelledListenerCounter() {
//        when(databaseErrorMock.toException()).thenReturn(new DatabaseException("Cancelled"));
//        mActivityRule.getActivity().listenerCounter.onCancelled(databaseErrorMock);
//    }
}
