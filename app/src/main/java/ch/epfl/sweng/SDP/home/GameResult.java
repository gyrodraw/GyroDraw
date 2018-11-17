package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import ch.epfl.sweng.SDP.R;

public class GameResult {

    private static final int USERNAME_SIZE = 20;
    private static final int REWARD_SIZE = 15;

    private final List<String> rankedUsername;
    private final int rank;
    private final Bitmap drawing;
    private final int stars;
    private final int trophies;

    private Context context;
    private Resources res;
    private Typeface typeMuro;

    public GameResult(List<String> rankedUsername, int rank, int stars,
                      int trophies, Bitmap drawing, Context context) {
        assert 0 <= rank && rank < 5;
        assert rankedUsername.size() <= 5;
        this.rankedUsername = rankedUsername;
        this.rank = rank;
        this.drawing = drawing;
        this.stars = stars;
        this.trophies = trophies;
        this.context = context;

        res = context.getResources();
        typeMuro = Typeface.createFromAsset(context.getAssets(), "fonts/Muro.otf");
    }

    public static byte[] toByteArray(GameResult gameResult) {
        byte[] byteArrayObject = null;

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(gameResult);

            objectStream.close();
            byteStream.close();
            byteArrayObject = byteStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArrayObject;
    }

    public static GameResult fromByteArray(byte[] byteArray) {
        GameResult gameResult = null;

        ByteArrayInputStream byteStream;
        ObjectInputStream objectStream;
        try {
            byteStream = new ByteArrayInputStream(byteArray);
            objectStream = new ObjectInputStream(byteStream);
            gameResult = (GameResult) objectStream.readObject();

            objectStream.close();
            byteStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gameResult;
    }

    /**
     * Converts this game result into a LinearLayout
     * that will be displayed in the log battle.
     *
     * @return LinearLayout that will be displayed
     */
    @SuppressLint("NewApi")
    public LinearLayout toLayout() {
        LinearLayout layout = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 30);

        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < rankedUsername.size(); i++) {
            String prefix = (i + 1) + ". ";
            if (i == rank) {
                layout.addView(userLayout());
            } else {
                layout.addView(rankLayout(prefix + rankedUsername.get(i)));
            }
        }

        return layout;
    }

    private LinearLayout rankLayout(String username) {
        TextView rankAndUsername = new TextView(context);

        styleView(rankAndUsername, username, USERNAME_SIZE,
                res.getColor(R.color.colorDrawYellow),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
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
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
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
        textFragment.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 4));

        textFragment.addView(setRankAndUsername());
        textFragment.addView(setStarsFragment());
        textFragment.addView(setTrophiesFragment());

        return textFragment;
    }

    private TextView setRankAndUsername() {
        TextView rankAndUsername = new TextView(context);
        styleView(rankAndUsername, (rank + 1) + ". " + rankedUsername.get(rank), USERNAME_SIZE,
                res.getColor(R.color.colorPrimaryDark),
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        rankAndUsername.setGravity(Gravity.CENTER_VERTICAL);
        rankAndUsername.setGravity(Gravity.START);

        return rankAndUsername;
    }

    private LinearLayout setStarsFragment() {
        LinearLayout starsFragment = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        starsWon.setGravity(Gravity.CENTER_VERTICAL);
        starsWon.setGravity(Gravity.START);

        return starsWon;
    }

    private ImageView setStar() {
        ImageView star = new ImageView(context);
        star.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        star.setImageResource(R.drawable.star);

        return star;
    }

    private LinearLayout setTrophiesFragment() {
        LinearLayout trophiesFragment = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        trophiesWon.setGravity(Gravity.CENTER_VERTICAL);
        trophiesWon.setGravity(Gravity.START);

        return trophiesWon;
    }

    private ImageView setTrophy() {
        ImageView trophy = new ImageView(context);
        trophy.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
        trophy.setImageResource(R.drawable.trophy);

        return trophy;
    }

    private ImageView setDrawingView() {
        ImageView drawingView = new ImageView(context);
        drawingView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        drawingView.setImageBitmap(drawing);

        return drawingView;
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