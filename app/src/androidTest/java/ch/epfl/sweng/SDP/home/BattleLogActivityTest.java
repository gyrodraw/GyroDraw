package ch.epfl.sweng.SDP.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForGameResults;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.SDP.game.drawing.DrawingOnlineTest.bitmapEqualsNewBitmap;
import static ch.epfl.sweng.SDP.game.drawing.DrawingOnlineTest.compressBitmap;
import static ch.epfl.sweng.SDP.game.drawing.DrawingOnlineTest.initializedBitmap;
import static ch.epfl.sweng.SDP.home.leaderboard.LeaderboardActivityTest.testExitButtonBody;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@RunWith(AndroidJUnit4.class)
public class BattleLogActivityTest {

    private static List<String> rankedUsernames = getUsernameList();

    private static final int RANK = 2;
    private static final int STARS = 15;
    private static final int TROPHIES = -5;
    private final Bitmap DRAWING = initializedBitmap();

    private GameResult gameResult;
    private LocalDbHandlerForGameResults localDbHandler;

    @Rule
    public final ActivityTestRule<BattleLogActivity> activityRule =
            new ActivityTestRule<>(BattleLogActivity.class);

    /**
     * Initialize the game result and the local database handler.
     */
    @Before
    public void init() {
        gameResult = new GameResult(rankedUsernames, RANK, STARS, TROPHIES, DRAWING,
                activityRule.getActivity());
        localDbHandler = new LocalDbHandlerForGameResults(
                activityRule.getActivity(), null, 1);
    }

    @Test
    public void testClickOnExitButtonOpensHomeActivity() {
        testExitButtonBody();
    }

    @Test
    public void testLocalDb() {
        localDbHandler.addGameResultToDb(gameResult);
        GameResult newGameResult =
                localDbHandler.getGameResultsFromDb(activityRule.getActivity()).get(0);

        assertThat(newGameResult.getRankedUsername(), is(rankedUsernames));
        assertThat(newGameResult.getRank(), is(RANK));
        assertThat(newGameResult.getStars(), is(STARS));
        assertThat(newGameResult.getTrophies(), is(TROPHIES));
        Bitmap compressedDrawing = compressBitmap(DRAWING, 20);
        bitmapEqualsNewBitmap(compressedDrawing, newGameResult.getDrawing());
    }

    @Test
    public void testGameResult() {
        assertThat(gameResult.getRankedUsername(), is(rankedUsernames));
        assertThat(gameResult.getRank(), is(RANK));
        assertThat(gameResult.getStars(), is(STARS));
        assertThat(gameResult.getTrophies(), is(TROPHIES));
        bitmapEqualsNewBitmap(DRAWING, gameResult.getDrawing());
    }

    @Test
    public void testGameResultToLayout() {
        localDbHandler.addGameResultToDb(gameResult);
        onView(withId(R.id.exitButton)).perform(click());
        onView(withId(R.id.battleLogButton)).perform(click());
        assertThat(activityRule.getActivity().getGameResultsCount(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testNullBitmapToDatabase() {
        localDbHandler.addGameResultToDb(new GameResult(
                rankedUsernames, RANK, STARS, TROPHIES, null, activityRule.getActivity()));
        Activity activity = activityRule.getActivity();
        Bitmap drawing = localDbHandler.getGameResultsFromDb(activity).get(0).getDrawing();
        for (int i = 5; i < 10; i++) {
            for (int j = 5; j < 10; j++) {
                assertThat(drawing.getPixel(i, j), is(0xFFFFFFFF));
            }
        }
    }

    private static List<String> getUsernameList() {
        List<String> rankedUsername = new ArrayList<>();
        rankedUsername.add("User1");
        rankedUsername.add("User2");
        rankedUsername.add("User3");
        rankedUsername.add("User4");
        rankedUsername.add("User5");

        return rankedUsername;
    }
}
