package ch.epfl.sweng.SDP.auth;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.os.Bundle;
import android.view.View;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

public class AccountCreationActivity extends Activity {

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
        launchActivity(HomeActivity.class);
        finish();
    }

    public static Account getAccount() {
        return new Account(1); // need to be changed with the real account
    }
}
