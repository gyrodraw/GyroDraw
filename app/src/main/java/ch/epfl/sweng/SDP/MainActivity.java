package ch.epfl.sweng.SDP;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.auth.LoginActivity;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.utils.GlideUtils;
import ch.epfl.sweng.SDP.utils.network.ConnectivityWrapper;

import static ch.epfl.sweng.SDP.firebase.Database.checkForDatabaseError;

/**
 * Class representing the first page shown to the user upon first app launch.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_loading_screen);

        GlideUtils.startDotsWaitingAnimation(this);
        GlideUtils.startBackgroundAnimation(this);

        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null && ConnectivityWrapper.isOnline(this)) {
            Database.getUserByEmail(auth.getCurrentUser().getEmail(),
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Go to the home if the user has already logged in
                                // and created an account
                                cloneAccountFromFirebase(dataSnapshot);

                                TextView errorMessage = findViewById(
                                        R.id.errorMessage);
                                errorMessage.setTypeface(typeMuro);

                                handleUserStatus(errorMessage);
                            } else {
                                displayMainLayout();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            checkForDatabaseError(databaseError);
                        }
                    });
        } else {
            displayMainLayout();
        }
    }

    private void displayMainLayout() {
        setContentView(R.layout.activity_main);
        GlideUtils.startBackgroundAnimation(this);

        findViewById(R.id.login_button).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ConnectivityWrapper.isOnline(getApplicationContext())) {
                            launchActivity(LoginActivity.class);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "No internet connection",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
