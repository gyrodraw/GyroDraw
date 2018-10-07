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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;


public class DrawingActivity extends AppCompatActivity implements SensorEventListener{
    private CanvasView canvas;

    private static final String TAG = "DrawingActivity";
    private int circleRadius;
    private float circleX;
    private float circleY;
    private int speed;
    private Path path;
    private Point size;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        circleRadius = 8;
        speed = 5;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        final Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        circleX = size.x / 2 - circleRadius;
        circleY = size.y / 2 - circleRadius;

        canvas = new CanvasView(DrawingActivity.this);
        setContentView(canvas);
        path = new Path();
        path.moveTo(circleX, circleY);

        handler = new Handler(){
           @Override
           public void handleMessage(Message message){
               canvas.invalidate();
           }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

            circleX -= sensorEvent.values[0] * speed;
            circleY += sensorEvent.values[1] * speed;
            if(circleX < 0) circleX = 0;
            else if(circleX > size.x){
                circleX = size.x;
            }
            if(circleY < 0) circleY = 0;
            else if (circleY > size.y){
                circleY = size.y;
            } else {
                Log.d(TAG, "Strange sensor event registered...");
            }
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //does nothing
    }

    private class CanvasView extends View {

        Paint paint;

        public CanvasView(Context context){
            super(context);
            setFocusable(true);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(10);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setXfermode(null);
            paint.setAlpha(0xff);
        }

        public void onDraw(Canvas canvas){
            canvas.save();
            path.lineTo(circleX, circleY);
            canvas.drawCircle(circleX, circleY, circleRadius, paint);
            canvas.drawPath(path, paint);
            canvas.restore();
            path.moveTo(circleX, circleY);
        }
    }

}
