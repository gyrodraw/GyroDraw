package ch.epfl.sweng.SDP.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

import java.util.Collections;
import java.util.LinkedList;

public class LeaderboardActivity extends Activity {

    private static final String TAG = "LeaderboardActivity";
    private static final String FIREBASE_ERROR = "There was a problem with Firebase";
    private int yellow;
    private Typeface typeMuro;
    private LinearLayout leaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_leaderboard);
        leaderboard = findViewById(R.id.leaderboard);

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        EditText searchField = findViewById(R.id.searchField);

        ((TextView) findViewById(R.id.exitButton)).setTypeface(typeMuro);
        searchField.setTypeface(typeMuro);
        setExitListener();

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
                updateLeaderboard(query.toString());
            }
        });

        updateLeaderboard("");
    }

    private void setExitListener() {
        final TextView exit = findViewById(R.id.exitButton);
        final Context context = this;
        exit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        HomeActivity.pressButton(exit, context);
                        break;
                    case MotionEvent.ACTION_UP:
                        HomeActivity.bounceButton(view, HomeActivity.MAIN_AMPLITUDE,
                                HomeActivity.MAIN_FREQUENCY, context);
                        launchActivity(HomeActivity.class);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void updateLeaderboard(String query) {
        Database.INSTANCE.getReference("users")
                .orderByChild("username").startAt(query).endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        processResponse(dataSnapshot.getChildren());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, FIREBASE_ERROR);
                    }
                });
    }

    private void processResponse(Iterable<DataSnapshot> snapshots) {
        leaderboard.removeAllViews();
        LinkedList<Player> players = new LinkedList<>();
        for (DataSnapshot s : snapshots) {
            Player temp = new Player((String) s.child("userId").getValue(),
                    (String) s.child("username").getValue(),
                    (Long) s.child("trophies").getValue());

            players.add(temp);
        }
        Collections.sort(players);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        for (int i = 0; i < Math.min(10, players.size()); ++i) {
            leaderboard.addView(players.get(i).toLayout(getApplicationContext(), i), layoutParams);
        }
    }

    private class Player implements Comparable {

        private String userId;
        private String username;
        private Long trophies;

        private Player(String userId, String username, Long trophies) {
            this.userId = userId;
            this.username = username;
            this.trophies = trophies;
        }

        @Override
        public int compareTo(Object object) {
            return -this.trophies.compareTo(((Player) object).trophies);
        }

        @SuppressLint("NewApi")
        private LinearLayout toLayout(final Context context, int index) {
            final ImageView friendsButton = new ImageView(context);
            LinearLayout.LayoutParams friendsParams =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            friendsButton.setLayoutParams(friendsParams);
            friendsButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // give Button unique Tag to test them later
            friendsButton.setTag("friendsButton" + index);

            TextView usernameView = new TextView(context);
            styleView(usernameView, username, 25,
                    getResources().getColor(R.color.colorDrawYellow),
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));

            TextView trophiesView = new TextView(context);
            styleView(trophiesView, trophies.toString(), 25,
                    getResources().getColor(R.color.colorLightGrey),
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            trophiesView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            trophiesView.setPadding(0, 0, 30, 0);

            // initializes the friendsButton on first view of leaderboard
            isFriendWithCurrentUser(context, initializeFriendsButton(friendsButton));

            // modifies friendsButton on every click
            friendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFriendWithCurrentUser(context,
                            changeFriendsButtonBackgroundOnClick(context, friendsButton));
                }
            });

            LinearLayout entry = addViews(new LinearLayout(context), usernameView, trophiesView, friendsButton);
            entry.setBackgroundColor(Color.DKGRAY);
            entry.setPadding(30, 10, 30, 10);

            return entry;
        }

        private LinearLayout addViews(LinearLayout layout, TextView usernameView,
                              TextView trophiesView, ImageView addFriends) {
            layout.addView(usernameView);
            layout.addView(trophiesView);
            layout.addView(addFriends);

            return layout;
        }

        private void styleView(TextView view, String text, float size, int color,
                                         LinearLayout.LayoutParams layoutParams) {
            view.setText(text);
            view.setTextSize(size);
            view.setTextColor(color);
            view.setTypeface(typeMuro);
            view.setLayoutParams(layoutParams);
        }

        private void isFriendWithCurrentUser(final Context context, ValueEventListener listener) {
            Database.INSTANCE.getReference("users").child(userId).child("friends")
                    .child(Account.getInstance(context).getUserId())
                    .addListenerForSingleValueEvent(listener);
        }

        private ValueEventListener initializeFriendsButton(final ImageView friendButton) {
            return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        friendButton.setBackgroundResource(R.drawable.remove_friend);
                    } else {
                        friendButton.setBackgroundResource(R.drawable.add_friend);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, FIREBASE_ERROR);
                }
            };
        }

        private ValueEventListener changeFriendsButtonBackgroundOnClick(
                final Context context, final ImageView friendButton) {
            return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Account.getInstance(context).removeFriend(userId);
                        friendButton.setBackgroundResource(R.drawable.add_friend);
                    } else {
                        Account.getInstance(context).addFriend(userId);
                        friendButton.setBackgroundResource(R.drawable.remove_friend);
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

