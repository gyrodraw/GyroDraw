package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
        GameResult test = new GameResult(players, 3, bmp, 27, 8, this);

        battleLogView.addView(test.toLayout());
        battleLogView.addView(test.toLayout());
        battleLogView.addView(test.toLayout());
        battleLogView.addView(test.toLayout());
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

        private static final int USERNAME_SIZE = 20;
        private static final int REWARD_SIZE = 15;
        private final String[] rankedUsername;
        private final int rank;
        private final Bitmap drawing;
        private final int stars;
        private final int trophies;
        private Context context;
        private Resources res;

        private GameResult(String[] rankedUsername, int rank, Bitmap drawing, int stars,
                           int trophies, Context context) {
            assert 0 <= rank && rank < 5;
            assert rankedUsername.length <= 5;
            this.rankedUsername = rankedUsername;
            this.rank = rank;
            this.drawing = drawing;
            this.stars = stars;
            this.trophies = trophies;
            this.context = context;
            res = getResources();
        }

        /**
         * Converts this game result into a LinearLayout
         * that will be displayed in the log battle.
         *
         * @return LinearLayout that will be displayed
         */
        @SuppressLint("NewApi")
        private LinearLayout toLayout() {
            LinearLayout layout = new LinearLayout(context);

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 30);

            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < rankedUsername.length; i++) {
                String prefix = (i + 1) + ". ";
                if (i == rank) {
                    layout.addView(userLayout());
                } else {
                    layout.addView(rankLayout(prefix + rankedUsername[i]));
                }
            }

            return layout;
        }

        private LinearLayout rankLayout(String username) {
            TextView rankAndUsername = new TextView(context);

            styleView(rankAndUsername, username, USERNAME_SIZE,
                    res.getColor(R.color.colorDrawYellow),
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);

            LinearLayout fragment = new LinearLayout(context);
            fragment.setLayoutParams(params);
            fragment.addView(rankAndUsername);
            fragment.setBackgroundColor(res.getColor(R.color.colorLightGrey));
            fragment.setPadding(30, 0, 30, 0);

            return fragment;
        }

        private LinearLayout userLayout() {
            LinearLayout mainFragment = new LinearLayout(context);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);

            mainFragment.setLayoutParams(params);
            mainFragment.setBackgroundColor(res.getColor(R.color.colorDrawYellow));
            mainFragment.setPadding(30, 0, 30, 0);

            mainFragment.addView(setTextFragment());
            mainFragment.addView(setDrawingView());

            return mainFragment;
        }

        private LinearLayout setTextFragment() {
            LinearLayout textFragment = new LinearLayout(context);
            textFragment.setOrientation(LinearLayout.VERTICAL);
            textFragment.setLayoutParams(new LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 4));

            textFragment.addView(setRankAndUsername());
            textFragment.addView(setStarsFragment());
            textFragment.addView(setTrophiesFragment());

            return textFragment;
        }

        private TextView setRankAndUsername() {
            TextView rankAndUsername = new TextView(context);
            styleView(rankAndUsername, (rank + 1) + ". " + rankedUsername[rank], USERNAME_SIZE,
                    res.getColor(R.color.colorPrimaryDark),
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            rankAndUsername.setGravity(Gravity.CENTER_VERTICAL);
            rankAndUsername.setGravity(Gravity.START);

            return rankAndUsername;
        }

        private LinearLayout setStarsFragment() {
            LinearLayout starsFragment = new LinearLayout(context);

            LayoutParams params = new LayoutParams(120, LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 0, 0, 0);

            starsFragment.setLayoutParams(params);
            starsFragment.addView(setStarsWon());
            starsFragment.addView(setStar());
            starsFragment.setPadding(0, 0, 0, 5);

            return starsFragment;
        }

        private TextView setStarsWon() {
            int dark = res.getColor(R.color.colorPrimaryDark);

            TextView starsWon = new TextView(context);
            styleView(starsWon, "+" + String.valueOf(stars), REWARD_SIZE, dark,
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
            starsWon.setGravity(Gravity.CENTER_VERTICAL);
            starsWon.setGravity(Gravity.START);

            return starsWon;
        }

        private ImageView setStar() {
            ImageView star = new ImageView(context);
            star.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            star.setImageResource(R.drawable.star);

            return star;
        }

        private LinearLayout setTrophiesFragment() {
            LinearLayout trophiesFragment = new LinearLayout(context);

            LayoutParams params = new LayoutParams(120, LayoutParams.WRAP_CONTENT);
            params.setMargins(20, 0, 0, 0);

            trophiesFragment.setLayoutParams(params);
            trophiesFragment.addView(setTrophiesWon());
            trophiesFragment.addView(setTrophy());
            trophiesFragment.setPadding(0, 0, 0, 5);

            return trophiesFragment;
        }

        private TextView setTrophiesWon() {
            int dark = res.getColor(R.color.colorPrimaryDark);

            TextView trophiesWon = new TextView(context);
            String prefix = trophies >= 0 ? "+" : "";
            styleView(trophiesWon, prefix + String.valueOf(trophies), REWARD_SIZE, dark,
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
            trophiesWon.setGravity(Gravity.CENTER_VERTICAL);
            trophiesWon.setGravity(Gravity.START);

            return trophiesWon;
        }

        private ImageView setTrophy() {
            ImageView trophy = new ImageView(context);
            trophy.setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
            trophy.setImageResource(R.drawable.trophy);

            return trophy;
        }

        private ImageView setDrawingView() {
            ImageView drawingView = new ImageView(context);
            drawingView.setLayoutParams(new LayoutParams(0,
                    LayoutParams.MATCH_PARENT, 1));
            drawingView.setImageBitmap(drawing);

            return drawingView;
        }

        private void styleView(TextView view, String text, int textSize, int color,
                               LayoutParams layoutParams) {
            view.setText(text);
            view.setTextSize(textSize);
            view.setTextColor(color);
            view.setTypeface(typeMuro);
            view.setLayoutParams(layoutParams);
        }
    }

}
