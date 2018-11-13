package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DrawingOffline extends DrawingActivity implements SensorEventListener {

    private int speed;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        speed = 5; //will be passed as variable in future, not hardcoded

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

    private void exitClick(View view) {
        Log.d(TAG, "Exiting drawing view");
        this.finish();
    }

}
