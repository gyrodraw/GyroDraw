package ch.epfl.sweng.SDP;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 42;
    private final List<IdpConfig> providers = Collections.singletonList(
            new GoogleBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createSignInIntent();
    }

    private void createSignInIntent() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.common_google_signin_btn_icon_dark) // custom logo here
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                assert response != null;

                // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); for Till

                if (response.isNewUser() || !getDefaultSharedPreferences(getApplicationContext())
                        .getBoolean("hasAccount", false)) {
                    // New user or a user who signed in but not created an account
                    Intent intent = new Intent(this, AccountCreationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User has an account
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                // Sign in failed
                TextView errorMessage = findViewById(R.id.error_message);

                // User pressed the back button
                if (response == null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
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
    }
}
