package ch.epfl.sweng.SDP.home;

import android.graphics.Bitmap;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static junit.framework.TestCase.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForGameResults;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static ch.epfl.sweng.SDP.game.drawing.DrawingOnlineTest.bitmapEqualsNewBitmap;
import static ch.epfl.sweng.SDP.game.drawing.DrawingOnlineTest.compressBitmap;
import static ch.epfl.sweng.SDP.game.drawing.DrawingOnlineTest.initializedBitmap;

@RunWith(AndroidJUnit4.class)
public class BattleLogActivityTest {

    @Rule
    public final ActivityTestRule<BattleLogActivity> activityRule =
            new ActivityTestRule<>(BattleLogActivity.class);

    @Test
    public void testClickOnExitButtonOpensHomeActivity() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.exitButton)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void testLocalDb() {
        List<String> rankedUsername = new ArrayList<>();
        rankedUsername.add("User1");
        rankedUsername.add("User2");
        rankedUsername.add("User3");
        rankedUsername.add("User4");
        rankedUsername.add("User5");
        int rank = 2;
        int stars = 15;
        int trophies = -5;
        Bitmap drawing = initializedBitmap();

        GameResult gameResult = new GameResult(rankedUsername, rank, stars, trophies, drawing,
                activityRule.getActivity());

        LocalDbHandlerForGameResults localDbHandler = new LocalDbHandlerForGameResults(
                activityRule.getActivity(), null, 1);
        localDbHandler.addGameResultToDb(gameResult);

        drawing = compressBitmap(drawing, 20);

        GameResult newGameResult =
                localDbHandler.getGameResultsFromDb(activityRule.getActivity()).get(0);

        assertEquals(rankedUsername, newGameResult.getRankedUsername());
        assertEquals(rank, newGameResult.getRank());
        assertEquals(stars, newGameResult.getStars());
        assertEquals(trophies, newGameResult.getTrophies());
        bitmapEqualsNewBitmap(drawing, newGameResult.getDrawing());
    }
}