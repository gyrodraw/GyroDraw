package ch.epfl.sweng.SDP.home.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.home.FriendsRequestState;

import static ch.epfl.sweng.SDP.firebase.FbDatabase.getFriend;

/**
 * Button to show if a user is a friend of current user and to manage friends requests.
 * Needs to be package-private.
 */
class FriendsButton extends AppCompatImageView {

    private static final String TAG = "FriendsButton";
    private static final String FIREBASE_ERROR = "There was a problem with Firebase";
    private static final int SENT = FriendsRequestState.SENT.ordinal();
    private static final int FRIENDS = FriendsRequestState.FRIENDS.ordinal();

    private final Context context;
    private final Account account;
    private final Player player;
    private final int index;
    private final boolean isCurrentUser;

    FriendsButton(final Context context, final Player player, int index, boolean isCurrentUser) {
        super(context);
        this.context = context;
        this.account = Account.getInstance(context);
        this.player = player;
        this.index = index;
        this.isCurrentUser = isCurrentUser;
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFriend(account.getUserId(),
                        player.getUserId(),
                        changeFriendsButtonImageOnClick());
            }
        });
        getFriend(account.getUserId(), player.getUserId(),
                initializeFriendsButton());
        initLayout();
    }

    private void initLayout() {
        LinearLayout.LayoutParams friendsParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        setLayoutParams(friendsParams);

        // Give Button unique Tag to test them later
        setTag("friendsButton" + index);

        // Set friendsButton invisible to yourself
        if (isCurrentUser) {
            setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Checks if users are already friends and sets image accordingly.
     *
     * @return listener
     */
    private ValueEventListener initializeFriendsButton() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    initializeImageCorrespondingToFriendsState(
                            dataSnapshot.getValue(int.class));
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
     * Sets the image depending on the friends state.
     *
     * @param state current state of friendship
     */
    @VisibleForTesting
    public void initializeImageCorrespondingToFriendsState(int state) {
        if (state == SENT) {
            setImageResource(R.drawable.pending_friend);
        } else if (state == FRIENDS) {
            setImageResource(R.drawable.remove_friend);
        } else {
            setImageResource(R.drawable.add_friend);
        }
    }

    /**
     * Friends button got clicked, now add/remove friend and modify image.
     *
     * @return listener
     */
    private ValueEventListener changeFriendsButtonImageOnClick() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setImageAndUpdateFriendsState(dataSnapshot.getValue(int.class));
                } else {
                    Account.getInstance(context).addFriend(player.getUserId());
                    setImageResource(R.drawable.pending_friend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, FIREBASE_ERROR);
            }
        };
    }

    /**
     * Helper function for changeFriendsButtonImageOnClick.
     * Updates the friends state depending on status.
     *
     * @param state current state of friendship
     */
    @VisibleForTesting
    public void setImageAndUpdateFriendsState(int state) {
        switch (FriendsRequestState.fromInteger(state)) {
            case RECEIVED:
                Account.getInstance(context).addFriend(player.getUserId());
                setImageResource(R.drawable.remove_friend);
                break;
            case FRIENDS:
            case SENT:
                Account.getInstance(context).removeFriend(player.getUserId());
                setImageResource(R.drawable.add_friend);
                break;
            default:
                break;
        }
    }
}