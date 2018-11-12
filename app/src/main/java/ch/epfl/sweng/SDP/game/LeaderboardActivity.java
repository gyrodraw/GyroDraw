package ch.epfl.sweng.SDP.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;

public class LeaderboardActivity extends Activity {

    LinearLayout leaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);
        leaderboard = findViewById(R.id.leaderboard);

        EditText searchField = findViewById(R.id.searchField);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not what we need.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not what we need.
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateLeaderboard(s.toString());
            }
        });

        updateLeaderboard("");
    }

    private void updateLeaderboard(String s) {
        Database.INSTANCE.getReference("users")
                .orderByChild("username").startAt(s).endAt(s+"\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        processResponse(dataSnapshot.getChildren());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void processResponse(Iterable<DataSnapshot> snapshots) {
        leaderboard.removeAllViews();
        LinkedList<Player> players = new LinkedList<>();
        for (DataSnapshot s : snapshots) {
            Player temp = new Player((String)s.child("userId").getValue(),
                    (String)s.child("username").getValue(),
                    (Long)s.child("trophies").getValue());

            players.add(temp);
        }
        Collections.sort(players);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        for(int i = 0; i < Math.min(10, players.size()); ++i) {
            leaderboard.addView(players.get(i).toLayout(getApplicationContext()), layoutParams);
        }
    }

    private class Player implements Comparable {

        private String userId;
        private String username;
        private Long trophies;

        protected Player(String userId, String username, Long trophies) {
            this.userId = userId;
            this.username = username;
            this.trophies = trophies;
        }

        @Override
        public int compareTo(Object o) {
            if (o.getClass() != this.getClass()) {
                throw new IllegalArgumentException("not same class");
            }
            return -trophies.compareTo(((Player) o).trophies);
        }

        @SuppressLint("NewApi")
        public LinearLayout toLayout(final Context context) {
            LinearLayout entry = new LinearLayout(context);
            TextView usernameView = new TextView(context);
            TextView trophiesView = new TextView(context);
            final Button friendsButton = new Button(context);

            setTextSizeAndColor(usernameView, username, 25, Color.YELLOW);
            setTextSizeAndColor(trophiesView, trophies.toString(), 25, Color.LTGRAY);

            trophiesView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);

            usernameView.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // initializes the friendsButton on first view of leaderboard
            isFriendWithCurrenUser(context, initializeFriendsButton(friendsButton));

            // modifies friedsButton on every click
            friendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFriendWithCurrenUser(context,
                            changeFriendsButtonBackgroundOnClick(context, friendsButton));

                }
            });

            entry.addView(usernameView);
            entry.addView(trophiesView);
            entry.addView(friendsButton);
            entry.setBackgroundColor(Color.DKGRAY);
            entry.setPadding(30,10,30,10);

            return entry;
        }

        private void setTextSizeAndColor(TextView view, String text, float size, int color) {
            view.setText(text);
            view.setTextSize(size);
            view.setTextColor(color);
        }

        private void isFriendWithCurrenUser(final Context context, ValueEventListener listener) {
            Database.INSTANCE.getReference("users").child(userId).child("friends")
                    .child(Account.getInstance(context).getUserId())
                    .addListenerForSingleValueEvent(listener);
        }

        private ValueEventListener initializeFriendsButton(final Button friendButton) {
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

                }
            };
        }

        private ValueEventListener changeFriendsButtonBackgroundOnClick(final Context context, final Button friendButton) {
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

                }
            };
        }
    }
}
