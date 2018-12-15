package ch.epfl.sweng.SDP.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.NoBackPressActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.utils.GlideUtils;

import static ch.epfl.sweng.SDP.auth.LoginActivity.EMAIL;

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

        userEmail = getIntent().getStringExtra(EMAIL);

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
            FbDatabase.getUserByUsername(username, new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        usernameTaken.setText(getString(R.string.usernameTaken));
                    } else {
                        Account.createAccount(getApplicationContext(),
                                new ConstantsWrapper(), username, userEmail);
                        Account.getInstance(getApplicationContext()).registerAccount();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        launchActivity(HomeActivity.class);
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
}
