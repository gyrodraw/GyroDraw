package ch.epfl.sweng.SDP.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VotingPageActivity extends Activity {

    private static final int NUMBER_OF_DRAWINGS = 5;
    private static final String TOP_ROOM_NODE_ID = "realRooms";
    private static final int TIME_FOR_VOTING = 24;

    private DatabaseReference rankingRef;
    private DatabaseReference stateRef;
    private DatabaseReference timerRef;
    private DatabaseReference usersRef;

    private Bitmap[] drawings = new Bitmap[NUMBER_OF_DRAWINGS];
    private short idsAndUsernamesCounter = 0;
    private short changeDrawingCounter = 0;

    private int[] ratings;
    private int previousRating = 0;

    private String[] playersNames = new String[NUMBER_OF_DRAWINGS];
    private String[] drawingsIds = new String[NUMBER_OF_DRAWINGS];

    private ImageView drawingView;
    private TextView playerNameView;
    private TextView timer;
    private RatingBar ratingBar;
    private StarAnimationView starsAnimation;

    private String roomID = "undefined";

    private static boolean enableAnimations = true;

    public int[] getRatings() {
        return ratings.clone();
    }

    protected final ValueEventListener listenerState = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer state = dataSnapshot.getValue(Integer.class);

            if (state != null) {
                GameStates stateEnum = GameStates.convertValueIntoState(state);
                switch (stateEnum) {
                    case END_VOTING_ACTIVITY:
                        // Start ranking activity
                        startRankingFragment();
                        break;
                    default:
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    protected final ValueEventListener listenerCounter = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);
            if (value != null) {
                timer.setText(String.valueOf(value % 5));

                if (value == TIME_FOR_VOTING) {
                    setLayoutToVisible();
                }

                if (value < TIME_FOR_VOTING && (value % 5) == 4 && value != 0) {
                    // Switch every 5 seconds
                    changeImage();
                }
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

        Intent intent = getIntent();
        roomID = intent.getStringExtra("RoomID");

        playerNameView = findViewById(R.id.playerNameView);
        drawingView = findViewById(R.id.drawing);
        timer = findViewById(R.id.timer);

        starsAnimation = findViewById(R.id.starsAnimation);
        ratingBar = findViewById(R.id.ratingBar);

        if (!enableAnimations) {
            setVisibility(View.GONE, R.id.starsAnimation);
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.background_animation)
                    .into((ImageView) findViewById(R.id.votingBackgroundAnimation));
            Glide.with(getApplicationContext()).load(R.drawable.waiting_animation_dots)
                    .into((ImageView) findViewById(R.id.waitingAnimationDots));
        }

        // Make the layout invisible until the drawings have been downloaded
        setVisibility(View.GONE, ratingBar, playerNameView,
                drawingView, timer, starsAnimation);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        playerNameView.setTypeface(typeMuro);
        timer.setTypeface(typeMuro);

        // Get the Database instance and the ranking reference
        final Database database = Database.INSTANCE;
        rankingRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".ranking");

        stateRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".state");
        stateRef.addValueEventListener(listenerState);

        timerRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".timer.observableTime");
        timerRef.addValueEventListener(listenerCounter);

        usersRef = database.getReference(TOP_ROOM_NODE_ID + "." + roomID + ".users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the players' ids and usernames
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    drawingsIds[idsAndUsernamesCounter] = snapshot.getKey();
                    playersNames[idsAndUsernamesCounter++] = (String) snapshot.getValue();
                }

                retrieveDrawingsFromDatabaseStorage();

                ratings = new int[NUMBER_OF_DRAWINGS];
                ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating,
                            boolean fromUser) {
                        ratingBar.setIsIndicator(true);
                        ratingBar.setAlpha(0.8f);
                        // Store the rating
                        ratings[changeDrawingCounter] = (int) rating;

                        // Send it to the database along with the corresponding player name
                        sendRatingToDatabase(playersNames[changeDrawingCounter]);
                    }
                });

                previousRating = 0;
                addStarAnimationListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (rankingRef != null) {
            // Remove the ranking reference in the database
            rankingRef.removeValue(); has to be decommented when a method for creating the entries
                                      corresponding to the ranking in the DB has been implemented
        }
        */
        if (roomID != null) {
            Matchmaker.getInstance(Account.getInstance(this))
                    .leaveRoom(roomID);
        }
        removeAllListeners();
        finish();
    }

    /**
     * Start the {@link HomeActivity} when the button is pressed. The button is used at the end of
     * the game to return to the home screen.
     *
     * @param view the view corresponding to the button pressed
     */
    public void startHomeActivity(View view) {
        // Remove the drawings from FirebaseStorage
        for (String id : drawingsIds) {
            // Remove this after testing
            if (id != null && !id.substring(0, 4).equals("user")) {
                FirebaseStorage.getInstance().getReference().child(id + ".jpg").delete();
            }
        }

        launchActivity(HomeActivity.class);
        finish();
    }

    /**
     * Switch the drawing when clicking the button.
     */
    public void changeImage() {
        ++changeDrawingCounter;
        previousRating = 0;
        changeDrawing(drawings[changeDrawingCounter], playersNames[changeDrawingCounter]);
        addStarAnimationListener();
        ratingBar.setRating(0f);
        ratingBar.setIsIndicator(false);
        ratingBar.setAlpha(1f);
    }

    private void setLayoutToVisible() {
        setVisibility(View.GONE, findViewById(R.id.waitingAnimationDots));
        setVisibility(View.VISIBLE, ratingBar, playerNameView,
                drawingView, timer, starsAnimation);
    }

    private void addStarAnimationListener() {
        final String playerName = playersNames[changeDrawingCounter];
        final DatabaseReference playerRating = rankingRef
                .child(playerName);

        playerRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the current rating
                Long value = dataSnapshot.getValue(Long.class);

                if (value != null) {
                    starsAnimation.addStars((int) (value - previousRating));
                    previousRating = value.intValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    // Change drawing and player name in the UI.
    private void changeDrawing(Bitmap img, String playerName) {
        drawingView.setImageBitmap(img);
        playerNameView.setText(playerName);
    }

    // Retrieve the drawings and store them in the drawings field.
    private void retrieveDrawingsFromDatabaseStorage() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference[] refs = new StorageReference[NUMBER_OF_DRAWINGS];
                final long ONE_MEGABYTE = 1024 * 1024; // Maximum image size

                for (int i = 0; i < NUMBER_OF_DRAWINGS; ++i) {
                    final String currentId = drawingsIds[i];
                    if (currentId != null) {
                        refs[i] = storage.getReference().child(currentId + ".jpg");

                        // Download the image
                        refs[i].getBytes(ONE_MEGABYTE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        final int OFFSET = 0;

                                        // Convert the image downloaded as byte[] to Bitmap
                                        Bitmap bitmap = BitmapFactory
                                                .decodeByteArray(bytes, OFFSET, bytes.length);

                                        // Store the image
                                        storeBitmap(bitmap, currentId);

                                        // Display the first drawing
                                        changeDrawing(drawings[0], playersNames[0]);
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void storeBitmap(Bitmap bitmap, String id) {
        int index;
        for (index = 0; index < drawingsIds.length; index++) {
            if (drawingsIds[index].equals(id)) {
                break;
            }
        }
        drawings[index] = bitmap;
    }

    /* public for testing only, the users in the database should be already sorted by their ranking
    before calling this */

    // Send "playerName" drawing's rating to the database.
    private void sendRatingToDatabase(String playerName) {
        final int rating = ratings[changeDrawingCounter];
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
    }

    private void startRankingFragment() {
        rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] ranking = new String[NUMBER_OF_DRAWINGS];
                short counter = 0;

                // Get the final ranking
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ranking[counter++] = snapshot.getKey();
                }

                // Prepare a Bundle for passing the ranking array to the fragment
                Bundle bundle = new Bundle();
                bundle.putString("roomID", roomID);

                // Clear the UI; buttonChangeImage and rankingButton need
                // to be removed after testing
                setVisibility(View.GONE, R.id.ratingBar, R.id.drawing,
                        R.id.playerNameView, R.id.timer);

                // Create and show the final ranking in the new fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.votingPageLayout,
                                RankingFragment.instantiate(getApplicationContext(),
                                        RankingFragment.class.getName(), bundle))
                        .addToBackStack(null).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Display the drawing of the winner.
     *
     * @param img Drawing of the winner
     * @param winnerName Name of the winner
     */
    public void showWinnerDrawing(Bitmap img, String winnerName) {
        changeDrawing(img, winnerName);
        // buttonChangeImage and rankingButton need to be removed after testing
        setVisibility(View.GONE, R.id.ratingBar);
    }

    private void removeAllListeners() {
        // Clear listeners
        stateRef.removeEventListener(listenerState);
        timerRef.removeEventListener(listenerCounter);
    }

    /**
     * Disables the background and stars animation. Call this method in every VotingPageActivity
     * test
     */
    public static void disableAnimations() {
        enableAnimations = false;
    }

    public short getChangeDrawingCounter() {
        return changeDrawingCounter;
    }

    @VisibleForTesting
    public void callShowWinnerDrawing(final Bitmap image, final String winner) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showWinnerDrawing(image, winner);
            }
        });
    }

    @VisibleForTesting
    public void callChangeImage() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeImage();
            }
        });
    }

    @VisibleForTesting
    public void callSetLayoutVisibility() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLayoutToVisible();
            }
        });
    }
}
