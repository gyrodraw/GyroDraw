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
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.CheckConnection;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.LoadingScreenActivity;
import ch.epfl.sweng.SDP.game.VotingPageActivity;

import ch.epfl.sweng.SDP.game.WaitingPageActivity;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;
import ch.epfl.sweng.SDP.game.drawing.DrawingGame;
import ch.epfl.sweng.SDP.game.drawing.DrawingGameWithTimer;

import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import com.bumptech.glide.Glide;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private static final int MAIN_FREQUENCY = 10;
    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;
    private static final double MAIN_AMPLITUDE = 0.1;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;
    private static boolean enableBackgroundAnimation = true;
    private final String user = "aa";

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
    private Dialog profileWindow;
    private DatabaseReference dbRef;
    private DatabaseReference dbRefTimer;

    /**
     * Disables the background animation. Call this method in every HomeActivity test
     */
    public static void disableBackgroundAnimation() {
        enableBackgroundAnimation = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profileWindow = new Dialog(this);

        LocalDbHandlerForAccount localDb = new LocalDbHandlerForAccount(this, null, 1);
        localDb.retrieveAccount(Account.getInstance(this));

        // Testing method
        Database database = Database.INSTANCE;
        dbRef = database.getReference("mockRooms.ABCDE.connectedUsers");

        dbRefTimer = database.getReference("mockRooms.ABCDE.timer.startTimer");
        dbRefTimer.addValueEventListener(listenerAllReady);
        initUsersDatabase();

        final ImageView drawButton = findViewById(R.id.drawButton);
        final Button offlineButton = findViewById(R.id.playoffline);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final ImageView trophiesButton = findViewById(R.id.trophiesButton);
        final ImageView starsButton = findViewById(R.id.starsButton);
        final ImageView leagueImage = findViewById(R.id.leagueImage);

        TextView leagueText = findViewById(R.id.leagueText);
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        if (enableBackgroundAnimation) {
            Glide.with(this).load(R.drawable.background_animation)
                    .into((ImageView) findViewById(R.id.homeBackgroundAnimation));
        }
        leagueText.setTypeface(typeOptimus);
        usernameButton.setTypeface(typeMuro);
        setListener(drawButton, DRAW_BUTTON_AMPLITUDE, DRAW_BUTTON_FREQUENCY);
        setListener(trophiesButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(starsButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(leagueImage, MAIN_AMPLITUDE, LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(offlineButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
    }

    // Launch the LeaguesActivity.
    private void showLeagues() {
        launchActivity(LeaguesActivity.class);
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
                if (CheckConnection.isOnline(this)) {
                    ((ImageView) view).setImageResource(R.drawable.draw_button);
                    launchActivity(LoadingScreenActivity.class);
                } else {
                    Toast.makeText(this, "No internet connection.", Toast.LENGTH_LONG);
                }

                break;
            case R.id.leagueImage:
                showLeagues();
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
            case R.id.playoffline:
                launchActivity(DrawingGame.class);
                break;
            default:
        }
    }

    private void bounceButton(final View view, double amplitude, int frequency) {
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

    private void setMuroFont() {
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        TextView profileTextView = profileWindow.findViewById(R.id.profileText);
        profileTextView.setTypeface(typeMuro);
        TextView gamesWonText = profileWindow.findViewById(R.id.gamesWonText);
        gamesWonText.setTypeface(typeMuro);
        TextView gamesLostText = profileWindow.findViewById(R.id.gamesLostText);
        gamesLostText.setTypeface(typeMuro);
        TextView averageStarsText = profileWindow.findViewById(R.id.averageStarsText);
        averageStarsText.setTypeface(typeMuro);
        TextView maxTrophiesText = profileWindow.findViewById(R.id.maxTrophiesText);
        maxTrophiesText.setTypeface(typeMuro);
        TextView gamesWonNumber = profileWindow.findViewById(R.id.gamesWonNumber);
        gamesWonNumber.setTypeface(typeMuro);
        TextView gamesLostNumber = profileWindow.findViewById(R.id.gamesLostNumber);
        gamesLostNumber.setTypeface(typeMuro);
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setTypeface(typeMuro);
        TextView maxTrophiesNumber = profileWindow.findViewById(R.id.maxTrophiesNumber);
        maxTrophiesNumber.setTypeface(typeMuro);
        TextView crossText = profileWindow.findViewById(R.id.crossText);
        crossText.setTypeface(typeMuro);
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        signOutButton.setTypeface(typeMuro);
        Button deleteButton = profileWindow.findViewById(R.id.deleteButton);
        deleteButton.setTypeface(typeMuro);
    }

    private void showPopup() {
        profileWindow.setContentView(R.layout.activity_pop_up);

        Account userAccount = new Account(1,2,3,4);

        this.setMuroFont();

        TextView gamesWonNumber = profileWindow.findViewById(R.id.gamesWonNumber);
        gamesWonNumber.setText(Integer.toString(userAccount.getMatchesWon()));
        TextView gamesLostNumber = profileWindow.findViewById(R.id.gamesLostNumber);
        gamesLostNumber.setText(Integer.toString(userAccount.getMatchesLost()));
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setText(Double.toString(userAccount.getAverageRating()));
        TextView maxTrophiesNumber = profileWindow.findViewById(R.id.maxTrophiesNumber);
        maxTrophiesNumber.setText(Integer.toString(userAccount.getTrophies()));
        TextView crossText = profileWindow.findViewById(R.id.crossText);
        setListener(crossText, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        setListener(signOutButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        Button deleteButton = profileWindow.findViewById(R.id.deleteButton);
        setListener(deleteButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);

        profileWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileWindow.show();
    }

    /**
     * Callback function when clicking the voting button. Sets the user ready in the database.
     *
     * @param view View referencing the button
     */
    public void startVotingPage(View view) {
        // For testing purposes only
        dbRef.child(user).setValue(1);
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
