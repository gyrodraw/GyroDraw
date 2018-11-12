package ch.epfl.sweng.SDP.home;

import android.app.Dialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.CheckConnection;
import ch.epfl.sweng.SDP.game.LoadingScreenActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    public static final int MAIN_FREQUENCY = 10;
    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;
    public static final double MAIN_AMPLITUDE = 0.1;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;
    private static boolean enableBackgroundAnimation = true;

    private Dialog profileWindow;

    /**
     * Disables the background animation. Call this method in every HomeActivity test
     */
    public static void disableBackgroundAnimation() {
        enableBackgroundAnimation = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home);
        profileWindow = new Dialog(this);

        overridePendingTransition(0, 0);

        if (enableBackgroundAnimation) {
            Glide.with(this).load(R.drawable.background_animation)
                    .into((ImageView) findViewById(R.id.homeBackgroundAnimation));
        }

        LocalDbHandlerForAccount localDb = new LocalDbHandlerForAccount(this, null, 1);
        localDb.retrieveAccount(Account.getInstance(this));

        final ImageView drawButton = findViewById(R.id.drawButton);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final ImageView leaderboardButton = findViewById(R.id.leaderboardButton);
        final ImageView trophiesButton = findViewById(R.id.trophiesButton);
        final TextView trophiesCount = findViewById(R.id.trophiesCount);
        final ImageView starsButton = findViewById(R.id.starsButton);
        final TextView starsCount = findViewById(R.id.starsCount);
        final ImageView leagueImage = findViewById(R.id.leagueImage);

        usernameButton.setText(Account.getInstance(this).getUsername());
        trophiesCount.setText(String.valueOf(Account.getInstance(this).getTrophies()));
        starsCount.setText(String.valueOf(Account.getInstance(this).getStars()));

        TextView leagueText = findViewById(R.id.leagueText);
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        leagueText.setTypeface(typeOptimus);
        usernameButton.setTypeface(typeMuro);
        trophiesCount.setTypeface(typeMuro);
        starsCount.setTypeface(typeMuro);
        setListener(drawButton, DRAW_BUTTON_AMPLITUDE, DRAW_BUTTON_FREQUENCY);
        setListener(leaderboardButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(trophiesButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(starsButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        setListener(leagueImage, MAIN_AMPLITUDE, LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
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
        final Context context = this;
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
                        pressButton(view, context);
                        break;
                    case MotionEvent.ACTION_UP:
                        listenerEventSelector(view, id);
                        bounceButton(view, amplitude, frequency, context);
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
            case R.id.leaderboardButton:
                launchActivity(LeaderboardActivity.class);
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
            default:
        }
    }

    /**
     * Bounce the given view.
     *
     * @param view      the view
     * @param amplitude the amplitude of the bounce
     * @param frequency the frequency of the bounce
     * @param context   the context of the view
     */
    public static void bounceButton(final View view, double amplitude,
                                    int frequency, Context context) {
        assert amplitude != 0;
        final Animation bounce = AnimationUtils.loadAnimation(context, R.anim.bounce);
        BounceInterpolator interpolator = new BounceInterpolator(amplitude, frequency);
        bounce.setInterpolator(interpolator);
        view.startAnimation(bounce);
    }

    /**
     * Press the given view.
     *
     * @param view      the view
     * @param context   the context of the view
     */
    public static void pressButton(View view, Context context) {
        final Animation press = AnimationUtils.loadAnimation(context, R.anim.press);
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

        Account userAccount = Account.getInstance(this);

        this.setMuroFont();

        TextView gamesWonNumber = profileWindow.findViewById(R.id.gamesWonNumber);
        gamesWonNumber.setText(Integer.toString(userAccount.getMatchesWon()));
        TextView gamesLostNumber = profileWindow.findViewById(R.id.gamesLostNumber);
        gamesLostNumber.setText(Integer.toString(userAccount.getTotalMatches()));
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setText(Double.toString(userAccount.getAverageRating()));
        TextView maxTrophiesNumber = profileWindow.findViewById(R.id.maxTrophiesNumber);
        maxTrophiesNumber.setText(Integer.toString(userAccount.getMaxTrophies()));
        TextView crossText = profileWindow.findViewById(R.id.crossText);
        setListener(crossText, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        setListener(signOutButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);
        Button deleteButton = profileWindow.findViewById(R.id.deleteButton);
        setListener(deleteButton, MAIN_AMPLITUDE, MAIN_FREQUENCY);

        profileWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileWindow.show();
    }
}
