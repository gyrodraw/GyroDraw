package ch.epfl.sweng.SDP.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AccountCreationActivity extends Activity {

    private EditText usernameInput;
    private TextView usernameTaken;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        userEmail = getIntent().getStringExtra("email");

        usernameInput = findViewById(R.id.usernameInput);
        usernameTaken = findViewById(R.id.usernameTaken);

        Button createAcc = findViewById(R.id.createAcc);
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccClicked();
            }
        });
    }

    /**
     * Gets called when user entered username and clicked on create account.
     */
    public void createAccClicked() {
        final String username = usernameInput.getText().toString();
        if (username.isEmpty()) {
            usernameTaken.setText(getString(R.string.usernameMustNotBeEmpty));
        } else {
            Database.INSTANCE.getReference("users").orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                usernameTaken.setText(getString(R.string.usernameTaken));
                            } else {
                                Account.createAccount(getApplicationContext(),
                                        new ConstantsWrapper(), username, userEmail);
                                Account.getInstance(getApplicationContext()).registerAccount();
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
