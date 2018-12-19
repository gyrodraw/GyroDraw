package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Abstract class representing the drawing via gyroscope page of the game.
 */
public abstract class GyroDrawingActivity extends DrawingActivity implements SensorEventListener {

    private static final String TAG = "GyroDrawingActivity";

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register accelerometer sensor listener
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.d(TAG, "We couldn't find the accelerometer on device.");
        }
    }

    /**
     * Fires when a sensor detects a change.
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
     * Called when accelerometer changed. Updates circle's coordinates.
     *
     * @param coordinateX coordinate
     * @param coordinateY coordinate
     */
    public void updateValues(float coordinateX, float coordinateY) {
        paintView.updateCoordinates(coordinateX, coordinateY);
    }
}
