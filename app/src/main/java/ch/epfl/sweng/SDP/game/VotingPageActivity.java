package ch.epfl.sweng.SDP.game;

import static java.lang.String.format;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Locale;

public class VotingPageActivity extends Activity {

    private static final int NUMBER_OF_DRAWINGS = 5;

    // For the moment it is defined as a constant
    private static final int WAITING_TIME = 20;
    private static final String PATH = "mockRooms.ABCDE";
    private static final String USER = "aa"; // need to be replaced with the username

    private DatabaseReference rankingRef;
    private DatabaseReference counterRef;
    private DatabaseReference endTimeRef;
    private DatabaseReference usersRef;
    private DatabaseReference endVotingUsersRef;

    private Bitmap[] drawings = new Bitmap[NUMBER_OF_DRAWINGS];
    private short drawingDownloadCounter = 0;
    private short changeDrawingCounter = 0;

    private int[] ratings;
    private short ratingToSendCounter = 0;

    private String[] playersNames;

    private ProgressBar progressBar;
    private ImageView drawingView;
    private TextView playerNameView;

    private RatingBar ratingBar;
    private StarAnimationView mAnimationView;

    public int[] getRatings() {
        return ratings.clone();
    }

    private final ValueEventListener listenerCounter = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);
            if (value != null && value != progressBar.getProgress()) {
                progressBar.setProgress(WAITING_TIME - value);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    private final ValueEventListener listenerEndTime = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);

            // Check if the timer ended
            if (value != null && value == 1) {
                // TODO create constants for states
                usersRef.setValue(2);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    private final ValueEventListener listenerEndUsersVoting = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);

            // Check if all the players are ready for the next phase
            if (value != null && value == 1) {
                // Start new activity
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
        setContentView(R.layout.activity_voting_page);

        // Get the Database instance and the ranking reference
        Database database = Database.getInstance();
        rankingRef = database
                .getReference(format(Locale.getDefault(), "rooms.%s.ranking", getRoomId()));
        counterRef = database.getReference(PATH + ".timer.observableTime");
        endTimeRef = database.getReference(PATH + ".timer.endTime");
        endVotingUsersRef = database.getReference(PATH + ".timer.usersEndTime");

        usersRef = database.getReference(PATH + ".connectedUsers." + USER);
        counterRef.addValueEventListener(listenerCounter);
        endTimeRef.addValueEventListener(listenerEndTime);
        endVotingUsersRef.addValueEventListener(listenerEndUsersVoting);

        // Get the drawingIds; hardcoded now, need to be given by the server/script
        String[] drawingsIds = new String[]{"1539331767.jpg", "1539297081.jpg", "1539331311.jpg",
                "1539331659.jpg", "1539381600.jpg"};
        retrieveDrawingsFromDatabaseStorage(drawingsIds);

        // Get the players' names
        playersNames = new String[]{"Player0", "Player1", "Player2", "Player3",
                "Player4"}; // hardcoded now, need to be given by the
                            // server/script or retrieved from database

        ratings = new int[NUMBER_OF_DRAWINGS];
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Store the rating
                ratings[ratingToSendCounter] = (int) rating;

                //Send it to the database along with the corresponding player name
                sendRatingToDatabase(playersNames[ratingToSendCounter]);
            }
        });
        playerNameView = findViewById(R.id.playerNameView);
        drawingView = findViewById(R.id.drawing);

        // Make the drawingView and the playerNameView invisible
        // until the drawings have been downloaded
        setVisibility(View.INVISIBLE, drawingView, playerNameView);
        initProgressBar();

        mAnimationView = findViewById(R.id.starsAnimation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnimationView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAnimationView.pause();
        /*if (rankingRef != null) {
            // Remove the ranking reference in the database
            rankingRef.removeValue(); has to be decommented when a method for creating the entries
                                      corresponding to the ranking in the DB has been implemented
        }
        */
    }

    private void initProgressBar() {
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(20);
        progressBar.setProgress(0);
    }

    /**
     * Start the {@link HomeActivity} when the button is pressed. The button is used at the end of
     * the game to return to the home screen.
     *
     * @param view the view corresponding to the button pressed
     */
    public void startHomeActivity(View view) {
        launchActivity(HomeActivity.class);
        finish();
    }

    /**
     * Switch the drawing when clicking the button.
     * @param view View referencing the button
     */
    public void changeImage(View view) {
        ++changeDrawingCounter;
        changeDrawing(drawings[changeDrawingCounter], playersNames[changeDrawingCounter]);
        ratingBar.setEnabled(true);
    }

    // Change drawing and player name in the UI.
    private void changeDrawing(Bitmap img, String playerName) {
        drawingView.setImageBitmap(img);
        playerNameView.setText(playerName);
    }

    private String getRoomId() {
        return "123456789"; // the room ID should be given by the server/script
    }

    // Retrieve the drawings corresponding to the given ids from the
    // storage and store them in the drawings field.
    private void retrieveDrawingsFromDatabaseStorage(String[] drawingsIds) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference[] refs = new StorageReference[NUMBER_OF_DRAWINGS];
        final long ONE_MEGABYTE = 1024 * 1024; // Maximum image size

        for (int i = 0; i < NUMBER_OF_DRAWINGS; ++i) {
            refs[i] = storage.getReference().child(drawingsIds[i]);

            // Download the image
            refs[i].getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    final int OFFSET = 0;

                    // Convert the image downloaded as byte[] to Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, OFFSET, bytes.length);

                    // Store the image
                    storeBitmap(bitmap);

                    // Make the drawingView and the playerNameView visible
                    setVisibility(View.VISIBLE, drawingView, playerNameView);

                    // Display the first drawing
                    changeDrawing(drawings[0], playersNames[0]);
                }
            });
        }
    }

    private void storeBitmap(Bitmap bitmap) {
        drawings[drawingDownloadCounter] = bitmap;
        ++drawingDownloadCounter;
    }

    // Send "playerName" drawing's rating to the database.
    private void sendRatingToDatabase(String playerName) {
        final int rating = ratings[ratingToSendCounter];
        final DatabaseReference playerRating = rankingRef
                .child(playerName);

        playerRating.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the current rating
                        Long value = dataSnapshot.getValue(Long.class);

                        if (value != null) {
                            // Increment the current rating
                            playerRating.setValue(value.intValue() + rating);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

        ++ratingToSendCounter;

        // Disable the rating bar since the player has already voted for the current drawing
        ratingBar.setEnabled(false);
    }

    /* public for testing only, the users in the database should be already sorted by their ranking
    before calling this */

    /**
     * Show the final ranking in a new fragment.
     * @param view View referencing the button
     */
    public void showFinalRanking(View view) {
        rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] ranking = new String[NUMBER_OF_DRAWINGS];
                short counter = 0;

                // Get the final ranking
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ranking[counter] = snapshot.getKey();
                    ++counter;
                }

                // Prepare a Bundle for passing the ranking array to the fragment
                Bundle bundle = new Bundle();
                bundle.putStringArray("Ranking", ranking);

                // Clear the UI; buttonChangeImage and rankingButton need
                // to be removed after testing
                setVisibility(View.GONE, R.id.ratingBar, R.id.drawing, R.id.playerNameView,
                        R.id.buttonChangeImage, R.id.rankingButton);

                // Create and show the final ranking in the new fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.votingPageLayout,
                                RankingFragment.instantiate(getApplicationContext(),
                                        RankingFragment.class.getName(), bundle))
                        .addToBackStack(null).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //throw databaseError.toException();
            }
        });
    }

    private void showWinnerDrawing(Bitmap img, String winnerName) {
        changeDrawing(img, winnerName);
        // buttonChangeImage and rankingButton need to be removed after testing
        setVisibility(View.GONE, R.id.ratingBar, R.id.buttonChangeImage, R.id.rankingButton);
    }
}
