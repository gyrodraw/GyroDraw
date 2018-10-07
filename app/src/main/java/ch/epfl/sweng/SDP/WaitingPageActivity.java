package ch.epfl.sweng.SDP;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Locale;
import java.util.Random;

public class WaitingPageActivity extends AppCompatActivity {

    private static final String TAG = "WaitingPage";
    private static final String WORD_CHILDREN_DB_ID = "words";
    private static final int WORDS_COUNT = 5;

    private int usersReadyCount = 1;
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
        setGlobalVisibility(View.INVISIBLE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Generates two random numbers between 0 and WORDS_COUNT
                int numbers[] = generateTwoRandomNumbers();

                // Get the words corresponding to the random numbers
                String word1 = dataSnapshot.child(Integer.toString(numbers[0])).getValue(String.class);
                String word2 = dataSnapshot.child(Integer.toString(numbers[1])).getValue(String.class);

                // Display them on the buttons
                Button word1View = findViewById(R.id.buttonWord1);
                Button word2View = findViewById(R.id.buttonWord2);
                word1View.setText(word1);
                word2View.setText(word2);

                // Clear the progress dialog
                if (progressDialog.isShowing()) {
                    progressDialog.cancel();
                    setGlobalVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Does nothing for the moment
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.buttonWord1:
                if (checked) {
                    // Vote for word1 TODO
                }
                break;
            case R.id.buttonWord2:
                if (checked) {
                    // Vote for word2 TODO
                }
                break;
        }
    }

    /* Now it is public in order to use it as a button for testing, should be reverted to private
     * afterwards
     */
    public void incrementCount(View view) {
        ProgressBar progressBar = findViewById(R.id.usersProgressBar);
        progressBar.incrementProgressBy(1);
        ++usersReadyCount;
        TextView usersReady = findViewById(R.id.usersTextView);
        usersReady.setText(
                String.format(Locale.getDefault(), "%d/5 users ready", usersReadyCount));

        // We should probably check if the database is ready too
        if (usersReadyCount == 5) {
            Intent intent = new Intent(this, DrawingActivity.class);
            startActivity(intent);
        }
    }

    private int[] generateTwoRandomNumbers() {
        Random rand = new Random();
        int number1 = rand.nextInt(WORDS_COUNT);
        int number2 = number1;
        while(number1 == number2) {
            number2 = rand.nextInt(WORDS_COUNT);
        }

        return new int[] {number1, number2};
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
    }

    private void setGlobalVisibility(int visibility) {
        findViewById(R.id.radioGroup).setVisibility(visibility);
        findViewById(R.id.relativeLayout).setVisibility(visibility);
        findViewById(R.id.incrementButton).setVisibility(visibility);
    }

    // TODO
    private void getReadyUsers() {
        // Do stuff with the database
        // Should increment the counter with incrementCounter()
    }


}
