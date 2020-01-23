package ch.epfl.sweng.GyroDraw.auth;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.home.HomeActivity;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;

import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.EMAIL;

/**
 * Class representing the account creation page.
 */
public class AccountCreationActivity extends NoBackPressActivity {

    private EditText usernameInput;
    private TextView usernameTaken;
    private String userEmail;
    private UsernameInputWatcher usernameWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        userEmail = getIntent().getStringExtra(EMAIL);

        usernameInput = findViewById(R.id.usernameInput);
        usernameTaken = findViewById(R.id.usernameTaken);

        setTypeFace(typeMuro, findViewById(R.id.createAccount), findViewById(R.id.usernameInput),
                findViewById(R.id.usernameTaken));

        GlideUtils.startBackgroundAnimation(this);

        usernameWatcher = new UsernameInputWatcher(usernameTaken,
                (Button) findViewById(R.id.createAccount), getResources());
        ((TextView) findViewById(R.id.usernameInput)).addTextChangedListener(usernameWatcher);
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
                    usernameWatcher.disableButton();
                    if (snapshot.exists()) {
                        usernameTaken.setText(getString(R.string.usernameTaken));
                    } else {
                        createAccountAndRedirect(username);
                    }
                }
            });
        }
    }

    /**
     * Creates and registers an account with the given username and redirects the user to {@link
     * HomeActivity}.
     */
    @VisibleForTesting
    public void createAccountAndRedirect(String username) {
        Account.createAccount(getApplicationContext(),
                new ConstantsWrapper(), username, userEmail);
        Account.getInstance(getApplicationContext()).registerAccount();
        launchActivity(HomeActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
