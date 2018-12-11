package ch.epfl.sweng.SDP.home.leaderboard;

import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueImageId;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.TypefaceLibrary;

/**
 * Helper class to manage and display user data from Firebase.
 * Needs to be package-private.
 */
class Player implements Comparable {

    private final Context context;
    private final String userId;
    private final String username;
    private final Long trophies;
    private final String league;
    private int rank;
    private final boolean isCurrentUser;

    Player(Context context, String userId, String username, Long trophies, String league,
                   boolean isCurrentUser) {
        this.context = context;
        this.userId = userId;
        this.username = username;
        this.trophies = trophies;
        this.league = league;
        this.isCurrentUser = isCurrentUser;
    }

    String getUserId() {
        return userId;
    }

    /**
     * Allows one to call sort on a collection of Players. Sorts collection in descending
     * order.
     *
     * @param object Player to compare
     * @return 1 if this is larger, -1 if this is smaller, 0 else
     */
    @Override
    public int compareTo(Object object) {
        int compareTrophies = -this.trophies.compareTo(((Player) object).trophies);
        if (compareTrophies == 0) {
            return this.username.compareTo(((Player) object).username);
        }
        return compareTrophies;
    }

    boolean playerNameContainsString(String query) {
        return username.toUpperCase().contains(query.toUpperCase());
    }

    /**
     * Converts this player into a LinearLayout that will be displayed on the leaderboard.
     *
     * @param index   of the player
     * @return LinearLayout that will be displayed
     */
    LinearLayout toLayout(int index) {
        TextView usernameView = new TextView(context);
        Resources res = context.getResources();
        styleView(usernameView, rank + ". " + username, res.getColor(
                isCurrentUser ? R.color.colorPrimaryDark : R.color.colorDrawYellow),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
        usernameView.setPadding(0, 10, 0, 10);

        TextView trophiesView = new TextView(context);
        styleView(trophiesView, trophies.toString(),
                res.getColor(R.color.colorPrimaryDark),
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        trophiesView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);

        ImageView leagueView = new ImageView(context);
        leagueView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        leagueView.setImageResource(getLeagueImageId(league));

        final FriendsButton friendsButton =
                new FriendsButton(context, this, index, isCurrentUser);

        LinearLayout entry = addViews(new LinearLayout(context),
                new View[]{usernameView, trophiesView, leagueView, friendsButton});

        entry.setBackgroundColor(res.getColor(
                isCurrentUser ? R.color.colorDrawYellow : R.color.colorLightGrey));
        entry.setPadding(30, 10, 30, 10);

        return entry;
    }

    private LinearLayout addViews(LinearLayout layout, View[] views) {
        for (View view : views) {
            layout.addView(view);
        }
        return layout;
    }

    private void styleView(TextView view, String text, int color,
                           LinearLayout.LayoutParams layoutParams) {
        view.setText(text);
        view.setTextSize(20);
        view.setMaxLines(1);
        view.setTextColor(color);
        view.setTypeface(TypefaceLibrary.getTypeMuro());
        view.setLayoutParams(layoutParams);
    }

    void setRank(int rank) {
        this.rank = rank;
    }
}
