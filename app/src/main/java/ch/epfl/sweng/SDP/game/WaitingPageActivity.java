package ch.epfl.sweng.SDP.game;

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
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.Locale;
import java.util.Random;

public class WaitingPageActivity extends AppCompatActivity {

    private enum WordNumber {
        ONE, TWO
    }

    private static final String WORD_CHILDREN_DB_ID = "words";
    private static final int WORDS_COUNT = 5;
    private static final int STEP = 1;
    private static final int NUMBER_OF_PLAYERS_NEEDED = 5;

    private ProgressDialog progressDialog;

    private int usersReadyCount = 1;

    private DatabaseReference wordsVotesRef;

    private DatabaseReference word1Ref;
    private int word1Votes = 0;

    private DatabaseReference word2Ref;
    private int word2Votes = 0;

    private final ValueEventListener listenerWord1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long value = dataSnapshot.getValue(Long.class);
            if (value != null) {
                word1Votes = value.intValue();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    private final ValueEventListener listenerWord2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Long value = dataSnapshot.getValue(Long.class);
            if (value != null) {
                word2Votes = value.intValue();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    private final ValueEventListener listenerWords = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            // Generates two random numbers between 0 and WORDS_COUNT
            int[] numbers = generateTwoRandomNumbers();

            // Get the words corresponding to the random numbers and update database
            String word1 = dataSnapshot.child(Integer.toString(numbers[0]))
                    .getValue(String.class);
            if (word1 != null) {
                word1Ref = wordsVotesRef.child(word1);
                initRadioButton((Button) findViewById(R.id.buttonWord1), word1, word1Ref,
                        WordNumber.ONE);
            }

            String word2 = dataSnapshot.child(Integer.toString(numbers[1]))
                    .getValue(String.class);
            if (word2 != null) {
                word2Ref = wordsVotesRef.child(word2);
                initRadioButton((Button) findViewById(R.id.buttonWord2), word2, word2Ref,
                        WordNumber.TWO);
            }

            // Clear the progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
                setGlobalVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_page);
        Database database = Database.getInstance();

        wordsVotesRef = database.getReference(
                "rooms.432432432.words"); // need to be replaced with a search for a suitable room

        initProgressDialog();
        setGlobalVisibility(View.GONE);

        DatabaseReference wordsSelectionRef = database.getReference(WORD_CHILDREN_DB_ID);
        wordsSelectionRef.addListenerForSingleValueEvent(listenerWords);
    }

    private void initRadioButton(Button button, String childString,
            DatabaseReference dbRef, WordNumber wordNumber) {
        dbRef.setValue(0);
        dbRef.addListenerForSingleValueEvent(
                wordNumber == WordNumber.ONE ? listenerWord1 : listenerWord2);

        // Display the word on the button
        button.setText(childString);
    }

    /**
     * Callback function called when a radio button is pressed. Updates the votes in the database
     *
     * @param view View corresponding to the button clicked
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.buttonWord1:
                if (checked) {
                    // Vote for word1
                    voteForWord(WordNumber.ONE);
                    disableButtons();
                }
                break;
            case R.id.buttonWord2:
                if (checked) {
                    // Vote for word2
                    voteForWord(WordNumber.TWO);
                    disableButtons();
                }
                break;
            default:
        }
    }

    private void disableButtons() {
        Button b1 = findViewById(R.id.buttonWord1);
        b1.setEnabled(false);
        Button b2 = findViewById(R.id.buttonWord2);
        b2.setEnabled(false);
    }

    // Vote for the specified word and update the database
    private void voteForWord(WordNumber wordNumber) {
        switch (wordNumber) {
            case ONE:
                word1Ref.setValue(++word1Votes);
                break;
            case TWO:
                word2Ref.setValue(++word2Votes);
                break;
            default:
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wordsVotesRef != null) {
            wordsVotesRef.removeValue(); // need to keep the most voted word here
        }
    }

    // Now it is public in order to use it as a button for testing, should be reverted to private
    // afterwards

    /**
     * Increment the number of players logged in the room. This method exists only for testing
     * purposes.
     *
     * @param view Button that will increase the count when pressed
     */
    public void incrementCount(View view) {
        ProgressBar progressBar = findViewById(R.id.usersProgressBar);
        progressBar.incrementProgressBy(STEP);
        ++usersReadyCount;
        TextView usersReady = findViewById(R.id.usersTextView);
        usersReady.setText(
                String.format(Locale.getDefault(), "%d/%d users ready", usersReadyCount,
                        NUMBER_OF_PLAYERS_NEEDED));

        // We should probably check if the database is ready too
        if (usersReadyCount == NUMBER_OF_PLAYERS_NEEDED) {
            Intent intent = new Intent(this, DrawingActivity.class);
            startActivity(intent);
        }
    }

    private int[] generateTwoRandomNumbers() {
        Random rand = new Random();
        int number1 = rand.nextInt(WORDS_COUNT);
        int number2 = number1;
        while (number1 == number2) {
            number2 = rand.nextInt(WORDS_COUNT);
        }

        return new int[]{number1, number2};
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();
    }

    private void setGlobalVisibility(final int visibility) {
        findViewById(R.id.buttonWord1).setVisibility(visibility);
        findViewById(R.id.buttonWord2).setVisibility(visibility);
        findViewById(R.id.radioGroup).setVisibility(visibility);
        findViewById(R.id.relativeLayout).setVisibility(visibility);
        findViewById(R.id.incrementButton).setVisibility(visibility);
        findViewById(R.id.usersProgressBar).setVisibility(visibility);
        findViewById(R.id.usersTextView).setVisibility(visibility);
    }

    // TODO
    private void getReadyUsers() {
        // Do stuff with the database
        // Should increment the counter with incrementCounter()
    }


}
