package ch.epfl.sweng.SDP;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.sweng.SDP.auth.LoginActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login_button).setOnClickListener(this);

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

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