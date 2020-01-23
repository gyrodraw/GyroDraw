package ch.epfl.sweng.GyroDraw.game;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.game.drawing.DrawingOnlineActivity;
import ch.epfl.sweng.GyroDraw.game.drawing.DrawingOnlineItemsActivity;
import ch.epfl.sweng.GyroDraw.matchmaking.GameStates;
import ch.epfl.sweng.GyroDraw.matchmaking.Matchmaker;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils;
import ch.epfl.sweng.GyroDraw.utils.network.ConnectivityWrapper;
import ch.epfl.sweng.GyroDraw.utils.network.NetworkStatusHandler;

import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.STATE;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.TIMER;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.USERS;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.WORDS;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.ROOM_ID;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.WORD_1;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.WORD_2;
import static ch.epfl.sweng.GyroDraw.home.HomeActivity.GAME_MODE;
import static java.lang.String.format;

/**
 * Class representing the first phase of an online game: a waiting page in which players can vote
 * for the word to draw.
 */
public class WaitingPageActivity extends NoBackPressActivity {

    public static final String WINNING_WORD = "winningWord";

    private static final String TAG = "WaitingPageActivity";

    private static boolean enableSquareAnimation = true;

    private enum WordNumber {
        ONE, TWO
    }

    private String roomID = null;

    private int gameMode;

    private boolean isDrawingActivityLaunched = false;

    private boolean hasVoted = false;
    private boolean isWord1Voted = false;

    private DatabaseReference word1Ref;
    private int word1Votes = 0;

    private DatabaseReference word2Ref;
    private int word2Votes = 0;

    private String word1 = null;
    private String word2 = null;
    private String winningWord = null;

    @VisibleForTesting
    protected final ValueEventListener listenerTimer = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);

            if (value != null) {
                ((TextView) findViewById(R.id.waitingTime))
                        .setText(String.valueOf(value));
            }
        }
    };

    @VisibleForTesting
    protected final ValueEventListener listenerState = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer state = dataSnapshot.getValue(Integer.class);
            if (state != null) {
                GameStates stateEnum = GameStates.convertValueIntoState(state);
                switch (stateEnum) {
                    case HOME_STATE:
                        findViewById(R.id.waitingTime).setVisibility(View.GONE);
                        findViewById(R.id.leaveButton).setVisibility(View.VISIBLE);
                        break;
                    case CHOOSE_WORDS_TIMER_START:
                        findViewById(R.id.waitingTime).setVisibility(View.VISIBLE);
                        findViewById(R.id.leaveButton).setVisibility(View.GONE);
                        FbDatabase.setListenerToRoomAttribute(roomID, TIMER, listenerTimer);
                        break;
                    case START_DRAWING_ACTIVITY:
                        FbDatabase.removeListenerFromRoomAttribute(roomID, TIMER, listenerTimer);
                        launchDrawingActivity(gameMode);
                        break;
                    default:
                }
            }
        }
    };

    @VisibleForTesting
    protected final ValueEventListener listenerWord1 = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                Long value = dataSnapshot.getValue(Long.class);
                if (value != null) {
                    word1Votes = value.intValue();
                    winningWord = getWinningWord(word1Votes, word2Votes,
                            new String[]{word1, word2});
                }
            } catch (Exception e) {
                Log.e(TAG, "Value is not ready");
            }
        }
    };

    @VisibleForTesting
    protected final ValueEventListener listenerWord2 = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                Long value = dataSnapshot.getValue(Long.class);
                if (value != null) {
                    word2Votes = value.intValue();
                    winningWord = getWinningWord(word1Votes, word2Votes,
                            new String[]{word1, word2});
                }
            } catch (Exception e) {
                Log.e(TAG, "Value is not ready");
            }
        }
    };

    @VisibleForTesting
    protected final ValueEventListener listenerCountUsers = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            long usersCount = dataSnapshot.getChildrenCount();
            ((TextView) findViewById(R.id.playersCounterText)).setText(
                    format("%s/5", String.valueOf(usersCount)));

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_waiting_page);

        ConnectivityWrapper.registerNetworkReceiver(this);

        Intent intent = getIntent();
        roomID = intent.getStringExtra(ROOM_ID);
        word1 = intent.getStringExtra(WORD_1);
        word2 = intent.getStringExtra(WORD_2);
        gameMode = intent.getIntExtra(GAME_MODE, 0);

        if (enableSquareAnimation) {
            GlideUtils.startSquareWaitingAnimation(this);
            GlideUtils.startBackgroundAnimation(this);
        }

        word1Ref = FbDatabase.getRoomAttributeReference(roomID, WORDS).child(word1);
        word2Ref = FbDatabase.getRoomAttributeReference(roomID, WORDS).child(word2);

        FbDatabase.setListenerToRoomAttribute(roomID, STATE, listenerState);

        FbDatabase.setListenerToRoomAttribute(roomID, USERS, listenerCountUsers);

        initRadioButton((Button) findViewById(R.id.buttonWord1), word1, word1Ref,
                WordNumber.ONE);
        initRadioButton((Button) findViewById(R.id.buttonWord2), word2, word2Ref,
                WordNumber.TWO);

        setTypeFace(typeMuro, findViewById(R.id.playersReadyText),
                findViewById(R.id.playersCounterText), findViewById(R.id.buttonWord1),
                findViewById(R.id.buttonWord2), findViewById(R.id.voteText),
                findViewById(R.id.waitingTime), findViewById(R.id.leaveButton));

        LayoutUtils.setFadingExitListener(findViewById(R.id.leaveButton), this);

        findViewById(R.id.waitingTime).setVisibility(View.GONE);
    }

    private void launchDrawingActivity(int gameMode) {
        Intent intent = new Intent(getApplicationContext(),
                gameMode == 0 ? DrawingOnlineActivity.class : DrawingOnlineItemsActivity.class);

        isDrawingActivityLaunched = true;

        intent.putExtra(ROOM_ID, roomID);
        intent.putExtra(WINNING_WORD, winningWord);
        startActivity(intent);
    }

    private void initRadioButton(Button button, String childString,
                                 DatabaseReference dbRef, WordNumber wordNumber) {
        dbRef.addValueEventListener(
                wordNumber == WordNumber.ONE ? listenerWord1 : listenerWord2);

        // Display the word on the button
        button.setText(childString);
    }

    /**
     * Gets the words that received the larger amount of votes.
     *
     * @param word1Votes Votes for the word 1
     * @param word2Votes Votes for the word 2
     * @param words      Array containing the words
     * @return Returns the winning word.
     */
    static String getWinningWord(int word1Votes, int word2Votes, String[] words) {
        String winningWord = words[1];
        if (word1Votes >= word2Votes) {
            winningWord = words[0];
        }
        return winningWord;
    }

    /**
     * Callback function called when a radio button is pressed. Updates the votes in the database.
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
                    hasVoted = true;
                    isWord1Voted = true;
                    voteForWord(WordNumber.ONE);
                    disableButtons();
                }
                break;
            case R.id.buttonWord2:
                if (checked) {
                    // Vote for word2
                    hasVoted = true;
                    isWord1Voted = false;
                    voteForWord(WordNumber.TWO);
                    disableButtons();
                }
                break;
            default:
        }
    }

    // Vote for the specified word and update the database
    private void voteForWord(WordNumber wordNumber) {
        ImageView imageWord1 = findViewById(R.id.imageWord1);
        ImageView imageWord2 = findViewById(R.id.imageWord2);
        switch (wordNumber) {
            case ONE:
                word1Ref.setValue(++word1Votes);
                imageWord1.setImageResource(R.drawable.word_image_picked);
                break;
            case TWO:
                word2Ref.setValue(++word2Votes);
                imageWord2.setImageResource(R.drawable.word_image_picked);
                break;
            default:
        }
        animateWord(imageWord1, R.anim.pick_word_1);
        animateWord(imageWord2, R.anim.pick_word_2);
    }

    private void animateWord(ImageView imageWord, int animId) {
        final Animation pickWord = AnimationUtils.loadAnimation(this, animId);
        pickWord.setFillAfter(true);
        imageWord.startAnimation(pickWord);
    }

    private void disableButtons() {
        Button b1 = findViewById(R.id.buttonWord1);
        b1.setEnabled(false);
        Button b2 = findViewById(R.id.buttonWord2);
        b2.setEnabled(false);
    }

    /**
     * Getter of the number of votes for word 1.
     *
     * @return the number of votes for word 1
     */
    @VisibleForTesting
    public int getWord1Votes() {
        return word1Votes;
    }

    @VisibleForTesting
    public void setWord1Votes(int votes) {
        word1Votes = votes;
    }

    /**
     * Getter of the number of votes for word 2.
     *
     * @return the number of votes for word 2
     */
    @VisibleForTesting
    public int getWord2Votes() {
        return word2Votes;
    }

    @VisibleForTesting
    public void setWord2Votes(int votes) {
        word2Votes = votes;
    }

    private void removeAllListeners() {
        try {
            FbDatabase.removeListenerFromRoomAttribute(roomID, TIMER, listenerTimer);
        } catch (NullPointerException e) {
            Log.e(TAG, "Timer listener not initialized");
        }

        FbDatabase.removeListenerFromRoomAttribute(roomID, STATE, listenerState);
        word1Ref.removeEventListener(listenerWord1);
        word2Ref.removeEventListener(listenerWord2);
    }

    @VisibleForTesting
    public static void disableAnimations() {
        enableSquareAnimation = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectivityWrapper.unregisterNetworkReceiver(this);

        // Does not leave the room if the activity is stopped because
        // drawing activity is launched.
        if (!isDrawingActivityLaunched && !NetworkStatusHandler.getHasLeft()) {
            Matchmaker.getInstance(Account.getInstance(this)).leaveRoom(roomID);
            if (hasVoted) {
                String wordVoted = isWord1Voted ? word1 : word2;
                FbDatabase.getRoomAttributeReference(roomID, WORDS).child(wordVoted);
                DatabaseReference wordRef = FbDatabase.getRoomAttributeReference(roomID, WORDS)
                        .child(wordVoted);
                removeVote(wordRef);
            }
        }

        NetworkStatusHandler.setHasLeft(false);
        removeAllListeners();
        finish();
    }

    private void removeVote(final DatabaseReference wordRef) {
        wordRef.addListenerForSingleValueEvent(new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value != null) {
                    wordRef.setValue(--value);
                }
            }
        });
    }

    /**
     * Method that calls onDataChange on the UI thread.
     *
     * @param dataSnapshot Snapshot of the database (mock snapshot in this case).
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void callOnDataChange(final DataSnapshot dataSnapshot) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listenerTimer.onDataChange(dataSnapshot);
            }
        });
    }
}
