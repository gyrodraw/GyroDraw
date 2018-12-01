package ch.epfl.sweng.SDP.auth;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;

public class AccountCreationActivity extends Activity {

    private EditText usernameInput;
    private TextView usernameTaken;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        userEmail = getIntent().getStringExtra("email");
        overridePendingTransition(0, 0);

        usernameInput = findViewById(R.id.usernameInput);
        usernameTaken = findViewById(R.id.usernameTaken);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        ((TextView) findViewById(R.id.createAccount)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.usernameInput)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.usernameTaken)).setTypeface(typeMuro);

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));
    }

    /**
     * Gets called when user entered username and clicked on create account.
     */
    public void createAccClicked(View view) {
        final String username = usernameInput.getText().toString().toUpperCase();
        if (username.isEmpty()) {
            usernameTaken.setText(getString(R.string.usernameMustNotBeEmpty));
        } else {
            Database.getReference("users").orderByChild("username").equalTo(username)
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
