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

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;

import static ch.epfl.sweng.SDP.utils.AnimUtils.bounceButton;
import static ch.epfl.sweng.SDP.utils.AnimUtils.pressButton;

public class BattleLogActivity extends Activity {

    private static final String TAG = "BattleLogActivity";
    private Typeface typeMuro;
    private LinearLayout battleLogView;
    private GameResult[] battleLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_battle_log);
        battleLogView = findViewById(R.id.battleLog);

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        ((TextView) findViewById(R.id.exitButton)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.battleLogText)).setTypeface(typeMuro);
        setExitListener();

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.battle_log_button);

        String[] players = {"Champion", "Singe_Des_Rues", "Onix", "Spectr0", "Bon_dernier"};
        GameResult test = new GameResult(players, 3, 27, 8, bmp, this);

        battleLogView.addView(test.toLayout());
        battleLogView.addView(test.toLayout());
        battleLogView.addView(test.toLayout());
        battleLogView.addView(test.toLayout());
    }

    private void addViewsToLog() {
        for (GameResult aBattleLog : battleLog) {
            battleLogView.addView(aBattleLog.toLayout());
        }
    }

    private void fetchGameResults() {

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
