package ch.epfl.sweng.GyroDraw.home.leaderboard;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.LinkedList;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.AccountAttributes;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.utils.OnlineStatus;
import ch.epfl.sweng.GyroDraw.utils.TestUsers;
import ch.epfl.sweng.GyroDraw.utils.TypefaceLibrary;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.LEAGUE;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.TROPHIES;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.USERNAME;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.USER_ID;
import static ch.epfl.sweng.GyroDraw.home.FriendsRequestState.FRIENDS;
import static ch.epfl.sweng.GyroDraw.home.FriendsRequestState.fromInteger;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.getLeagueImageId;

/**
 * Helper class to manage and display user data from Firebase. Needs to be package-private.
 */
class Player implements Comparable {

    private final Context context;
    private final String userId;
    private final String username;
    private final Long trophies;
    private final String league;
    private int rank;
    private final boolean isFriend;
    private final boolean isCurrentUser;

    Player(Context context, String userId, String username, Long trophies, String league,
           boolean isFriend, boolean isCurrentUser) {
        this.context = context;
        this.userId = userId;
        this.username = username;
        this.trophies = trophies;
        this.league = league;
        this.isFriend = isFriend;
        this.isCurrentUser = isCurrentUser;
    }

    String getUserId() {
        return userId;
    }

    /**
     * Allows one to call sort on a collection of Players. Sorts collection in descending order.
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

    /**
     * Returns true if the player name contains the given string, false otherwise.
     *
     * @param query the string to search for in the player name
     * @return true if the player name contains the given string, false otherwise
     */
    boolean playerNameContainsString(String query) {
        return username.toUpperCase().contains(query.toUpperCase());
    }

    /**
     * Converts this player into a LinearLayout that will be displayed on the leaderboard.
     *
     * @param index of the player
     * @return LinearLayout that will be displayed
     */
    LinearLayout toLayout(int index) {
        TextView usernameView = new TextView(context);
        Resources res = context.getResources();
        styleView(usernameView, rank + ". " + username, res.getColor(
                isCurrentUser ? R.color.colorPrimaryDark : R.color.colorDrawYellow),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5));
        usernameView.setPadding(0, 10, 0, 10);

        TextView trophiesView = new TextView(context);
        styleView(trophiesView, trophies.toString(),
                res.getColor(R.color.colorPrimaryDark),
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        trophiesView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);

        ImageView leagueView = new ImageView(context);
        leagueView.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        leagueView.setImageResource(getLeagueImageId(league));

        final FriendsButton friendsButton =
                new FriendsButton(context, this, index, isCurrentUser);

        LinearLayout entry = addViews(new LinearLayout(context),
                new View[]{usernameView, trophiesView, leagueView, friendsButton});

        if (isFriend && !isCurrentUser) {
            createAndAddOnlineView(res, entry, usernameView);
        }

        entry.setBackgroundColor(res.getColor(
                isCurrentUser ? R.color.colorDrawYellow : R.color.colorLightGrey));
        entry.setPadding(30, 10, 30, 10);

        return entry;
    }

    private void createAndAddOnlineView(final Resources res, final LinearLayout entry, final TextView usernameView) {
        FbDatabase.getUserOnlineStatus(userId, new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer data = dataSnapshot.getValue(Integer.class);
                if (data != null) {
                    OnlineStatus onlineStatus = OnlineStatus.fromInteger(data);
                    if (onlineStatus == OnlineStatus.ONLINE) {
                        final TextView onlineView = new TextView(context);
                        styleView(onlineView, "O", res.getColor(R.color.colorGreen),
                                new LinearLayout.LayoutParams(0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        onlineView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
                        usernameView.setLayoutParams(
                                new LinearLayout.LayoutParams(0,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 4));
                        onlineView.setPadding(0, 10, 0, 10);
                        entry.addView(onlineView, 1);
                    }
                }
            }
        });
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

    /**
     * Checks if the received player is not a test-user and if all values are available. Then adds
     * the player to allPlayers.
     *
     * @param snapshot to convert
     */
    static void convertSnapshotToPlayerAndAddToList(Context context, DataSnapshot snapshot,
                                                    LinkedList<Player> players) {
        String userId = snapshot.child(USER_ID).getValue(String.class);
        String username = snapshot.child(USERNAME).getValue(String.class);
        Long trophies = snapshot.child(TROPHIES).getValue(Long.class);
        String league = snapshot.child(LEAGUE).getValue(String.class);
        if (!TestUsers.isTestUser(snapshot.getKey())
                && userId != null
                && username != null
                && trophies != null
                && league != null) {
            boolean isFriend = false;
            for (DataSnapshot friend : snapshot.child(AccountAttributes.FRIENDS).getChildren()) {
                if (friend.getKey().equals(Account.getInstance(context).getUserId())
                        && fromInteger(friend.getValue(int.class)) == FRIENDS) {
                    isFriend = true;
                }
            }
            Player temp = new Player(context, userId, username, trophies, league, isFriend,
                    username.equals(
                            Account.getInstance(context)
                                    .getUsername()));

            players.add(temp);
        }
    }
}
