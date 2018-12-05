package ch.epfl.sweng.SDP.home.leaderboard;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * Class representing the leaderboard.
 */
public class LeaderboardActivity extends BaseActivity {

    private static final String TAG = "LeaderboardActivity";
    private static final String FIREBASE_ERROR = "There was a problem with Firebase";
    private static final int MAX_PLAYERS_DISPLAYED = 10;
    private static final int SENT = FriendsRequestState.SENT.ordinal();
    private static final int FRIENDS = FriendsRequestState.FRIENDS.ordinal();
    private static final String USERS_TAG = "users";
    private static final String USERNAME_TAG = "username";
    private static final String USERID_TAG = "userId";
    private static final String TROPHIES_TAG = "trophies";
    private static final String FRIENDS_TAG = "friends";
    private static final String LEAGUE_TAG = "currentLeague";

    private Typeface typeMuro;
    private LinearLayout leaderboardView;
    private Leaderboard leaderboard;
    private Boolean filterByFriends;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_leaderboard);

        filterByFriends = false;
        leaderboardView = findViewById(R.id.leaderboard);

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        final EditText searchField = findViewById(R.id.searchField);
        TextView exitButton = findViewById(R.id.exitButton);
        LayoutUtils.setFadingExitListener(exitButton, this);
        exitButton.setTypeface(typeMuro);
        searchField.setTypeface(typeMuro);

        leaderboard = new Leaderboard(getApplicationContext());
        setCheckBoxListener(searchField);

        searchField.addTextChangedListener(getTextWatcher());
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence query, int start, int count, int after) {
                // Not what we need.
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                // Not what we need.
            }

            @Override
            public void afterTextChanged(Editable query) {
                leaderboard.update(query.toString());
            }
        };
    }

    private void setCheckBoxListener(final EditText searchField) {
        final CheckBox friendsFilterCheckbox = findViewById(R.id.friendsFilterCheckBox);
        final TextView friendsFilterText = findViewById(R.id.friendsFilterText);
        friendsFilterText.setTypeface(typeMuro);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 500) {
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                filterByFriends ^= true;
                if (filterByFriends) {
                    leaderboard.update(searchField.getText().toString());
                    friendsFilterCheckbox.setChecked(true);
                } else {
                    leaderboard.update(searchField.getText().toString());
                    friendsFilterCheckbox.setChecked(false);
                }
            }
        };

        friendsFilterCheckbox.setOnClickListener(clickListener);
        friendsFilterText.setOnClickListener(clickListener);
    }

    /**
     * Helper class to manage and display data from Firebase.
     */
    private class Leaderboard {

        private LinkedList<Player> allPlayers;
        private LinkedList<Player> allFriends;
        private TreeSet<Player> wantedPlayers;
        private Context context;

        private Leaderboard(Context context) {
            this.context = context;
            allPlayers = new LinkedList<>();
            allFriends = new LinkedList<>();
            wantedPlayers = new TreeSet<>();
            fetchPlayersFromFirebase();
            fetchFriendsFromFirebase();
        }

        /**
         * Gets called when user entered a new search query.
         * Clears the leaderboard and adds the players containing the query in their name.
         *
         * @param query new string to search
         */
        private void update(String query) {
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
                if (wantedPlayers.size() >= MAX_PLAYERS_DISPLAYED) {
                    return;
                }
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
            Database.getReference(USERS_TAG)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
            Database.getReference(USERS_TAG + "."
                    + Account.getInstance(getApplicationContext()).getUserId() + "."
                    + FRIENDS_TAG)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            allPlayers.clear();
                            wantedPlayers.clear();
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                if (s != null && !s.getKey().equals("123456789")
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
            Database.getReference(USERS_TAG + "." + playerId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
            if (!snapshot.getKey().equals("123456789")
                    && userId != null
                    && username != null
                    && trophies != null
                    && league != null) {
                Player temp = new Player(userId, username, trophies, league,
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
            while (playerIterator.hasNext() && index < MAX_PLAYERS_DISPLAYED) {
                Player currentPlayer = playerIterator.next();
                currentPlayer.setRank(index + 1);
                leaderboardView.addView(currentPlayer
                        .toLayout(getApplicationContext(), index), layoutParams);
                ++index;
            }
        }
    }
}
