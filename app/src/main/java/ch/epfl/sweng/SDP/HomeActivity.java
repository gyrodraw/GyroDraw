package ch.epfl.sweng.SDP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private static final int TOP_BUTTONS_FREQUENCY = 10;
    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;

    private static final double MAIN_AMPLITUDE = 0.1;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;

    private static final int LEFT_PADDING = 140;
    private static final int TOP_PADDING = -5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        final ImageView drawButton = findViewById(R.id.drawButton);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final Button trophiesButton = findViewById(R.id.trophiesButton);
        final Button starsButton = findViewById(R.id.starsButton);
        final ImageView leagueImage = findViewById(R.id.leagueImage);
        TextView leagueText = findViewById(R.id.leagueText);

        usernameButton.setTypeface(typeMuro);
        trophiesButton.setTypeface(typeMuro);
        starsButton.setTypeface(typeMuro);
        leagueText.setTypeface(typeOptimus);

        trophiesButton.setPadding(LEFT_PADDING, TOP_PADDING, 0, 0);
        starsButton.setPadding(LEFT_PADDING, TOP_PADDING, 0, 0);

        setDrawButtonListener(drawButton);
        setListener(trophiesButton, MAIN_AMPLITUDE, TOP_BUTTONS_FREQUENCY);
        setListener(starsButton, MAIN_AMPLITUDE, TOP_BUTTONS_FREQUENCY);
        setListener(leagueImage, MAIN_AMPLITUDE, LEAGUE_IMAGE_FREQUENCY);
        setUsernameButtonListener(usernameButton);
    }

    /**
     * Signs the current user out and starts the {@link MainActivity}.
     *
     * @param view the view corresponding to the clicked button
     */
    public void signOut(View view) {
        final Toast toastSignOut = makeAndShowToast("Signing out...");

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            toastSignOut.cancel();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Sign out failed!");
                        }
                    }
                });
    }

    /**
     * Deletes the user from FirebaseAuth and deletes any existing credentials for the user in
     * Google Smart Lock. It then starts the {@link MainActivity}.
     *
     * @param view the view corresponding to the clicked button
     */
    public void delete(View view) {
        final Toast toastDelete = makeAndShowToast("Deleting account...");

        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            toastDelete.cancel();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Delete account failed!");
                        }
                    }
                });
    }

    private Toast makeAndShowToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }


    private void setListener(final View view, final double amplitude, final int frequency) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(view, amplitude, frequency);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void setDrawButtonListener(final ImageView drawButton) {
        drawButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawButton.setImageResource(R.drawable.draw_button_pressed);
                        pressButton(drawButton);
                        break;
                    case MotionEvent.ACTION_UP:
                        drawButton.setImageResource(R.drawable.draw_button);
                        bounceButton(drawButton, DRAW_BUTTON_AMPLITUDE, DRAW_BUTTON_FREQUENCY);
                        startDrawingActivity();
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void setUsernameButtonListener(Button usernameButton) {
        usernameButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        bounceButton(view, MAIN_AMPLITUDE, TOP_BUTTONS_FREQUENCY);
                        startPopUpActivity();
                        break;
                    default:
                }
                return true;
            }
        });
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

    private void startDrawingActivity() {
        Intent intent = new Intent(this, DrawingActivity.class);
        startActivity(intent);
    }

    private void startPopUpActivity() {
        Intent intent = new Intent(this, PopUpActivity.class);
        startActivity(intent);
    }
}
