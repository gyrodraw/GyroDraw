package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.TypefaceLibrary;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;
import static ch.epfl.sweng.SDP.utils.RankingUtils.addSignToNumber;

/**
 * Class representing a game result.
 */
public class GameResult {

    private static final LayoutParams rankListParams =
            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private static final LayoutParams rankParams =
            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private static final LayoutParams textFragmentParams =
            new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4);
    private static final LayoutParams usernameParams =
            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    private static final LayoutParams rewardFragmentParams =
            new LayoutParams(120, LayoutParams.WRAP_CONTENT);
    private static final LayoutParams textParams =
            new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
    private static final LayoutParams imagesParams =
            new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);

    private static final int USERNAME_SIZE = 20;
    private static final int REWARD_SIZE = 15;

    private final List<String> rankedUsername;
    private final int rank;
    private final int stars;
    private final int trophies;
    private final Bitmap drawing;

    private Context context;
    private Resources res;
    private Typeface typeMuro;

    /**
     * Creates a new game result.
     *
     * @param rankedUsername the list of usernames in ranking order
     * @param rank           the rank of the user
     * @param stars          the stars won during this game
     * @param trophies       the trophies won during this game
     * @param drawing        the drawing of the user
     * @param context        the context of the caller
     */
    public GameResult(List<String> rankedUsername, int rank, int stars,
                      int trophies, Bitmap drawing, Context context) {
        checkPrecondition(0 <= rank && rank < rankedUsername.size(),
                "Rank is out of bounds");
        checkPrecondition(rankedUsername.size() <= 5,
                "The number of username is bigger than 5");
        this.rankedUsername = rankedUsername;
        this.rank = rank;
        this.drawing = drawing;
        this.stars = stars;
        this.trophies = trophies;
        this.context = context;

        res = context.getResources();
        typeMuro = TypefaceLibrary.getTypeMuro();

        rankListParams.setMargins(0, 0, 0, 30);
        rankParams.setMargins(0, 0, 0, 10);
        rewardFragmentParams.setMargins(20, 0, 0, 0);
    }

    public List<String> getRankedUsername() {
        return new ArrayList<>(rankedUsername);
    }

    public int getRank() {
        return rank;
    }

    public int getStars() {
        return stars;
    }

    public int getTrophies() {
        return trophies;
    }

    public Bitmap getDrawing() {
        return drawing == null ? null : drawing.copy(Bitmap.Config.ARGB_8888, false);
    }

    /**
     * Converts this game result into a LinearLayout
     * that will be displayed in the log battle.
     *
     * @return LinearLayout that will be displayed
     */
    @SuppressLint("NewApi")
    LinearLayout toLayout() {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(rankListParams);
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
        TextView rankAndUsername = styleView(username, USERNAME_SIZE,
                res.getColor(R.color.colorDrawYellow), textParams);

        LinearLayout fragment = new LinearLayout(context);

        fragment.setLayoutParams(rankParams);
        fragment.setBackgroundColor(res.getColor(R.color.colorLightGrey));
        fragment.setPadding(30, 0, 30, 0);

        fragment.addView(rankAndUsername);

        return fragment;
    }

    private LinearLayout userLayout() {
        LinearLayout mainFragment = new LinearLayout(context);

        mainFragment.setLayoutParams(rankParams);
        mainFragment.setBackgroundColor(res.getColor(R.color.colorDrawYellow));
        mainFragment.setPadding(30, 0, 30, 0);

        mainFragment.addView(setTextFragment());
        mainFragment.addView(setDrawingView());

        return mainFragment;
    }

    private LinearLayout setTextFragment() {
        LinearLayout textFragment = new LinearLayout(context);

        textFragment.setOrientation(LinearLayout.VERTICAL);
        textFragment.setLayoutParams(textFragmentParams);

        textFragment.addView(setRankAndUsername());
        textFragment.addView(setRewardFragment(stars, R.drawable.star));
        textFragment.addView(setRewardFragment(trophies, R.drawable.trophy));

        return textFragment;
    }

    private TextView setRankAndUsername() {
        TextView rankAndUsername = styleView((rank + 1) + ". " + rankedUsername.get(rank),
                USERNAME_SIZE, res.getColor(R.color.colorPrimaryDark), usernameParams);

        rankAndUsername.setGravity(Gravity.CENTER_VERTICAL);
        rankAndUsername.setGravity(Gravity.START);

        return rankAndUsername;
    }

    private LinearLayout setRewardFragment(int reward, int drawableId) {
        LinearLayout rewardFragment = new LinearLayout(context);

        rewardFragment.setLayoutParams(rewardFragmentParams);
        rewardFragment.addView(setReward(reward));
        rewardFragment.addView(setDrawable(drawableId));
        rewardFragment.setPadding(0, 0, 0, 5);

        return rewardFragment;
    }

    private TextView setReward(int reward) {
        TextView rewardView = styleView(addSignToNumber(reward), REWARD_SIZE,
                res.getColor(R.color.colorPrimaryDark), textParams);

        rewardView.setGravity(Gravity.CENTER_VERTICAL);
        rewardView.setGravity(Gravity.START);

        return rewardView;
    }

    private ImageView setDrawable(int id) {
        ImageView drawable = new ImageView(context);

        drawable.setLayoutParams(imagesParams);
        drawable.setImageResource(id);

        return drawable;
    }

    private ImageView setDrawingView() {
        ImageView drawingView = new ImageView(context);

        drawingView.setLayoutParams(imagesParams);
        drawingView.setImageBitmap(drawing);

        return drawingView;
    }

    private TextView styleView(String text, int textSize, int color, LayoutParams layoutParams) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(textSize);
        view.setTextColor(color);
        view.setTypeface(typeMuro);
        view.setLayoutParams(layoutParams);

        return view;
    }
}