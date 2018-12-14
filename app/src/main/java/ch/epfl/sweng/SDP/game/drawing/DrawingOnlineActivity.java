package ch.epfl.sweng.SDP.game.drawing;

import static ch.epfl.sweng.SDP.game.LoadingScreenActivity.ROOM_ID;
import static ch.epfl.sweng.SDP.game.WaitingPageActivity.WINNING_WORD;
import static ch.epfl.sweng.SDP.game.drawing.FeedbackTextView.timeIsUpTextFeedback;
import static java.lang.String.format;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbForImages;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.utils.network.ConnectivityWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

/**
 * Class representing the drawing phase of an online game in normal mode.
 */
public class DrawingOnlineActivity extends GyroDrawingActivity {

    private static final String TOP_ROOM_NODE_ID = "realRooms";

    private String winningWord;

    private String roomId;

    private DatabaseReference timerRef;
    private DatabaseReference stateRef;

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
                    case WAITING_UPLOAD:
                        DrawingOnlineActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                paintViewHolder
                                        .addView(timeIsUpTextFeedback(DrawingOnlineActivity.this));
                            }
                        });
                        uploadDrawing().addOnCompleteListener(
                                new OnCompleteListener<TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<TaskSnapshot> task) {
                                        Database.getReference(
                                                format("%s.%s.uploadDrawing.%s", TOP_ROOM_NODE_ID,
                                                        roomId,
                                                        Account.getInstance(getApplicationContext())
                                                                .getUsername())).setValue(1);
                                        Log.d(TAG, "Upload completed");

                                        Log.d(TAG, winningWord);
                                        timerRef.removeEventListener(listenerTimer);

                                        Intent intent = new Intent(getApplicationContext(),
                                                VotingPageActivity.class);
                                        intent.putExtra(ROOM_ID, roomId);
                                        startActivity(intent);
                                    }
                                });
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

    @VisibleForTesting
    @Override
    public int getLayoutId() {
        return R.layout.activity_drawing_online;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        ConnectivityWrapper.registerNetworkReceiver(this);

        Intent intent = getIntent();

        roomId = intent.getStringExtra(ROOM_ID);
        winningWord = intent.getStringExtra(WINNING_WORD);

        TextView wordView = findViewById(R.id.winningWord);
        wordView.setText(winningWord);
        wordView.setTypeface(typeMuro);

        ((TextView) findViewById(R.id.timeRemaining)).setTypeface(typeMuro);

        timerRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomId + ".timer.observableTime");
        timerRef.addValueEventListener(listenerTimer);
        stateRef = Database.getReference(TOP_ROOM_NODE_ID + "." + roomId + ".state");
        stateRef.addValueEventListener(listenerState);

        ConnectivityWrapper.setOnlineStatusInGame(roomId, Account.getInstance(this).getUsername());
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectivityWrapper.unregisterNetworkReceiver(this);

        removeAllListeners();
        finish();
    }

    private void removeAllListeners() {
        timerRef.removeEventListener(listenerTimer);
        stateRef.removeEventListener(listenerState);
    }

    /**
     * Saves drawing in the local database and uploads it to Firebase Storage.
     *
     * @return the {@link StorageTask} in charge of the upload
     */
    private StorageTask<TaskSnapshot> uploadDrawing() {
        LocalDbForImages localDbHandler = new LocalDbHandlerForImages(this, null, 1);
        paintView.saveCanvasInDb(localDbHandler);
        return paintView.saveCanvasInStorage();
    }

    /**
     * Method that call onDataChange on the UI thread.
     *
     * @param dataSnapshot Snapshot of the database (mock snapshot in out case).
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void callOnDataChangeTimer(final DataSnapshot dataSnapshot) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listenerTimer.onDataChange(dataSnapshot);
            }
        });
    }
}
