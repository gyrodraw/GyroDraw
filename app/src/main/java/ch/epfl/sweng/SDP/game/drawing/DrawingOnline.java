package ch.epfl.sweng.SDP.game.drawing;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DrawingOnline extends DrawingOffline {

    private int time;
    private int timeInterval;
    private String winningWord;

    private static final String TOP_ROOM_NODE_ID = "realRooms";

    private String roomId;

    private final Database database = Database.INSTANCE;
    private DatabaseReference timerRef;
    private DatabaseReference stateRef;
    private boolean isVotingActivityLaunched = false;

    @Override
    protected int getLayoutid() {
        return R.layout.activity_drawing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        time = 60000; //will be passed as variable in future, not hardcoded
        timeInterval = 1000;  //will be passed as variable in future, not hardcoded

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        ((TextView) findViewById(R.id.timeRemaining)).setTypeface(typeMuro);

        String path = TOP_ROOM_NODE_ID + "." + roomId + ".timer.observableTime";
        timerRef = database.getReference(path);
        timerRef.addValueEventListener(listenerTimer);
        stateRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomId + ".state");
        stateRef.addValueEventListener(listenerState);

        Intent intent = getIntent();

        roomId = intent.getStringExtra("RoomID");
        winningWord = intent.getStringExtra("WinningWord");

        setCountdownTimer();

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

    protected final ValueEventListener listenerState = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer state = dataSnapshot.getValue(Integer.class);
            if(state != null) {
                GameStates stateEnum = GameStates.convertValueIntoState(state);
                switch(stateEnum) {
                    case START_VOTING_ACTIVITY:
                        timerRef.removeEventListener(listenerTimer);
                        Intent intent = new Intent(getApplicationContext(),
                                VotingPageActivity.class);
                        Log.d(TAG,winningWord);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Matchmaker.getInstance(Account.getInstance(this))
                    .leaveRoom(roomId);
            launchActivity(HomeActivity.class);
            finish();
        }
        return super.onKeyDown(keyCode, event);

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
     * Initializes the countdown to a given time.
     *
     * @return the countdown
     */
    private CountDownTimer setCountdownTimer() {
        return new CountDownTimer(time, timeInterval) {
            public void onTick(long millisUntilFinished) {
                TextView textView = findViewById(R.id.timeRemaining);
                textView.setText(Long.toString(millisUntilFinished / timeInterval));
            }

            public void onFinish() {
                TextView textView = findViewById(R.id.timeRemaining);
                textView.setTextSize(20);
                textView.setText("Time over!");
                stop();
            }
        }.start();
    }


}
