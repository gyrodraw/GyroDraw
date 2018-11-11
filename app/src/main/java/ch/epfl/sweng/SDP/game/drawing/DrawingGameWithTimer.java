package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;

public class DrawingGameWithTimer extends DrawingGame {

    private int time;
    private int timeInterval;

    @Override
    int getLayoutid() {
        return R.layout.activity_drawing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        time = 60000; //will be passed as variable in future, not hardcoded
        timeInterval = 1000;  //will be passed as variable in future, not hardcoded

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        ((TextView) findViewById(R.id.timeRemaining)).setTypeface(typeMuro);

        setCountdownTimer();

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
