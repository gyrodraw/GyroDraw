package ch.epfl.sweng.SDP.home.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.LinearLayout;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.utils.TestUsers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import static ch.epfl.sweng.SDP.firebase.Database.getAllFriends;
import static ch.epfl.sweng.SDP.firebase.Database.getUserById;
import static ch.epfl.sweng.SDP.firebase.Database.getUsers;

/**
 * Helper class to manage and display data from Firebase.
 */
class Leaderboard {

    private static final String TAG = "Leaderboard";
    private static final String FIREBASE_ERROR = "There was a problem with Firebase";
    private static final int FRIENDS = FriendsRequestState.FRIENDS.ordinal();
    private static final String USERS_TAG = "users";
    private static final String USERNAME_TAG = "username";
    private static final String USERID_TAG = "userId";
    private static final String TROPHIES_TAG = "trophies";
    private static final String FRIENDS_TAG = "friends";
    private static final String LEAGUE_TAG = "currentLeague";

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
     * @param players   pool of players to filter
     * @param query     string to search
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
     * Gets all the players from Firebase and stores them in LinkedList.
     */
    private void fetchPlayersFromFirebase() {
        allPlayers.clear();
        getUsers(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allPlayers.clear();
                        wantedPlayers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            convertSnapshotToPlayerAndAddToList(snapshot, allPlayers);
                        }
                        update("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, FIREBASE_ERROR + databaseError.toString());
                    }
                });
    }

    /**
     * Gets all friends of current user and stores them in LinkedList.
     */
    private void fetchFriendsFromFirebase() {
        allFriends.clear();
        getAllFriends(Account.getInstance(context).getUserId(), new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        allPlayers.clear();
                        wantedPlayers.clear();
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            if (s != null && !TestUsers.isTestUser(s.getKey())
                                    && s.getValue(int.class) == FRIENDS) {
                                findAndAddPlayer(s.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, FIREBASE_ERROR + databaseError.toString());
                    }
                });
    }

    /**
     * Searches a player in Firebase using id and converts the response into a Player.
     * Then the new player is added to the pool of friends.
     *
     * @param playerId  id of friend to search
     */
    private void findAndAddPlayer(final String playerId) {
        getUserById(playerId, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            convertSnapshotToPlayerAndAddToList(dataSnapshot, allFriends);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, FIREBASE_ERROR + databaseError.toString());
                    }
                });
    }

    /**
     * Checks if the received player is not the test-user and if all values are available.
     * Then adds the player to allPlayers.
     *
     * @param snapshot  to convert
     */
    private void convertSnapshotToPlayerAndAddToList(DataSnapshot snapshot,
                                                     LinkedList<Player> players) {
        String userId = snapshot.child(USERID_TAG).getValue(String.class);
        String username = snapshot.child(USERNAME_TAG).getValue(String.class);
        Long trophies = snapshot.child(TROPHIES_TAG).getValue(Long.class);
        String league = snapshot.child(LEAGUE_TAG).getValue(String.class);
        if (!TestUsers.isTestUser(snapshot.getKey())
                && userId != null
                && username != null
                && trophies != null
                && league != null) {
            Player temp = new Player(context, userId, username, trophies, league,
                    username.equals(
                            Account.getInstance(context)
                                    .getUsername()));

            players.add(temp);
        }
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
        Iterator<Player> playerIterator = wantedPlayers.iterator();
        while (playerIterator.hasNext()) {
            Player currentPlayer = playerIterator.next();
            currentPlayer.setRank(index + 1);
            leaderboardView.addView(currentPlayer
                    .toLayout(index), layoutParams);
            ++index;
        }
    }
}
