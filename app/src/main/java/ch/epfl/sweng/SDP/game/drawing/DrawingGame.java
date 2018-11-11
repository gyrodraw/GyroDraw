package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DrawingGame extends DrawingActivity implements SensorEventListener {


    private int speed;
    private SensorManager sensorManager;

    private final Database database = Database.INSTANCE;
    private DatabaseReference timerRef;
    private DatabaseReference stateRef;
    private boolean isVotingActivityLaunched = false;

    private static final String TOP_ROOM_NODE_ID = "realRooms";

    private String roomID;

    protected final ValueEventListener listenerTimer = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);

            if(value != null) {
                ((TextView) findViewById(R.id.timeRemaining)).setText(String.valueOf(value));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Does nothing for the moment
        }
    };

    protected String winningWord;

    @Override
    int getLayoutid() {
        return R.layout.activity_drawing_offline;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            speed = 5; //will be passed as variable in future, not hardcoded

             Intent intent = getIntent();

              timerRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".timer.observableTime");
              timerRef.addValueEventListener(listenerTimer);
              stateRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".state");
            stateRef.addValueEventListener(listenerState);

             roomID = intent.getStringExtra("RoomID");
             winningWord = intent.getStringExtra("WinningWord");

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    // MARK: ACCELEROMETER METHODS

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();

        // Register accelerometer sensor listener
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Log.d(TAG, "We couldn't find the accelerometer on device.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Does not leave the room if the activity is stopped because
        // voting activity is launched.
        if (!isVotingActivityLaunched) {
            Matchmaker.getInstance(Account.getInstance(this)).leaveRoom(roomID);
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
                        intent.putExtra("RoomID", roomID);
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

    /**
     * Fires when a sensor detected a change.
     *
     * @param sensorEvent the sensor that has changed
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        // we only use accelerometer
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            updateValues(sensorEvent.values[0], sensorEvent.values[1]);
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //does nothing
    }

    /**
     * Called when accelerometer changed, circle coordinates are updated.
     *
     * @param coordinateX coordiate
     * @param coordinateY coordinate
     */
    public void updateValues(float coordinateX, float coordinateY) {
        float tempX = paintView.getCircleX();
        float tempY = paintView.getCircleY();

        tempX -= coordinateX * speed;
        tempY += coordinateY * speed;

        paintView.setCircle((int) tempX, (int) tempY);
    }

    public void exitClick(View view) {
        Log.d(TAG, "Exiting drawing view");
        this.finish();
    }

    /**
     * Method that call onDataChange on the UI thread.
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
