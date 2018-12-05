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

    private Typeface typeMuro;
    private LinearLayout leaderboardView;
    private Leaderboard leaderboard;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_leaderboard);

        leaderboardView = findViewById(R.id.leaderboard);

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        final EditText searchField = findViewById(R.id.searchField);
        TextView exitButton = findViewById(R.id.exitButton);
        LayoutUtils.setFadingExitListener(exitButton, this);
        exitButton.setTypeface(typeMuro);
        searchField.setTypeface(typeMuro);

        leaderboard = new Leaderboard(getApplicationContext(), leaderboardView);
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
                leaderboard.xOrFilterByFriends();
                if (leaderboard.getFilterByFriends()) {
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
}
