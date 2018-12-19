package ch.epfl.sweng.SDP.home.leaderboard;

import static ch.epfl.sweng.SDP.firebase.FbDatabase.getAllFriends;
import static ch.epfl.sweng.SDP.firebase.FbDatabase.getUserById;
import static ch.epfl.sweng.SDP.firebase.FbDatabase.getUsers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.utils.TestUsers;
import com.google.firebase.database.DataSnapshot;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Helper class to manage and display data from Firebase.
 */
final class Leaderboard {

    private static final int FRIENDS = FriendsRequestState.FRIENDS.ordinal();

    private LinkedList<Player> allPlayers;
    private LinkedList<Player> allFriends;
    private TreeSet<Player> wantedPlayers;
    private Context context;
    private LinearLayout leaderboardView;
    private boolean filterByFriends;

    Leaderboard(Context context, LinearLayout leaderboardView) {
        this.context = context;
        this.leaderboardView = leaderboardView;
        allPlayers = new LinkedList<>();
        allFriends = new LinkedList<>();
        wantedPlayers = new TreeSet<>();
        filterByFriends = false;

        initLeaderboard();
    }

    void xorFilterByFriends() {
        filterByFriends ^= true;
    }

    boolean getFilterByFriends() {
        return filterByFriends;
    }

    void initLeaderboard() {
        fetchPlayersFromFirebase();
        fetchFriendsFromFirebase();
    }

    /**
     * Gets called when user entered a new search query.
     * Clears the leaderboard and adds the players containing the query in their name.
     *
     * @param query new string to search
     */
    void update(String query) {
        query = query.toUpperCase();
        leaderboardView.removeAllViews();

        if (filterByFriends) {
            filterWantedPlayers(allFriends, query);
        } else {
            filterWantedPlayers(allPlayers, query);
        }
        addWantedPlayersToLayout();
    }

    /**
     * Copies all players that contain query into wantedPlayers.
     *
     * @param players pool of players to filter
     * @param query   string to search
     */
    private void filterWantedPlayers(LinkedList<Player> players, String query) {
        wantedPlayers.clear();
        for (Player tempPlayer : players) {
            if (tempPlayer.playerNameContainsString(query)) {
                wantedPlayers.add(tempPlayer);
            }
        }
    }

    /**
     * Gets all the players from Firebase and stores them in the linked list.
     */
    private void fetchPlayersFromFirebase() {
        allPlayers.clear();
        getUsers(new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allPlayers.clear();
                wantedPlayers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Player.convertSnapshotToPlayerAndAddToList(context, snapshot, allPlayers);
                }
                update("");
            }
        });
    }

    /**
     * Gets all friends of current user and stores them in the linked list.
     */
    private void fetchFriendsFromFirebase() {
        allFriends.clear();
        final String userId = Account.getInstance(context).getUserId();
        getAllFriends(userId, new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allPlayers.clear();
                wantedPlayers.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    if (s != null && !TestUsers.isTestUser(s.getKey())
                            && (s.getValue(int.class) == FRIENDS)) {
                        findAndAddPlayer(s.getKey());
                    }
                }
                findAndAddPlayer(userId);
            }
        });
    }

    /**
     * Searches a player in Firebase using id and converts the response into a Player.
     * Then the new player is added to the pool of friends.
     *
     * @param playerId id of friend to search
     */
    private void findAndAddPlayer(final String playerId) {
        getUserById(playerId, new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Player.convertSnapshotToPlayerAndAddToList(context, dataSnapshot, allFriends);
                }
            }
        });
    }

    /**
     * Adds wantedPlayers to the leaderboardView.
     */
    private void addWantedPlayersToLayout() {
        leaderboardView.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);

        // add all (max MAX_PLAYERS_DISPLAYED) players to the leaderboard
        int index = 0;
        for (Player currentPlayer : wantedPlayers) {
            currentPlayer.setRank(index + 1);
            leaderboardView.addView(currentPlayer
                    .toLayout(index), layoutParams);
            ++index;
        }
    }
}
