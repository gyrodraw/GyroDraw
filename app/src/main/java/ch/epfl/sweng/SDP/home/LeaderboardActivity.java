package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

import static ch.epfl.sweng.SDP.utils.LayoutUtils.AnimMode.getLeagueImageId;
import static java.lang.String.format;

public class LeaderboardActivity extends Activity {

    private static final String TAG = "LeaderboardActivity";
    private static final String FIREBASE_ERROR = "There was a problem with Firebase";
    private Typeface typeMuro;
    private LinearLayout leaderboardView;
    private Leaderboard leaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_leaderboard);
        leaderboardView = findViewById(R.id.leaderboard);

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        EditText searchField = findViewById(R.id.searchField);

        TextView exitButton = findViewById(R.id.exitButton);
        LayoutUtils.setExitListener(exitButton, this);
        exitButton.setTypeface(typeMuro);
        searchField.setTypeface(typeMuro);

        leaderboard = new Leaderboard(this);

        searchField.addTextChangedListener(new TextWatcher() {
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
        });
    }

    /**
     * Helper classes to manage and display data from Firebase.
     */
    private class Player implements Comparable {

        private final String userId;
        private final String username;
        private final Long trophies;
        private final String league;
        private int rank;
        private final boolean isCurrentUser;

        private Player(String userId, String username, Long trophies, String league,
                       boolean isCurrentUser) {
            this.userId = userId;
            this.username = username;
            this.trophies = trophies;
            this.league = league;
            this.isCurrentUser = isCurrentUser;
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
            return -this.trophies.compareTo(((Player) object).trophies);
        }

        public boolean playerNameContainsString(String query) {
            return username.toUpperCase().contains(query.toUpperCase());
        }

        /**
         * Converts this player into a LinearLayout that will be displayed in the leaderboard.
         *
         * @param context of the app
         * @param index   of the player
         * @return LinearLayout that will be displayed
         */
        @SuppressLint("NewApi")
        private LinearLayout toLayout(final Context context, int index) {
            TextView usernameView = new TextView(context);
            Resources res = getResources();
            styleView(usernameView, rank + ". " + username, res.getColor(
                    isCurrentUser ? R.color.colorPrimaryDark : R.color.colorDrawYellow),
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));

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
            leagueView.setForegroundGravity(Gravity.CENTER);

            final FriendsButton friendsButton =
                    new FriendsButton(context, this, index, isCurrentUser);

            LinearLayout entry = addViews(new LinearLayout(context),
                    usernameView, trophiesView, leagueView, friendsButton);

            entry.setBackgroundColor(res.getColor(
                    isCurrentUser ? R.color.colorDrawYellow : R.color.colorLightGrey));
            entry.setPadding(30, 10, 30, 10);

            return entry;
        }

        private LinearLayout addViews(LinearLayout layout, TextView usernameView,
                                      TextView trophiesView, ImageView leagueView,
                                      ImageView addFriends) {
            layout.addView(usernameView);
            layout.addView(trophiesView);
            layout.addView(leagueView);
            layout.addView(addFriends);

            return layout;
        }

        private void styleView(TextView view, String text, int color,
                               LinearLayout.LayoutParams layoutParams) {
            view.setText(text);
            view.setTextSize(20);
            view.setMaxLines(1);
            view.setTextColor(color);
            view.setTypeface(typeMuro);
            view.setLayoutParams(layoutParams);
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
    }

    private class Leaderboard {

        private LinkedList<Player> allPlayers;
        private LinkedList<Player> wantedPlayers;
        private Context context;

        private Leaderboard(Context context) {
            this.context = context;
            allPlayers = new LinkedList<>();
            wantedPlayers = new LinkedList<>();
            update("");
        }

        /**
         * Gets called when user entered a new search query. Processes inquiry locally if cache
         * (allPlayers) is not empty. Else fetches data from Firebase and stores it in allPlayers.
         *
         * @param query new string to search
         */
        private void update(String query) {
            if (!allPlayers.isEmpty()) {
                filterWantedPlayers(query);
                Collections.sort(wantedPlayers);
                leaderboardView.removeAllViews();
                addWantedPlayersToLayout();
            } else {
                fetchPlayersFromFirebase(query);
            }
        }

        /**
         * Copies all players that contain query into wantedPlayers.
         *
         * @param query string to search
         */
        private void filterWantedPlayers(String query) {
            wantedPlayers.clear();
            for (Player tempPlayer : allPlayers) {
                if (tempPlayer.playerNameContainsString(query)) {
                    wantedPlayers.add(tempPlayer);
                }
            }
        }

        /**
         * Gets called when local cache of players is empty. Adds snapshots to a list, filters it by
         * query, sorts it by trophies and adds players to LinearLayout
         *
         * @param query string to search
         */
        private void fetchPlayersFromFirebase(final String query) {
            Database.getReference("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                if (s.child("userId") == null || s.child("username") == null
                                        || s.child("trophies") == null
                                        || s.child("currentLeague") == null
                                        || s.getKey().equals("123456789")) {
                                    continue;
                                }
                                String username = (String) s.child("username").getValue();
                                Player temp = new Player((String) s.child("userId").getValue(),
                                        username, (Long) s.child("trophies").getValue(),
                                        (String) s.child("currentLeague").getValue(),
                                        username.equals(
                                                Account.getInstance(context).getUsername()));

                                allPlayers.add(temp);
                            }
                            filterWantedPlayers(query);
                            Collections.sort(wantedPlayers);
                            leaderboardView.removeAllViews();
                            addWantedPlayersToLayout();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, FIREBASE_ERROR);
                        }
                    });
        }

        /**
         * Adds wantedPlayers to the leaderboardView.
         */
        private void addWantedPlayersToLayout() {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 20, 0, 0);

            // add all (max 20) players to the leaderboard
            for (int i = 0; i < Math.min(20, wantedPlayers.size()); ++i) {
                Player currentPlayer = wantedPlayers.get(i);
                currentPlayer.setRank(i + 1);
                leaderboardView.addView(currentPlayer
                        .toLayout(getApplicationContext(), i), layoutParams);
            }
        }
    }

    private class FriendsButton extends android.support.v7.widget.AppCompatImageView {

        private final Context context;
        private final Player player;
        private final int index;
        private final boolean isCurrentUser;

        public FriendsButton(Context context, Player player, int index, boolean isCurrentUser) {
            super(context);
            this.context = context;
            this.player = player;
            this.index = index;
            this.isCurrentUser = isCurrentUser;
            this.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFriendWithCurrentUser(
                            changeFriendsButtonBackgroundOnClick());
                }
            });
            this.isFriendWithCurrentUser(initializeFriendsButton());
            initLayout();
        }

        private void initLayout() {
            LinearLayout.LayoutParams friendsParams =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            setLayoutParams(friendsParams);

            // give Button unique Tag to test them later
            setTag("friendsButton" + index);

            // set friendsButton invisible to yourself
            if (isCurrentUser) {
                setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Gets data if users are friends, else null. Then applies listener.
         *
         * @param listener how to handle response
         */
        private void isFriendWithCurrentUser(ValueEventListener listener) {
            Database.constructBuilder().addChildren(
                    format("users.%s.friends.%s", player.userId,
                            Account.getInstance(context).getUserId())).build()
                    .addListenerForSingleValueEvent(listener);
        }

        /**
         * Check if users are already friends and set background accordingly.
         *
         * @return listener
         */
        private ValueEventListener initializeFriendsButton() {
            return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        setImageResource(R.drawable.remove_friend);
                    } else {
                        setImageResource(R.drawable.add_friend);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, FIREBASE_ERROR);
                }
            };
        }

        /**
         * Friends button got clicked, now add/remove friend and modify background.
         *
         * @return listener
         */
        private ValueEventListener changeFriendsButtonBackgroundOnClick() {
            return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Account.getInstance(context).removeFriend(player.userId);
                        setImageResource(R.drawable.add_friend);
                    } else {
                        Account.getInstance(context).addFriend(player.userId);
                        setImageResource(R.drawable.remove_friend);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, FIREBASE_ERROR);
                }
            };
        }
    }
}

