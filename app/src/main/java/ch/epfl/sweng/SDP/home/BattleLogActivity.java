package ch.epfl.sweng.SDP.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForGameResults;

import static ch.epfl.sweng.SDP.utils.AnimUtils.bounceButton;
import static ch.epfl.sweng.SDP.utils.AnimUtils.pressButton;

public class BattleLogActivity extends Activity {

    private static final String TAG = "BattleLogActivity";
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

        ((TextView) findViewById(R.id.exitButton)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.battleLogText)).setTypeface(typeMuro);
        setExitListener();

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
        List<String> players = new ArrayList<>();
        players.add("Champion");
        players.add("Singe_Des_Rues");
        players.add("Onix");
        players.add("Spectr0");
        players.add("Bon_dernier");
        GameResult test = new GameResult(players, 3, 23, 7, bmp, this);
        battleLogView.addView(test.toLayout());
        fetchGameResults();
    }

    private void fetchGameResults() {
        LocalDbHandlerForGameResults localDb = new LocalDbHandlerForGameResults(this, null, 1);
        List<GameResult> gameResults = localDb.getGameResultsFromDb(this);

        for (GameResult gameResult: gameResults) {
            if (gameResult != null) {
                battleLogView.addView(gameResult.toLayout());
            }
        }
    }

    /**
     * Sets listener and animation for exit button.
     */
    private void setExitListener() {
        final TextView exit = findViewById(R.id.exitButton);
        final Context context = this;
        exit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(exit, context);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(view, HomeActivity.MAIN_AMPLITUDE,
                                HomeActivity.MAIN_FREQUENCY, context);
                        launchActivity(HomeActivity.class);
                        break;
                    default:
                }
                return true;
            }
        });
    }
}
