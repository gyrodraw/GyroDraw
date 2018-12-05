package ch.epfl.sweng.SDP.home.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.R;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import static java.lang.String.format;

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
    private final Player player;
    private final int index;
    private final boolean isCurrentUser;

    FriendsButton(Context context, Player player, int index, boolean isCurrentUser) {
        super(context);
        this.context = context;
        this.player = player;
        this.index = index;
        this.isCurrentUser = isCurrentUser;
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFriendWithCurrentUser(
                        changeFriendsButtonImageOnClick());
            }
        });
        this.isFriendWithCurrentUser(initializeFriendsButton());
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
     * Gets data if users are friends, else null. Then applies listener.
     *
     * @param listener how to handle response
     */
    private void isFriendWithCurrentUser(ValueEventListener listener) {
        Database.constructBuilder().addChildren(
                format("users.%s.friends.%s",
                        Account.getInstance(context).getUserId(),
                        player.getUserId())).build()
                .addListenerForSingleValueEvent(listener);
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
                    int status = dataSnapshot.getValue(int.class);
                    if (status == SENT) {
                        setImageResource(R.drawable.pending_friend);
                    } else if (status == FRIENDS) {
                        setImageResource(R.drawable.remove_friend);
                    } else {
                        setImageResource(R.drawable.add_friend);
                    }
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
     * Friends button got clicked, now add/remove friend and modify image.
     *
     * @return listener
     */
    private ValueEventListener changeFriendsButtonImageOnClick() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int status = dataSnapshot.getValue(int.class);
                    switch (FriendsRequestState.fromInteger(status)) {
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
}