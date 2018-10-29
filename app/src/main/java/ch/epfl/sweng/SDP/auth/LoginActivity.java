package ch.epfl.sweng.SDP.auth;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collections;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createSignInIntent();
    }

    /**
     * Creates signInIntent.
     */
    private void createSignInIntent() {
        final List<IdpConfig> providers = Collections.singletonList(
                new GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.common_google_signin_btn_icon_dark) // custom logo here
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                assert response != null;
                handleSuccessfulSignIn(response);
            } else {
                // Sign in failed
                handleFailedSignIn(response);
            }
        }
    }

    /**
     * Handle successful signIn.
     * @param response contains the response.
     */
    private void handleSuccessfulSignIn(IdpResponse response) {
        assert response != null;

        if (response.isNewUser() || !getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("hasAccount", false)) {
            // New user or a user who signed in but not created an account
            launchActivity(AccountCreationActivity.class);
            finish();
        } else {
            // User has an account
            launchActivity(HomeActivity.class);
            finish();
        }
    }

    /**
     * Handle failed signIn.
     * @param response contains the response
     */
    private void handleFailedSignIn(IdpResponse response) {
        TextView errorMessage = findViewById(R.id.error_message);

        // User pressed the back button
        if (response == null) {
            launchActivity(MainActivity.class);
            finish();
            return;
        }

        // No network
        if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
            errorMessage.setText(getString(R.string.no_internet));
            errorMessage.setVisibility(View.VISIBLE);
            return;
        }

        // Unknown error
        errorMessage.setText(getString(R.string.unknown_error));
        errorMessage.setVisibility(View.VISIBLE);

        Log.e(TAG, "Sign-in error: ", response.getError());
    }
}