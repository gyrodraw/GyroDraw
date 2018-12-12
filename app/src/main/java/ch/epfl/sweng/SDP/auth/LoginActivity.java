package ch.epfl.sweng.SDP.auth;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.NoBackPressActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.utils.GlideUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.Collections;
import java.util.List;


/**
 * Class containing the methods used for the login. This activity is launched but not actually
 * displayed.
 */
public class LoginActivity extends NoBackPressActivity {

    private static final String TAG = "LoginActivity";
    private static final String EMAIL = "email";
    private static final int REQUEST_CODE_SIGN_IN = 42;

    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_loading_screen);

        createSignInIntent();

        errorMessage = findViewById(R.id.errorMessage);
        errorMessage.setTypeface(typeMuro);

        GlideUtils.startDotsWaitingAnimation(this);
        GlideUtils.startBackgroundAnimation(this);
    }

    private void createSignInIntent() {
        final List<IdpConfig> providers = Collections.singletonList(
                new GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.mipmap.ic_launcher_round) // custom logo here
                        .build(),
                REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (response == null) {
                launchActivity(MainActivity.class);
                finish();
                return;
            }

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                handleSuccessfulSignIn(response);
            } else {
                // Sign in failed
                handleFailedSignIn(response.getError().getErrorCode());
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    /**
     * Handles a successful sign in.
     *
     * @param response the response to process
     */
    private void handleSuccessfulSignIn(IdpResponse response) {
        assert response != null;

        final String email = response.getEmail();
        if (response.isNewUser()) {
            // New user
            Log.d(TAG, "New user");
            Intent intent = new Intent(this, AccountCreationActivity.class);
            intent.putExtra(EMAIL, email);
            startActivity(intent);
            finish();
        } else {
            Database.getUserByEmail(email, new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User already has an account on Firebase
                                Log.d(TAG, "User already has an account on Firebase");

                                cloneAccountFromFirebase(snapshot);

                                handleUserStatus(errorMessage);
                            } else {
                                // User signed in but not did not create an account
                                Log.d(TAG, "User signed in but not did not create an account");
                                Intent intent = new Intent(getApplicationContext(),
                                        AccountCreationActivity.class);
                                intent.putExtra(EMAIL, email);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
        }
    }

    /**
     * Handles a failed signIn.
     *
     * @param errorCode specifies the error that occurred
     */
    @VisibleForTesting
    public void handleFailedSignIn(int errorCode) {
        // No network
        if (errorCode == ErrorCodes.NO_NETWORK) {
            errorMessage.setText(getString(R.string.no_internet));
            errorMessage.setVisibility(VISIBLE);
            return;
        }

        // Unknown error
        errorMessage.setText(getString(R.string.unknown_error));
        errorMessage.setVisibility(VISIBLE);
    }
}
