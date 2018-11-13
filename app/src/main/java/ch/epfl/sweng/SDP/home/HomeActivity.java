package ch.epfl.sweng.SDP.home;

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
import ch.epfl.sweng.SDP.game.LoadingScreenActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static ch.epfl.sweng.SDP.utils.ViewUtils.getMainAmplitude;
import static ch.epfl.sweng.SDP.utils.ViewUtils.getMainFrequency;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;
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
        setListener(trophiesButton, getMainAmplitude(), getMainFrequency());
        setListener(starsButton, getMainAmplitude(), getMainFrequency());
        setListener(leagueImage, getMainAmplitude(), LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, getMainAmplitude(), getMainFrequency());
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
                            Account.deleteAccount();
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
                    Toast.makeText(this, "No internet connection.",
                            Toast.LENGTH_LONG).show();
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
            case R.id.crossText:
                profileWindow.dismiss();
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
    }

    private void showPopup() {
        profileWindow.setContentView(R.layout.activity_pop_up);

        Account userAccount = Account.getInstance(this);

        this.setMuroFont();

        TextView gamesWonNumber = profileWindow.findViewById(R.id.gamesWonNumber);
        gamesWonNumber.setText(String.valueOf(userAccount.getMatchesWon()));
        TextView gamesLostNumber = profileWindow.findViewById(R.id.gamesLostNumber);
        gamesLostNumber.setText(String.valueOf(userAccount.getTotalMatches()));
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setText(String.valueOf(userAccount.getAverageRating()));
        TextView maxTrophiesNumber = profileWindow.findViewById(R.id.maxTrophiesNumber);
        maxTrophiesNumber.setText(String.valueOf(userAccount.getMaxTrophies()));
        TextView crossText = profileWindow.findViewById(R.id.crossText);
        setListener(crossText, getMainAmplitude(), getMainFrequency());
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        setListener(signOutButton, getMainAmplitude(), getMainFrequency());

        profileWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileWindow.show();
    }
}
