package ch.epfl.sweng.SDP.game;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.drawing.DrawingActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Random;

public class WaitingPageActivity extends Activity {

    private enum WordNumber {
        ONE, TWO
    }

    private static final String WORD_CHILDREN_DB_ID = "words";
    private static final int WORDS_COUNT = 5;
    private static final int NUMBER_OF_PLAYERS_NEEDED = 5;

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

            setVisibility(View.VISIBLE, R.id.buttonWord1, R.id.buttonWord2, R.id.radioGroup,
                    R.id.incrementButton, R.id.playersCounterText, R.id.imageWord1, R.id.imageWord2,
                    R.id.playersReadyText, R.id.voteText,  R.id.waitingAnimationSquare);

            setVisibility(View.GONE, R.id.waitingAnimationDots);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_waiting_page);
        Database database = Database.getInstance();

        wordsVotesRef = database.getReference(
                "rooms.432432432.words"); // need to be replaced with a search for a suitable room

        Glide.with(this).load(R.drawable.waiting_animation_square)
                .into((ImageView) findViewById(R.id.waitingAnimationSquare));
        Glide.with(this).load(R.drawable.waiting_animation_dots)
                .into((ImageView) findViewById(R.id.waitingAnimationDots));

        setVisibility(View.GONE, R.id.buttonWord1, R.id.buttonWord2, R.id.radioGroup,
                R.id.incrementButton, R.id.playersCounterText, R.id.imageWord1, R.id.imageWord2,
                R.id.playersReadyText, R.id.waitingAnimationSquare, R.id.voteText);

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.backgroundAnimation));

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        ((TextView) findViewById(R.id.playersReadyText)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.playersCounterText)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.buttonWord1)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.buttonWord2)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.voteText)).setTypeface(typeMuro);

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
     * Callback function called when a radio button is pressed.
     * Updates the votes in the database.
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

    // Vote for the specified word and update the database
    private void voteForWord(WordNumber wordNumber) {
        switch (wordNumber) {
            case ONE:
                word1Ref.setValue(++word1Votes);
                ((ImageView) findViewById(R.id.imageWord1))
                        .setImageResource(R.drawable.word_image_picked);
                break;
            case TWO:
                word2Ref.setValue(++word2Votes);
                ((ImageView) findViewById(R.id.imageWord2))
                        .setImageResource(R.drawable.word_image_picked);
                break;
            default:
        }
        animateWord1();
        animateWord2();
    }

    private void animateWord1() {
        final Animation pickWord1 = AnimationUtils.loadAnimation(this, R.anim.pick_word_1);
        pickWord1.setFillAfter(true);
        findViewById(R.id.imageWord1).startAnimation(pickWord1);
    }

    private void animateWord2() {
        final Animation pickWord2 = AnimationUtils.loadAnimation(this, R.anim.pick_word_2);
        pickWord2.setFillAfter(true);
        findViewById(R.id.imageWord2).startAnimation(pickWord2);
    }

    private void disableButtons() {
        Button b1 = findViewById(R.id.buttonWord1);
        b1.setEnabled(false);
        Button b2 = findViewById(R.id.buttonWord2);
        b2.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wordsVotesRef != null) {
            // need to keep the most voted word here, it has to
            // be done by the script not by this class
            wordsVotesRef.removeValue();
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
        ++usersReadyCount;
        TextView usersReady = findViewById(R.id.playersCounterText);
        usersReady.setText(
                String.format(Locale.getDefault(), "%d/%d", usersReadyCount,
                        NUMBER_OF_PLAYERS_NEEDED));

        // We should probably check if the database is ready too
        if (usersReadyCount == NUMBER_OF_PLAYERS_NEEDED) {
            launchActivity(DrawingActivity.class);
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

    /**
     * Getter of the number of votes for word 1.
     *
     * @return the number of votes for word 1
     */
    public int getWord1Votes() {
        return word1Votes;
    }

    /**
     * Getter of the number of votes for word 2.
     *
     * @return the number of votes for word 2
     */
    public int getWord2Votes() {
        return word2Votes;
    }

    // TODO
    private void getReadyUsers() {
        // Do stuff with the database
        // Should increment the counter with incrementCounter()
    }
}
