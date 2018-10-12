package ch.epfl.sweng.SDP;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity {
    private Dialog profileWindow;

    private static boolean enableBackgroundAnimation = true;
    private static final String TAG = "HomeActivity";

    private static final int MAIN_FREQUENCY = 10;
    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;

    private static final double MAIN_AMPLITUDE = 0.1;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;

    private static final int LEFT_PADDING = 140;
    private static final int TOP_PADDING = -5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);git

        setContentView(R.layout.activity_home);
        profileWindow = new Dialog(this);
        if (enableBackgroundAnimation) { setBackgroundAnimation(); }

        final ImageView drawButton = findViewById(R.id.drawButton);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final Button trophiesButton = findViewById(R.id.trophiesButton);
        final Button starsButton = findViewById(R.id.starsButton);
        final ImageView leagueImage = findViewById(R.id.leagueImage);
        TextView leagueText = findViewById(R.id.leagueText);
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        leagueText.setTypeface(typeOptimus);
        usernameButton.setTypeface(typeMuro);
        trophiesButton.setTypeface(typeMuro);
        starsButton.setTypeface(typeMuro);
        trophiesButton.setPadding(LEFT_PADDING, TOP_PADDING, 0, 0);
        starsButton.setPadding(LEFT_PADDING, TOP_PADDING, 0, 0);
        setListener(drawButton, DRAW_BUTTON_AMPLITUDE, DRAW_BUTTON_FREQUENCY);
        setListener(trophiesButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(starsButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(leagueImage, MAIN_AMPLITUDE, LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
    }

    /**
     * Signs the current user out and starts the {@link MainActivity}.
     */
    private void signOut() {
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
        profileWindow.dismiss();
    }

    /**
     * Deletes the user from FirebaseAuth and deletes any existing credentials for the user in
     * Google Smart Lock. It then starts the {@link MainActivity}.
     */
    private void delete() {
        final Toast toastDelete = makeAndShowToast("Deleting account...");

        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            getDefaultSharedPreferences(getApplicationContext()).edit()
                                    .putBoolean("hasAccount", false).apply();

                            toastDelete.cancel();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Delete account failed!");
                        }
                    }
                });
        profileWindow.dismiss();
    }

    private Toast makeAndShowToast(String msg) {
        assert msg != null;
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    private void setBackgroundAnimation() {
        final ImageView backgroundImage = findViewById(R.id.backgroundImage);
        final Animation backgroundAnim = AnimationUtils.loadAnimation(this, R.anim.background_anim);
        backgroundAnim.setInterpolator(new LinearInterpolator());
        backgroundImage.startAnimation(backgroundAnim);
    }

    private void setListener(final View view, final double amplitude, final int frequency) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int id = view.getId();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (id == R.id.drawButton) { ((ImageView) view).setImageResource(R.drawable.draw_button_pressed); }
                        pressButton(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (id == R.id.drawButton) {
                            ((ImageView) view).setImageResource(R.drawable.draw_button);
                            startDrawingActivity();
                        }
                        else if (id == R.id.usernameButton) { showPopup(); }
                        else if (id == R.id.signOutButton) { signOut(); }
                        else if (id == R.id.deleteButton) { delete(); }
                        else if (id == R.id.crossText) { profileWindow.dismiss(); }
                        bounceButton(view, amplitude, frequency);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void bounceButton(View view, double amplitude, int frequency) {
        assert amplitude != 0;
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

    private void showPopup() {
        profileWindow.setContentView(R.layout.activity_pop_up);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        TextView crossText = profileWindow.findViewById(R.id.crossText);
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        Button deleteButton = profileWindow.findViewById(R.id.deleteButton);

        crossText.setTypeface(typeMuro);
        signOutButton.setTypeface(typeMuro);
        deleteButton.setTypeface(typeMuro);

        setListener(crossText, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(signOutButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(deleteButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);

        profileWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileWindow.show();
    }

    /**
     * Disables the background animation.
     * Call this method in every HomeActivity test
     */
    public static void disableBackgroundAnimation() {
        enableBackgroundAnimation = false;
    }
}
