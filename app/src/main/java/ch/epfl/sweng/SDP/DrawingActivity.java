package ch.epfl.sweng.SDP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ToggleButton;


public class DrawingActivity extends AppCompatActivity implements SensorEventListener{
    private PaintView paintView;

    private static final String TAG = "DrawingActivity";
    private int speed;
    private Point size;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Boolean draw;
    ToggleButton fly_draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //final ToggleButton fly_or_draw = (ToggleButton) findViewById(R.id.fly_or_draw);

        speed = 5;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        final Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        paintView = findViewById(R.id.paintView);
        paintView.circleX = size.x / 2 - paintView.circleRadius;
        paintView.circleY = size.y / 2 - paintView.circleRadius;

        handler = new Handler(){
           @Override
           public void handleMessage(Message message){
               paintView.invalidate();
           }
        };
        fly_draw = findViewById(R.id.fly_or_draw);
    }

    public void fly_or_draw(View view){
        paintView.draw = ((ToggleButton) view).isChecked();
    }
    public void clear(View view) {
        paintView.clear();
        fly_draw.setChecked(false);
        paintView.draw = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();

        // Register accelerometer sensor listener
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        } else Log.d("No accelerometer", "Sorry, we couldn't find the accelerometer on your device.");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

            paintView.circleX -= sensorEvent.values[0] * speed;
            paintView.circleY += sensorEvent.values[1] * speed;

            if(paintView.circleX < 0) paintView.circleX = 0;
            else if(paintView.circleX > size.x) paintView.circleX = size.x;

            if(paintView.circleY < 0) paintView.circleY = 0;
            else if (paintView.circleY > size.y) paintView.circleY = size.y;

            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //does nothing
    }

}
