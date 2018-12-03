package ch.epfl.sweng.SDP.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForGameResults;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

/**
 * Class representing the battle log.
 */
public class BattleLogActivity extends BaseActivity {

    private LinearLayout battleLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_battle_log);

        battleLogView = findViewById(R.id.battleLog);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        TextView exitButton = findViewById(R.id.exitButton);
        TextView emptyBattleLogText = findViewById(R.id.emptyBattleLogText);
        LayoutUtils.setFadingExitListener(exitButton, this);

        exitButton.setTypeface(typeMuro);
        emptyBattleLogText.setTypeface(typeMuro);
        ((TextView) findViewById(R.id.battleLogText)).setTypeface(typeMuro);

        fetchGameResults();

        // Hide or display the empty battle log text
        ScrollView scrollBattleLog = findViewById(R.id.scrollBattleLog);
        if (battleLogView.getChildCount() == 0) {
            ((ViewManager) scrollBattleLog.getParent()).removeView(scrollBattleLog);
        } else {
            ((ViewManager) emptyBattleLogText.getParent()).removeView(emptyBattleLogText);
        }
    }

    /**
     * Fetches the latest game results in the local database, convert them to views
     * and add them to the layout.
     */
    private void fetchGameResults() {
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
     * Returnsthe number of game result currently displayed.
     */
    @VisibleForTesting
    public int getGameResultsCount() {
        return ((LinearLayout) findViewById(R.id.gameResults)).getChildCount();
    }
}
