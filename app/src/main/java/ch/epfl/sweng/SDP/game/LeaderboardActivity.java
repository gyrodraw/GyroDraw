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
        EditText searchField = findViewById(R.id.searchField);
        leaderboard = findViewById(R.id.leaderboard);
        searchField.addTextChangedListener(new TextWatcher() {
            boolean _ignore = false;
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
                if (_ignore)
                    return;
                _ignore = true;

                updateLeaderboard(s.toString());

                _ignore = false;
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
                        leaderboard.removeAllViews();
                        LinkedList<Player> players = new LinkedList<>();
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
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
                            Player p = players.get(i);

                            leaderboard.addView(p.toLayout(getApplicationContext()), layoutParams);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private class Player implements Comparable{

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
        public RelativeLayout toLayout(final Context context) {
            RelativeLayout entry = new RelativeLayout(context);
            TextView name = new TextView(context);
            TextView troph = new TextView(context);
            final Button addFriend = new Button(context);
            name.setText(username);
            name.setTextSize(25);
            name.setTextColor(Color.YELLOW);
            troph.setText(trophies+"");
            troph.setTextSize(25);
            troph.setTextColor(Color.LTGRAY);
            troph.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            RelativeLayout.LayoutParams friendsButtonParams = new RelativeLayout.LayoutParams(90,90);
            friendsButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            addFriend.setLayoutParams(friendsButtonParams); //causes layout update
            RelativeLayout.LayoutParams trophiesParams = new RelativeLayout.LayoutParams(800,90);
            trophiesParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            trophiesParams.addRule(RelativeLayout.LEFT_OF, addFriend.getId());

            troph.setLayoutParams(trophiesParams);
            isFriendWithCurrenUser(context, new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        addFriend.setBackgroundResource(R.drawable.remove_friend);
                    } else {
                        addFriend.setBackgroundResource(R.drawable.add_friend);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFriendWithCurrenUser(context, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Account.getInstance(context).removeFriend(userId);
                                addFriend.setBackgroundResource(R.drawable.add_friend);
                            } else {
                                Account.getInstance(context).addFriend(userId);
                                addFriend.setBackgroundResource(R.drawable.remove_friend);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
            entry.addView(name);
            entry.addView(troph);
            entry.addView(addFriend);
            entry.setBackgroundColor(Color.DKGRAY);
            entry.setPadding(30,10,30,10);

            return entry;
        }

        private void isFriendWithCurrenUser(final Context context, ValueEventListener listener) {
            Database.INSTANCE.getReference("users").child(userId).child("friends")
                    .child(Account.getInstance(context).getUserId())
                    .addListenerForSingleValueEvent(listener);
        }
    }
}
