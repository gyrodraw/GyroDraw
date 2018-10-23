package ch.epfl.sweng.SDP;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import ch.epfl.sweng.SDP.auth.LoginActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;


import ch.epfl.sweng.SDP.Matchmaking.Matchmaker;

public class MainActivity extends Activity implements OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login_button).setOnClickListener(this);

        FirebaseApp.initializeApp(this);


        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Go to the home if the user has already logged in and created an account
        if (auth.getCurrentUser() != null && getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("hasAccount", false)) {
            launchActivity(HomeActivity.class);
            finish();
        }


    }

    @Override
    public void onClick(View view) {
        launchActivity(LoginActivity.class);
    }
}