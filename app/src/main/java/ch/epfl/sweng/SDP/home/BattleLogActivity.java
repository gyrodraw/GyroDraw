package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Locale;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;

import static ch.epfl.sweng.SDP.utils.AnimUtils.bounceButton;
import static ch.epfl.sweng.SDP.utils.AnimUtils.pressButton;

public class BattleLogActivity extends Activity {

    private static final String TAG = "BattleLogActivity";
    private static final String FIREBASE_ERROR = "There was a problem with Firebase";
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

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(100, 100, conf);

        String[] players = {"Champion", "Singe_Des_Rues", "Onix", "Spectr0", "Bon_dernier"};
        GameResult test = new GameResult(players, 3, bmp, 27, 8);

        battleLogView.addView(test.toLayout(this));
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

    /**
     * Helper classes to manage and display the game results from the local database.
     */
    private class GameResult {

        private final String[] rankedUsername;
        private final int rank;
        private final Bitmap drawing;
        private final int stars;
        private final int trophies;

        private GameResult(String[] rankedUsername, int rank, Bitmap drawing, int stars, int trophies) {
            assert 0 <= rank && rank < 5;
            assert rankedUsername.length <= 5;
            this.rankedUsername = rankedUsername;
            this.rank = rank;
            this.drawing = drawing;
            this.stars = stars;
            this.trophies = trophies;
        }

        /**
         * Converts this game result into a LinearLayout
         * that will be displayed in the log battle.
         *
         * @param context of the app
         * @return LinearLayout that will be displayed
         */
        @SuppressLint("NewApi")
        private LinearLayout toLayout(final Context context) {
            LinearLayout layout = new LinearLayout(context);
            for (int i = 0; i < rankedUsername.length; i++) {
                String prefix = (i + 1) + ". ";
                if (i == rank) {
                    addUserLayout(context, prefix + rankedUsername[i], layout);
                } else {
                    addRankLayout(context, prefix + rankedUsername[i], layout);
                }
            }

            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(0, 0, 0, 18);
            return layout;
        }

        private void addRankLayout(final Context context, String username, LinearLayout parent) {
            TextView rankAndUsername = new TextView(context);
            Resources res = getResources();
            styleView(rankAndUsername, username, 15, res.getColor(R.color.colorDrawYellow),
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            LinearLayout item = new LinearLayout(context);
            item.addView(rankAndUsername);
            item.setBackgroundColor(res.getColor(R.color.colorLightGrey));
            item.setPadding(30, 2, 30, 2);

            parent.addView(item);
        }

        private void addUserLayout(final Context context, String username, LinearLayout parent) {
            LinearLayout item = new LinearLayout(context);
            LayoutInflater.from(context).inflate(R.layout.battle_log_item, parent, false);

            TextView rankAndUsername = item.findViewById(R.id.playerName);
            TextView starsWon = item.findViewById(R.id.starsWon);
            TextView trophiesWon = item.findViewById(R.id.trophiesWon);
            ImageView drawingView = item.findViewById(R.id.drawing);

            rankAndUsername.setText(String.format(Locale.getDefault(),
                    "%d. %s", rank + 1, username));
            rankAndUsername.setTypeface(typeMuro);

            starsWon.setText(String.format(Locale.getDefault(),
                    "+ %d", stars));
            rankAndUsername.setTypeface(typeMuro);

            String prefix = trophies >= 0 ? "+" : "";
            trophiesWon.setText(String.format(Locale.getDefault(),
                    "%s %d", prefix, trophies));
            trophiesWon.setTypeface(typeMuro);

            drawingView.setImageBitmap(drawing);

            parent.addView(item);
        }

        private void styleView(TextView view, String text, int textSize, int color,
                               LinearLayout.LayoutParams layoutParams) {
            view.setText(text);
            view.setTextSize(textSize);
            view.setTextColor(color);
            view.setTypeface(typeMuro);
            view.setLayoutParams(layoutParams);
        }
    }

}

