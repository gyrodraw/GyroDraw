package ch.epfl.sweng.SDP.home;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.game.WaitingPageActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends Activity {
    private Dialog profileWindow;

    private final String user = "aa";

    private static final String TAG = "HomeActivity";

    private static final int MAIN_FREQUENCY = 10;
    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;

    private static final double MAIN_AMPLITUDE = 0.1;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;

    private Database database;
    private DatabaseReference dbRef;
    private DatabaseReference dbRefTimer;

    // To be removed (for testing purposes only)
    private final ValueEventListener listenerAllReady = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long value = dataSnapshot.getValue(Long.class);
            if (value == 1) {
                launchActivity(VotingPageActivity.class);
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

        setContentView(R.layout.activity_home);
        profileWindow = new Dialog(this);

        // Testing method
        database = Database.getInstance();
        dbRef = database.getReference("mockRooms.ABCDE.connectedUsers");

        dbRefTimer = database.getReference("mockRooms.ABCDE.timer.startTimer");
        dbRefTimer.addValueEventListener(listenerAllReady);

        initUsersDatabase();

        final ImageView drawButton = findViewById(R.id.drawButton);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final ImageView trophiesButton = findViewById(R.id.trophiesButton);
        final ImageView starsButton = findViewById(R.id.starsButton);
        final ImageView leagueImage = findViewById(R.id.leagueImage);
        TextView leagueText = findViewById(R.id.leagueText);
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.homeBackgroundAnimation));
        leagueText.setTypeface(typeOptimus);
        usernameButton.setTypeface(typeMuro);
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
                            launchActivity(MainActivity.class);
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
                            launchActivity(MainActivity.class);
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

    private void setListener(final View view, final double amplitude, final int frequency) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int id = view.getId();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (id == R.id.drawButton) {
                            ((ImageView) view)
                                    .setImageResource(R.drawable.draw_button_pressed);
                        }
                        pressButton(view);
                        break;
                    case MotionEvent.ACTION_UP:
                        listenerEventSelector(view, id);
                        bounceButton(view, amplitude, frequency);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void listenerEventSelector(final View view, int id) {
        switch (id) {
            case R.id.drawButton:
                ((ImageView) view).setImageResource(R.drawable.draw_button);
                launchActivity(WaitingPageActivity.class);
                break;
            case R.id.usernameButton:
                showPopup();
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.deleteButton:
                delete();
                break;
            case R.id.crossText:
                profileWindow.dismiss();
                break;
            default:
        }
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

    // To remove, only for testing

    /**
     * Callback function when clicking the voting button.
     * Sets the user ready in the database.
     *
     * @param view View referencing the button
     */
    public void startVotingPage(View view) {
        // For testing purposes only
        dbRef.child(user).setValue(1);
        // Commented because of conflicts but can be still useful
        // launchActivity(VotingPageActivity.class);
    }

    // Testing purpose method
    private void initUsersDatabase() {
        dbRef.child(user).setValue(0);
        dbRef.child("bb").setValue(1);
        dbRef.child("cc").setValue(1);
        dbRef.child("dd").setValue(1);
        dbRef.child("ee").setValue(1);
        DatabaseReference timerRef = dbRef.getParent().child("timer");
        timerRef.child("endTime").setValue(0);
        timerRef.child("observableTime").setValue(0);
        timerRef.child("startTimer").setValue(0);
        timerRef.child("usersEndVoting").setValue(0);
    }
}