package ch.epfl.sweng.SDP.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.Constants;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AccountCreationActivity extends AppCompatActivity {
    private EditText usernameInput;
    private Button createAcc;
    private TextView usernameTaken;
    private String username;
    private Account account;
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
            usernameTaken.setText("Username must not be empty.");
        } else {
            checkIfNameIsTakenOrCreateAccount();
        }
    }

    /**
     * Checks if username is already taken.
     * Else creates a new account.
     */
    private void checkIfNameIsTakenOrCreateAccount(){
        Constants.usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            usernameTaken.setText("Username already taken.");
                        }
                        else {
                            createAccount();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        usernameTaken.setText(getString(R.string.database_error));
                    }
                });
    }

    /**
     * Creates a new account.
     */
    private void createAccount(){
        account = new Account(username);
        Constants.usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(account, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            usernameTaken.setText(getString(R.string.database_error));
                        }
                        else {
                            getDefaultSharedPreferences(getApplicationContext()).edit()
                                    .putBoolean("hasAccount", true).apply();
                            usernameTaken.setText("Success!");
                            gotoHome();
                        }
                    }
                });
    }

    /**
     * Calls HomeActivity.
     */
    private void gotoHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("account", this.account);
        startActivity(intent);
        finish();
    }
}