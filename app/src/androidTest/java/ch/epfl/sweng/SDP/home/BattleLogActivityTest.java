package ch.epfl.sweng.SDP.home;

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
import static ch.epfl.sweng.SDP.home.LeaderboardActivityTest.testExitButtonBody;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class BattleLogActivityTest {

    private static List<String> rankedUsernames = getUsernameList();

    private static final int RANK = 2;
    private static final int STARS = 15;
    private static final int TROPHIES = -5;
    private static final Bitmap DRAWING = initializedBitmap();

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

        assertEquals(rankedUsernames, newGameResult.getRankedUsername());
        assertEquals(RANK, newGameResult.getRank());
        assertEquals(STARS, newGameResult.getStars());
        assertEquals(TROPHIES, newGameResult.getTrophies());
        Bitmap compressedDrawing = compressBitmap(DRAWING, 20);
        bitmapEqualsNewBitmap(compressedDrawing, newGameResult.getDrawing());
    }

    @Test
    public void testGameResult() {
        assertEquals(rankedUsernames, gameResult.getRankedUsername());
        assertEquals(RANK, gameResult.getRank());
        assertEquals(STARS, gameResult.getStars());
        assertEquals(TROPHIES, gameResult.getTrophies());
        bitmapEqualsNewBitmap(DRAWING, gameResult.getDrawing());
    }

    @Test
    public void testGameResultToLayout() {
        localDbHandler.addGameResultToDb(gameResult);
        onView(withId(R.id.exitButton)).perform(click());
        onView(withId(R.id.battleLogButton)).perform(click());
        assertTrue(activityRule.getActivity().getGameResultsCount() >= 1);
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
