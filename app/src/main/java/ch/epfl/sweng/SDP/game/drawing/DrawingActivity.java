package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;


public class DrawingActivity extends Activity implements SensorEventListener {
    private static final String TAG = "DrawingActivity";
    private PaintView paintView;
    private int speed;
    private int time;
    private int timeIntervall;
    private Point size;
    private Handler handler;
    private SensorManager sensorManager;

    private ImageView blackButton;
    private ImageView blueButton;
    private ImageView greenButton;
    private ImageView yellowButton;
    private ImageView redButton;

    private ImageView pencilButton;
    private ImageView eraserButton;
    private ImageView bucketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.fui_slide_in_right,
                R.anim.fui_slide_out_left);
        setContentView(R.layout.activity_drawing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        blackButton = findViewById(R.id.blackButton);
        blueButton = findViewById(R.id.blueButton);
        greenButton = findViewById(R.id.greenButton);
        yellowButton = findViewById(R.id.yellowButton);
        redButton = findViewById(R.id.redButton);

        pencilButton = findViewById(R.id.pencilButton);
        eraserButton = findViewById(R.id.eraserButton);
        bucketButton = findViewById(R.id.bucketButton);

        Resources res = getResources();
        blueButton.setColorFilter(res.getColor(R.color.colorBlue), PorterDuff.Mode.SRC_ATOP);
        greenButton.setColorFilter(res.getColor(R.color.colorGreen), PorterDuff.Mode.SRC_ATOP);
        yellowButton.setColorFilter(res.getColor(R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
        redButton.setColorFilter(res.getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        ((TextView) findViewById(R.id.timeRemaining)).setTypeface(typeMuro);

        speed = 5; //will be passed as variable in future, not hardcoded
        time = 60000; //will be passed as variable in future, not hardcoded
        timeIntervall = 1000; //will be passed as variable in future, not hardcoded

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        paintView = findViewById(R.id.paintView);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE);
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.

        final Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        final ViewTreeObserver observer = paintView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paintView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                paintView.setSizeAndInit(paintView.getWidth(), paintView.getHeight());
            }
        });

        paintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        paintView.setDraw(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        paintView.setDraw(false);
                        break;
                    default:
                }
                return true;
            }
        });

        setCountdownTimer();

        // informes the paintView that it has to be updated
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

    /**
     * Initializes the countdown to a given time.
     *
     * @return the countdown
     */
    private CountDownTimer setCountdownTimer() {
        return new CountDownTimer(time, timeIntervall) {

            public void onTick(long millisUntilFinished) {
                TextView textView = findViewById(R.id.timeRemaining);
                textView.setText(Long.toString(millisUntilFinished / timeIntervall));
            }

            public void onFinish() {
                TextView textView = findViewById(R.id.timeRemaining);
                textView.setTextSize(20);
                textView.setText("Time over!");
                stop();
            }
        }.start();

    }

    /**
     * Getter of the paint view.
     *
     * @return the paint view
     */
    public PaintView getPaintView() {
        return paintView;
    }

    /**
     * Clears the entire Path in paintView.
     *
     * @param view paintView
     */
    public void clear(View view) {
        paintView.clear();
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
     * @param coordinateX coordiate
     * @param coordinateY coordinate
     */
    public void updateValues(float coordinateX, float coordinateY) {
        float tempX = paintView.getCircleX();
        float tempY = paintView.getCircleY();

        tempX -= coordinateX * speed;
        tempY += coordinateY * speed;

        paintView.setCircleX(tempX);
        paintView.setCircleY(tempY);
    }

    /**
     * Gets called when time is over.
     * Saves drawing in database and storage and calls new activity.
     */
    private void stop() {
        paintView.saveCanvasInDb(this);
        // add redirection here
    }

    /**
     * Sets the clicked button to selected and sets the corresponding color
     *
     * @param view
     */
    public void colorClickHandler(View view) {
        Resources res = getResources();
        ((ImageView) view).setImageResource(R.drawable.color_circle_selected);

        switch (view.getId()) {
            case R.id.blackButton:
                paintView.setColor(Color.BLACK);
                blueButton.setImageResource(R.drawable.color_circle);
                greenButton.setImageResource(R.drawable.color_circle);
                yellowButton.setImageResource(R.drawable.color_circle);
                redButton.setImageResource(R.drawable.color_circle);
                break;
            case R.id.blueButton:
                paintView.setColor(res.getColor(R.color.colorBlue));
                blackButton.setImageResource(R.drawable.color_circle);
                greenButton.setImageResource(R.drawable.color_circle);
                yellowButton.setImageResource(R.drawable.color_circle);
                redButton.setImageResource(R.drawable.color_circle);
                break;
            case R.id.greenButton:
                paintView.setColor(res.getColor(R.color.colorGreen));
                blackButton.setImageResource(R.drawable.color_circle);
                blueButton.setImageResource(R.drawable.color_circle);
                yellowButton.setImageResource(R.drawable.color_circle);
                redButton.setImageResource(R.drawable.color_circle);
                break;
            case R.id.yellowButton:
                paintView.setColor(res.getColor(R.color.colorYellow));
                blackButton.setImageResource(R.drawable.color_circle);
                blueButton.setImageResource(R.drawable.color_circle);
                greenButton.setImageResource(R.drawable.color_circle);
                redButton.setImageResource(R.drawable.color_circle);
                break;
            case R.id.redButton:
                paintView.setColor(res.getColor(R.color.colorRed));
                blackButton.setImageResource(R.drawable.color_circle);
                blueButton.setImageResource(R.drawable.color_circle);
                greenButton.setImageResource(R.drawable.color_circle);
                yellowButton.setImageResource(R.drawable.color_circle);
                break;
            default:
        }
    }

    /**
     * Sets the clicked button to selected and sets the corresponding color
     *
     * @param view
     */
    public void toolClickHandler(View view) {
        switch (view.getId()) {
            case R.id.pencilButton:
                pencilButton.setImageResource(R.drawable.pencil_selected);
                eraserButton.setImageResource(R.drawable.eraser);
                bucketButton.setImageResource(R.drawable.bucket);
                break;
            case R.id.eraserButton:
                pencilButton.setImageResource(R.drawable.pencil);
                eraserButton.setImageResource(R.drawable.eraser_selected);
                bucketButton.setImageResource(R.drawable.bucket);
                break;
            case R.id.bucketButton:
                pencilButton.setImageResource(R.drawable.pencil);
                eraserButton.setImageResource(R.drawable.eraser);
                bucketButton.setImageResource(R.drawable.bucket_selected);
                break;
            default:
        }
    }
}
