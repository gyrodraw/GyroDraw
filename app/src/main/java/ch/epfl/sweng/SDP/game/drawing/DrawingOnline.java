package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;

public class DrawingOnline extends GyroDrawingActivity {

    private String winningWord;

    private static final String TOP_ROOM_NODE_ID = "realRooms";

    private String roomId;

    private DatabaseReference timerRef;
    private DatabaseReference stateRef;
    private boolean isVotingActivityLaunched = false;

    protected final ValueEventListener listenerTimer = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);

            if (value != null) {
                ((TextView) findViewById(R.id.timeRemaining)).setText(String.valueOf(value));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Does nothing for the moment
        }
    };

    protected final ValueEventListener listenerState = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer state = dataSnapshot.getValue(Integer.class);
            if (state != null) {
                GameStates stateEnum = GameStates.convertValueIntoState(state);
                switch (stateEnum) {
                    case START_VOTING_ACTIVITY:
                        stop();
                        isVotingActivityLaunched = true;
                        timerRef.removeEventListener(listenerTimer);
                        Intent intent = new Intent(getApplicationContext(),
                                VotingPageActivity.class);
                        Log.d(TAG, winningWord);
                        intent.putExtra("RoomID", roomId);
                        startActivity(intent);
                        break;
                    default:
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Does nothing for the moment
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drawing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        roomId = intent.getStringExtra("RoomID");
        winningWord = intent.getStringExtra("WinningWord");

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        ((TextView) findViewById(R.id.timeRemaining)).setTypeface(typeMuro);

        String path = TOP_ROOM_NODE_ID + "." + roomId + ".timer.observableTime";
        timerRef = Database.getReference(path);
        timerRef.addValueEventListener(listenerTimer);
        stateRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomId + ".state");
        stateRef.addValueEventListener(listenerState);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Does not leave the room if the activity is stopped because
        // voting activity is launched.
        if (!isVotingActivityLaunched) {
            Matchmaker.getInstance(Account.getInstance(this)).leaveRoom(roomId);
        }

        removeAllListeners();

        finish();
    }

    protected void removeAllListeners() {
        timerRef.removeEventListener(listenerTimer);
        stateRef.removeEventListener(listenerState);
    }

    // MARK: COUNTDOWN METHODS

    /**
     * Gets called when time is over.
     * Saves drawing in database and storage and calls new activity.
     */
    private void stop() {
        LocalDbHandlerForImages localDbHandler = new LocalDbHandlerForImages(this, null, 1);
        paintView.saveCanvasInDb(localDbHandler);
        paintView.saveCanvasInStorage();
        // add redirection here
    }

    /**
     * Method that call onDataChange on the UI thread.
     *
     * @param dataSnapshot Snapshot of the database (mock snapshot
     *                     in out case).
     */
    @VisibleForTesting
    public void callOnDataChangeTimer(final DataSnapshot dataSnapshot) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listenerTimer.onDataChange(dataSnapshot);
            }
        });
    }
}
