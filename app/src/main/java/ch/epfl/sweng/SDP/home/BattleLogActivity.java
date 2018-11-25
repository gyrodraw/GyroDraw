package ch.epfl.sweng.SDP.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForGameResults;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

public class BattleLogActivity extends Activity {

    private LinearLayout battleLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_battle_log);
        battleLogView = findViewById(R.id.battleLog);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        TextView exitButton = findViewById(R.id.exitButton);
        LayoutUtils.setExitListener(exitButton, this);
        exitButton.setTypeface(typeMuro);
        ((TextView) findViewById(R.id.battleLogText)).setTypeface(typeMuro);

        fetchGameResults();
    }

    /**
     * Fetch the latest game results in the local database, convert them to views
     * and add them to the layout.
     */
    public void fetchGameResults() {
        LocalDbHandlerForGameResults localDb =
                new LocalDbHandlerForGameResults(this, null, 1);
        List<GameResult> gameResults = localDb.getGameResultsFromDb(this);

        for (GameResult gameResult : gameResults) {
            if (gameResult != null) {
                battleLogView.addView(gameResult.toLayout());
            }
        }
    }

    /**
     * Return the number of game result currently displayed.
     */
    @VisibleForTesting
    public int getGameResultsCount() {
        return ((LinearLayout) findViewById(R.id.gameResults)).getChildCount();
    }
}