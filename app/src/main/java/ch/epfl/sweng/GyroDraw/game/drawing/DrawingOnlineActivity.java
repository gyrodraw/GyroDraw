package ch.epfl.sweng.GyroDraw.game.drawing;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.game.VotingPageActivity;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForImages;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.GyroDraw.matchmaking.GameStates;
import ch.epfl.sweng.GyroDraw.utils.network.ConnectivityWrapper;

import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.STATE;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.TIMER;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.UPLOAD_DRAWING;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.ROOM_ID;
import static ch.epfl.sweng.GyroDraw.game.WaitingPageActivity.WINNING_WORD;
import static ch.epfl.sweng.GyroDraw.game.drawing.FeedbackTextView.timeIsUpTextFeedback;

/**
 * Class representing the drawing phase of an online game in normal mode.
 */
public class DrawingOnlineActivity extends GyroDrawingActivity {

    private static final String TAG = "DrawingOnlineActivity";

    private String winningWord;

    private String roomId;

    protected final ValueEventListener listenerTimer = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);

            if (value != null) {
                ((TextView) findViewById(R.id.timeRemaining)).setText(String.valueOf(value));
            }
        }
    };

    protected final ValueEventListener listenerState = new OnSuccessValueEventListener() {
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
                                        FbDatabase.setValueToUserInRoomAttribute(roomId,
                                                Account.getInstance(getApplicationContext())
                                                        .getUsername(), UPLOAD_DRAWING, 1);
                                        Log.d(TAG, "Upload completed");

                                        Log.d(TAG, winningWord);
                                        FbDatabase.removeListenerFromRoomAttribute(roomId,
                                                TIMER, listenerTimer);

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
    };

    @VisibleForTesting
    @Override
    public int getLayoutId() {
        return R.layout.activity_drawing_online;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityWrapper.registerNetworkReceiver(this);

        Intent intent = getIntent();

        roomId = intent.getStringExtra(ROOM_ID);
        winningWord = intent.getStringExtra(WINNING_WORD);

        TextView wordView = findViewById(R.id.winningWord);
        wordView.setText(winningWord);
        wordView.setTypeface(typeMuro);

        ((TextView) findViewById(R.id.timeRemaining)).setTypeface(typeMuro);

        FbDatabase.setListenerToRoomAttribute(roomId, TIMER, listenerTimer);
        FbDatabase.setListenerToRoomAttribute(roomId, STATE, listenerState);

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
        FbDatabase.removeListenerFromRoomAttribute(roomId, TIMER, listenerTimer);
        FbDatabase.removeListenerFromRoomAttribute(roomId, STATE, listenerState);
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
     * Method that calls {@code listenerTimer.onDataChange()} on the UI thread.
     *
     * @param dataSnapshot Snapshot of the database (mock snapshot in our case).
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
