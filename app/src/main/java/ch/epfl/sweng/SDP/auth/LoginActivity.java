package ch.epfl.sweng.SDP.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.database.DataSnapshot;

import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.NoBackPressActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbAuthentication;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.firebase.OnSuccesValueEventListener;
import ch.epfl.sweng.SDP.utils.GlideUtils;

import static android.view.View.VISIBLE;


/**
 * Class containing the methods used for the login. This activity is launched but not actually
 * displayed.
 */
public class LoginActivity extends NoBackPressActivity {

    public static final String EMAIL = "email";

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE_SIGN_IN = 42;

    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_loading_screen);

        FbAuthentication.signIn(this, REQUEST_CODE_SIGN_IN);

        errorMessage = findViewById(R.id.errorMessage);
        errorMessage.setTypeface(typeMuro);

        GlideUtils.startDotsWaitingAnimation(this);
        GlideUtils.startBackgroundAnimation(this);
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
            FbDatabase.getUserByEmail(email, new OnSuccesValueEventListener() {
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
