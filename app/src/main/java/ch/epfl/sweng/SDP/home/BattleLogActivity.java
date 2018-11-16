package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

import static ch.epfl.sweng.SDP.utils.AnimUtils.bounceButton;
import static ch.epfl.sweng.SDP.utils.AnimUtils.pressButton;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;

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
         * @param context   of the app
         * @return          LinearLayout that will be displayed
         */
        @SuppressLint("NewApi")
        private LinearLayout toLayout(final Context context) {
            LinearLayout layout = new LinearLayout(context);
            for (int i = 0; i < rankedUsername.length; i++) {
                String prefix = (i + 1) + ". ";
                if (i == rank) {
                    layout.addView(userLayout(context, prefix + rankedUsername[i]));
                } else {
                    layout.addView(rankLayout(context, prefix + rankedUsername[i]));
                }
            }

            layout.setPadding(0, 0, 0, 18);
            return layout;
        }

        private LinearLayout rankLayout(final Context context, String username) {
            TextView rankAndUsername = new TextView(context);
            Resources res = getResources();
            styleView(rankAndUsername, username, 15, res.getColor(R.color.colorDrawYellow),
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            LinearLayout fragment = new LinearLayout(context);
            fragment.addView(rankAndUsername);
            fragment.setBackgroundColor(res.getColor(R.color.colorLightGrey));
            fragment.setPadding(30, 2, 30, 2);

            return fragment;
        }

        private LinearLayout userLayout(final Context context, String username) {
            Resources res = getResources();

            LinearLayout mainFragment = new LinearLayout(context);
            mainFragment.setBackgroundColor(res.getColor(R.color.colorDrawYellowDark));
            mainFragment.setPadding(30, 2, 30, 2);

            LinearLayout textFragment = new LinearLayout(context);
            textFragment.setOrientation(LinearLayout.VERTICAL);
            textFragment.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 8));

            ImageView drawingView = new ImageView(context);
            drawingView.setImageBitmap(drawing);
            drawingView.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView rankAndUsername = new TextView(context);
            styleView(rankAndUsername, (rank + 1) + username, 15, res.getColor(R.color.colorDrawYellow),
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,0, 1));

            LinearLayout starsFragment = new LinearLayout(context);
            textFragment.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,0, 1));

            LinearLayout trophiesFragment = new LinearLayout(context);
            textFragment.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,0, 1));

            int dark = res.getColor(R.color.colorPrimaryDark);

            TextView trophiesWon = new TextView(context);
            String prefix = trophies >= 0 ? "+ " : "";
            styleView(trophiesWon, prefix + String.valueOf(trophies), 10, dark,
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            TextView starsWon = new TextView(context);
            styleView(starsWon, "+ " + String.valueOf(stars), 10, dark,
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            starsFragment.addView(starsWon);
            trophiesFragment.addView(trophiesWon);

            textFragment.addView(rankAndUsername);
            textFragment.addView(starsFragment);
            textFragment.addView(trophiesFragment);

            mainFragment.addView(textFragment);
            mainFragment.addView(drawingView);

            return mainFragment;
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

