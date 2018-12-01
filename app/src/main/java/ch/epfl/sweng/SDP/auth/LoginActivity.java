package ch.epfl.sweng.SDP.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.bumptech.glide.Glide;
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

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(0, 0);

        createSignInIntent();

        Glide.with(this).load(R.drawable.waiting_animation_dots)
                .into((ImageView) findViewById(R.id.waitingAnimationDots));
        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));
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
                        .setTheme(R.style.LoginTheme)
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

        final String email = response.getEmail();
        if (response.isNewUser()) {
            // New user
            Log.d(TAG, "New user");
            Intent intent = new Intent(this, AccountCreationActivity.class);
            intent.putExtra(EMAIL, email);
            startActivity(intent);
            finish();
        } else {
            Database.getReference("users").orderByChild(EMAIL).equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User already has an account on Firebase
                                Log.d(TAG, "User already has an account on Firebase");
                                cloneAccountFromFirebase(snapshot);
                                launchActivity(HomeActivity.class);
                                finish();
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
