package ch.epfl.sweng.SDP.auth;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.LocalDbHandlerForAccount;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AccountCreationActivity extends Activity {

    private EditText usernameInput;
    private Button createAcc;
    private TextView usernameTaken;
    private String username;
    private View.OnClickListener createAccListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        usernameInput = this.findViewById(R.id.usernameInput);
        createAcc = this.findViewById(R.id.createAcc);
        createAcc.setOnClickListener(createAccListener);
        usernameTaken = this.findViewById(R.id.usernameTaken);
        createAccListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccClicked();
            }
        };
        createAcc.setOnClickListener(createAccListener);
    }

    /**
     * Gets called when user entered username and clicked on create account.
     */
    public void createAccClicked() {
        username = usernameInput.getText().toString();
        if (username.isEmpty()) {
            usernameTaken.setText(getString(R.string.usernameMustNotBeEmpty));
        } else {
            Account.createAccount(new ConstantsWrapper(), username);
            try {
                Database.INSTANCE.getReference("users").orderByChild("username").equalTo(username)
                        .addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    usernameTaken.setText(getString(R.string.usernameAlreadyTaken));
                                } else {
                                    Account.getInstance().registerAccount();
                                    getDefaultSharedPreferences(getThis()).edit()
                                            .putBoolean("hasAccount", true).apply();
                                    LocalDbHandlerForAccount localDB = new LocalDbHandlerForAccount(
                                            getApplicationContext(), null, 1);
                                    localDB.saveAccount(Account.getInstance());
                                    gotoHome();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });
            } catch (Exception exception) {
                usernameTaken.setText(exception.getMessage());
            }
        }
    }

    /**
     * Important for function above.
     *
     * @return this
     */
    private AccountCreationActivity getThis() {
        return this;
    }

    /**
     * Calls HomeActivity.
     */
    public void gotoHome() {
        launchActivity(HomeActivity.class);
        finish();
    }
}
