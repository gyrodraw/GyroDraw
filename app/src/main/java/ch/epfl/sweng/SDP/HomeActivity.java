package ch.epfl.sweng.SDP;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        view = this.getWindow().getDecorView();
        view.setBackgroundResource(R.color.colorGrey);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(),"fonts/Muro.otf");
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(),"fonts/Optimus.otf");

        final ImageView drawButton = findViewById(R.id.drawButton);
        final Button trophiesButton = findViewById(R.id.trophiesButton);
        final Button starsButton = findViewById(R.id.starsButton);
        final ImageView leagueImage = findViewById(R.id.leagueImage);
        TextView leagueText = findViewById(R.id.leagueText);

        drawButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawButton.setImageResource(R.drawable.draw_button_pressed);
                        pressButton(drawButton);
                        break;
                    case MotionEvent.ACTION_UP:
                        drawButton.setImageResource(R.drawable.draw_button);
                        bounceButton(drawButton, 0.2, 20);
                }
                return true;
            }
        });

        trophiesButton.setTypeface(typeMuro);
        trophiesButton.setPadding(140, -5, 0, 0);
        trophiesButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(trophiesButton);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(trophiesButton, 0.1, 10);
                }
                return false;
            }
        });

        starsButton.setTypeface(typeMuro);
        starsButton.setPadding(140, -5, 0, 0);
        starsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(starsButton);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(starsButton, 0.1, 10);
                }
                return false;
            }
        });

        leagueImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(leagueImage);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(leagueImage, 0.1, 30);
                }
                return true;
            }
        });

        leagueText.setTypeface(typeOptimus);
        leagueText.setPadding(0, -14, 0, 0);
    }

    private void bounceButton(View view, double amplitude, int frequency) {
        final Animation bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(amplitude, frequency);
        bounce.setInterpolator(interpolator);
        view.startAnimation(bounce);
    }

    private void pressButton(View view) {
        final Animation press = AnimationUtils.loadAnimation(this, R.anim.press);
        press.setFillAfter(true);
        view.startAnimation(press);
    }
}