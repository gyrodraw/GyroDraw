package ch.epfl.sweng.SDP.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import ch.epfl.sweng.SDP.NoBackPressActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.utils.GlideUtils;

import static ch.epfl.sweng.SDP.firebase.AccountAttributes.EMAIL;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.attributeToPath;

/**
 * Class representing the account creation page.
 */
public class AccountCreationActivity extends NoBackPressActivity {

    private EditText usernameInput;
    private TextView usernameTaken;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        userEmail = getIntent().getStringExtra(attributeToPath(EMAIL));

        usernameInput = findViewById(R.id.usernameInput);
        usernameTaken = findViewById(R.id.usernameTaken);

        setTypeFace(typeMuro, findViewById(R.id.createAccount), findViewById(R.id.usernameInput),
                findViewById(R.id.usernameTaken));

        GlideUtils.startBackgroundAnimation(this);

        ((TextView) findViewById(R.id.usernameInput)).addTextChangedListener(
                new UsernameInputWatcher((TextView) findViewById(R.id.usernameTaken),
                        (Button) findViewById(R.id.createAccount), getResources()));
    }

    /**
     * Gets called when user entered username and clicked on the create account button.
     */
    public void createAccountClicked(View view) {
        final String username = usernameInput.getText().toString().toUpperCase();

        if (!username.isEmpty()) {
            FbDatabase.getUserByUsername(username, new OnSuccessValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        usernameTaken.setText(getString(R.string.usernameTaken));
                    } else {
                        handleResponseAndRedirect(username);
                    }
                }
            });
        }
    }

    @VisibleForTesting
    public void handleResponseAndRedirect(String username) {
        Account.createAccount(getApplicationContext(),
                new ConstantsWrapper(), username, userEmail);
        Account.getInstance(getApplicationContext()).registerAccount();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        launchActivity(HomeActivity.class);
        finish();
    }
}
