package ch.epfl.sweng.SDP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class WaitingPageActivity extends AppCompatActivity {

    private static final String TAG = "WaitingPage";
    private static final String WORD_CHILDREN_DB_ID = "words";
    private static final int WORDS_COUNT = 5;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_page);
        mDatabase = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
        myRef = mDatabase.getReference(WORD_CHILDREN_DB_ID);
        initProgressDialog();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Random r = new Random();

                // Generates two random numbers between 0 and WORDS_COUNT
                int i1 = r.nextInt(WORDS_COUNT);
                int i2 = r.nextInt(WORDS_COUNT);

                // Get the words corresponding to the random numbers
                String word1 = dataSnapshot.child(Integer.toString(i1)).getValue(String.class);
                String word2 = dataSnapshot.child(Integer.toString(i2)).getValue(String.class);

                // Display them on the buttons
                Button word1View = findViewById(R.id.buttonWord1);
                Button word2View = findViewById(R.id.buttonWord2);
                word1View.setText(word1);
                word2View.setText(word2);

                // Clear the progress dialog
                progressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Does nothing for the moment
            }
        });
    }

    public void onClickWordsButtons(View view) {
        Intent intent = new Intent(this, DrawingActivity.class);
        startActivity(intent);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
    }


}
