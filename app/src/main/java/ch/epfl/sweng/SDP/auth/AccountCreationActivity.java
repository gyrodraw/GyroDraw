package ch.epfl.sweng.SDP.auth;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

public class AccountCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
    }

    /**
     * Create an account and update the database.
     *
     * @param view the button clicked
     */
    public void createAccount(View view) {
        getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("hasAccount", true)
                .apply(); // Set and store hasAccount to true in preferences
        // TODO update database
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
