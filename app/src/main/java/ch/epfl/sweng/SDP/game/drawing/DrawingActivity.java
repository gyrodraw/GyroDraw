package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.LocalDbHandler;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;


public class DrawingActivity extends Activity implements SensorEventListener {
    private PaintView paintView;

    private static final String TAG = "DrawingActivity";
    private int speed;
    private int time;
    private int timeInterval;
    private Point size;
    private Handler handler;
    private SensorManager sensorManager;
    private String roomID;
    private String winningWord;
    ToggleButton flyDraw;

    private final Database database = Database.INSTANCE;
    private DatabaseReference timerRef;
    private DatabaseReference stateRef;

    private final ValueEventListener listenerState = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer state = dataSnapshot.getValue(Integer.class);
            if(state != null) {
                GameStates stateEnum = GameStates.convertValueIntoState(state);
                switch(stateEnum) {
                    case START_VOTING_ACTIVITY:
                        timerRef.removeEventListener(listenerTimer);
                        Intent intent = new Intent(getApplicationContext()
                                                , VotingPageActivity.class);
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

    private final ValueEventListener listenerTimer = new ValueEventListener() {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        roomID = intent.getStringExtra("RoomID");
        winningWord = intent.getStringExtra("WinningWord");

        ((TextView) findViewById(R.id.winningWord)).setText(winningWord);

        timerRef = database.getReference("realRooms." + roomID + ".timer.observableTime");
        timerRef.addValueEventListener(listenerTimer);
        stateRef = database.getReference("realRooms." + roomID + ".state");
        stateRef.addValueEventListener(listenerState);

        speed = 5; //will be passed as variable in future, not hardcoded
        time = 60000; //will be passed as variable in future, not hardcoded
        timeInterval = 1000; //will be passed as variable in future, not hardcoded

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        flyDraw = findViewById(R.id.flyOrDraw);
        paintView = findViewById(R.id.paintView);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);


    final Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        paintView.setSizeAndInit(size);

        // setCountdownTimer();

        // informs the paintView that it has to be updated
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                paintView.invalidate();
            }
        };
    }

    public Point getSize() {
        return size;
    }

    public void setSize(Point size) {
        this.size = size;
    }

    /**
     * Checks if ToggleButton Draw is checked and saves the boolean in paintView.draw.
     * which enables the user to either fly or draw
     *
     * @param view ToggleButton
     */
    public void flyOrDraw(View view) {
        paintView.setDraw(((ToggleButton) view).isChecked());
    }

    /**
     * Clears the entire Path in paintView.
     *
     * @param view paintView
     */
    public void clear(View view) {
        paintView.clear();
        flyDraw.setChecked(false);
        paintView.setDraw(false);
    }

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
     * @param coordinateX coordinate
     * @param coordinateY coordinate
     */
    public void updateValues(float coordinateX, float coordinateY) {
        float tempX = paintView.getCircleX();
        float tempY = paintView.getCircleY();

        tempX -= coordinateX * speed;
        tempY += coordinateY * speed;

        tempX = sanitizeCoordinate(tempX, size.x);
        tempY = sanitizeCoordinate(tempY, size.y);

        paintView.setCircleX(tempX);
        paintView.setCircleY(tempY);
    }

    /**
     * Keep coordinates within screen boundaries.
     *
     * @param coordinate coordinate to sanitize
     * @param maxBound   maximum bound
     * @return sanitized coordinate
     */
    public float sanitizeCoordinate(float coordinate, float maxBound) {
        if (coordinate < 0) {
            return 0;
        } else if (coordinate > maxBound) {
            return maxBound;
        } else {
            return coordinate;
        }
    }

    /**
     * Gets called when time is over.
     * Saves drawing in database and storage and calls new activity.
     */
    private void stop(){
        LocalDbHandler localDbHandler = new LocalDbHandler(this, null, 1);
        paintView.saveCanvasInDb(localDbHandler);
        paintView.saveCanvasInStorage();
        // add redirection here
    }

    private void removeAllListeners() {
        timerRef.removeEventListener(listenerTimer);
        stateRef.removeEventListener(listenerState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Matchmaker.INSTANCE.leaveRoom(roomID);
            removeAllListeners();
            launchActivity(HomeActivity.class);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
