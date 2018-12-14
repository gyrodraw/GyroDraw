package ch.epfl.sweng.SDP.home.battlelog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.TypefaceLibrary;

/**
 * Class that manages the view part of GameResult.
 */
public final class GameResultLayout {

    private static final int USERNAME_SIZE = 20;
    private static final int REWARD_SIZE = 15;

    private final Context context;
    private final GameResult result;
    private final Resources res;
    private final Typeface typeMuro;

    private final LinearLayout layout;

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

    /**
     * Creates a layout from the given game result.
     *
     * @param result  the game result
     * @param context the context of the caller
     */
    GameResultLayout(GameResult result, Context context) {
        this.result = result;
        this.context = context;

        res = context.getResources();
        typeMuro = TypefaceLibrary.getTypeMuro();

        rankListParams.setMargins(0, 0, 0, 30);
        rankParams.setMargins(0, 0, 0, 10);
        rewardFragmentParams.setMargins(20, 0, 0, 0);

        layout = setLayout();
    }

    public LinearLayout getLayout() {
        return layout;
    }

    /**
     * Creates the LinearLayout corresponding to the game result
     * that will be displayed in the log battle.
     *
     * @return LinearLayout that will be displayed
     */
    private LinearLayout setLayout() {
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(rankListParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        List<String> rankedUsername = result.getRankedUsername();

        for (int i = 0; i < rankedUsername.size(); i++) {
            String prefix = (i + 1) + ". ";
            if (i == result.getRank()) {
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
        textFragment.addView(setRewardFragment(result.getStars(), R.drawable.star));
        textFragment.addView(setRewardFragment(result.getTrophies(), R.drawable.trophy));

        return textFragment;
    }

    private TextView setRankAndUsername() {
        int rank = result.getRank();
        TextView rankAndUsername = styleView((rank + 1) + ". "
                        + result.getRankedUsername().get(rank), USERNAME_SIZE,
                res.getColor(R.color.colorPrimaryDark), usernameParams);

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
        String prefix = reward >= 0 ? "+" : "";
        TextView rewardView = styleView(prefix + String.valueOf(reward),
                REWARD_SIZE, res.getColor(R.color.colorPrimaryDark), textParams);

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
        drawingView.setImageBitmap(result.getDrawing());

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
