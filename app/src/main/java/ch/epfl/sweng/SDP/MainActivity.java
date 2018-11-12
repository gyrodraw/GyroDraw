package ch.epfl.sweng.SDP;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import ch.epfl.sweng.SDP.auth.LoginActivity;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        Glide.with(this).load(R.drawable.waiting_animation_dots)
                .into((ImageView) findViewById(R.id.waitingAnimationDots));
        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.waitingBackgroundAnimation));

        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Go to the home if the user has already logged in and created an account
        if (auth.getCurrentUser() != null) {
            Database.INSTANCE.getReference("users").orderByChild("email")
                    .equalTo(auth.getCurrentUser().getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                cloneAccountFromFirebase(dataSnapshot);
                                launchActivity(HomeActivity.class);
                                finish();
                            } else {
                                displayMainLayout();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            displayMainLayout();
        }
    }

    private void displayMainLayout() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.login_button).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchActivity(LoginActivity.class);
                        finish();
                    }
                });
    }
}
