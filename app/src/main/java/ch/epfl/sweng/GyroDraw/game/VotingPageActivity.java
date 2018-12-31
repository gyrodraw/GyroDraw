package ch.epfl.sweng.GyroDraw.game;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.FbStorage;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.home.HomeActivity;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForImages;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForImages;
import ch.epfl.sweng.GyroDraw.matchmaking.GameStates;
import ch.epfl.sweng.GyroDraw.matchmaking.Matchmaker;
import ch.epfl.sweng.GyroDraw.utils.BitmapManipulator;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.ImageSharer;
import ch.epfl.sweng.GyroDraw.utils.ImageStorageManager;
import ch.epfl.sweng.GyroDraw.utils.network.ConnectivityWrapper;

import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.RANKING;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.STATE;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.TIMER;
import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.USERS;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.ROOM_ID;

/**
 * Class representing the voting phase of an online game, where players vote for the drawings.
 */
public class VotingPageActivity extends NoBackPressActivity {

    private static final String TAG = "VotingPageActivity";

    private static final int NUMBER_OF_DRAWINGS = 5;

    private static boolean enableAnimations = true;

    private final String username = Account.getInstance(this).getUsername();

    private DatabaseReference rankingRef;

    private HashMap<String, Integer> rankingTable = new HashMap<>();

    private Bitmap[] drawings = new Bitmap[NUMBER_OF_DRAWINGS];
    private short idsAndUsernamesCounter = 0;
    private short changeDrawingCounter = 0;

    private int[] ratings;
    private int previousRating = 0;

    private final String[] playersNames = new String[NUMBER_OF_DRAWINGS];
    private final String[] drawingsIds = new String[NUMBER_OF_DRAWINGS];

    private ImageView drawingView;
    private TextView playerNameView;
    private TextView timer;
    private TextView disconnectedText;
    private RatingBar ratingBar;
    private StarAnimationView starsAnimation;

    private String roomId = "undefined";
    private boolean sharingMode = false;
    private boolean savingModeRequest = false;

    /**
     * Shares an image to Facebook.
     *
     * @param view the button which invoked this method.
     */
    public void shareImage(View view) {
        sharingMode = true;
        LocalDbForImages localDbHandler = new LocalDbHandlerForImages(this, null, 1);
        ImageSharer.getInstance(this).shareImageToFacebook(localDbHandler.getLatestBitmapFromDb());
    }

    /**
     * Saves an image to the disk.
     *
     * @param view the button which invoked this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveImage(View view) {
        if (ImageStorageManager.hasExternalWritePermissions(this)) {
            ImageStorageManager.saveImageFromDb(this);
        } else {
            savingModeRequest = true;
            ImageStorageManager.askForStoragePermission(this);
        }
    }

    /**
     * This listener is only visible to tests, else private.
     */
    @VisibleForTesting
    protected final ValueEventListener listenerState = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer state = dataSnapshot.getValue(Integer.class);

            if (state != null) {
                GameStates stateEnum = GameStates.convertValueIntoState(state);
                switch (stateEnum) {
                    case START_VOTING_ACTIVITY:
                        retrieveDrawingsFromDatabaseStorage();
                        break;
                    case END_VOTING_ACTIVITY:
                        ConnectivityWrapper.setOnlineStatusInGame(roomId,
                                Account.getInstance(getApplicationContext()).getUsername());
                        setAnimationWaitingBackground();
                        break;
                    case RANKING_FRAGMENT:
                        // Start ranking activity
                        setLayoutToVisible();
                        startRankingFragment();
                        break;
                    default:
                }
            }
        }
    };

    /**
     * This listener is only visible to tests, else private.
     */
    @VisibleForTesting
    protected final ValueEventListener listenerCounter = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer value = dataSnapshot.getValue(Integer.class);
            if (value != null) {
                timer.setText(value % 6 == 0 && value != 0 ? "5" :
                        String.valueOf(Math.max(value % 6 - 1, 0)));

                if (value != 30 && (value % 6) == 0 && value != 0) {
                    // Switch every 6 seconds
                    changeImage();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        ConnectivityWrapper.registerNetworkReceiver(this);

        Intent intent = getIntent();
        roomId = intent.getStringExtra(ROOM_ID);

        playerNameView = findViewById(R.id.playerNameView);
        drawingView = findViewById(R.id.drawing);
        timer = findViewById(R.id.timer);
        disconnectedText = findViewById(R.id.disconnectedText);

        starsAnimation = findViewById(R.id.starsAnimation);
        ratingBar = findViewById(R.id.ratingBar);

        setAnimationWaitingBackground();

        setTypeFace(typeMuro, playerNameView, timer, disconnectedText);

        // Get the ranking reference
        rankingRef = FbDatabase.getRoomAttributeReference(roomId, RANKING);
        FbDatabase.setListenerToRoomAttribute(roomId, RANKING,
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getValue(Integer.class) != null) {
                                rankingTable.put(ds.getKey(), ds.getValue(Integer.class));
                            }
                        }
                    }
                });

        FbDatabase.setListenerToRoomAttribute(roomId, STATE, listenerState);
        FbDatabase.setListenerToRoomAttribute(roomId, TIMER, listenerCounter);

        FbDatabase.getRoomAttribute(roomId, USERS,
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the players' ids and usernames
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            drawingsIds[idsAndUsernamesCounter] = snapshot.getKey();
                            playersNames[idsAndUsernamesCounter++] = (String) snapshot.getValue();
                        }

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
                });

        ConnectivityWrapper.setOnlineStatusInGame(roomId, Account.getInstance(this).getUsername());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sharingMode) {
            sharingMode = false;
            return;
        }

        if (savingModeRequest) {
            savingModeRequest = false;
            return;
        }

        Log.d(TAG, "Pausing activity");
        ConnectivityWrapper.unregisterNetworkReceiver(this);

        ImageSharer sharer = ImageSharer.getInstance();
        if (sharer != null) {
            sharer.setActivity(null);
        }

        removeAllListeners();
        finish();
    }

    /**
     * Starts the {@link HomeActivity} when the button is pressed. The button is used at the end of
     * the game to return to the home screen.
     */
    public void startHomeActivity() {
        // Remove the drawings from Firebase Storage
        for (String id : drawingsIds) {
            FbStorage.removeImage(id + ".jpg");
        }

        Log.d(TAG, "Starting home activity");

        if (ImageSharer.getInstance() != null) {
            ImageSharer.getInstance().setActivity(null);
        }

        launchActivity(HomeActivity.class);

        if (roomId != null && ConnectivityWrapper.isOnline(this)) {
            Matchmaker.getInstance(Account.getInstance(this)).leaveRoom(roomId);
        }

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Changes the drawing displayed.
     */
    public void changeImage() {
        ++changeDrawingCounter;
        previousRating = 0;

        String playerName = playersNames[changeDrawingCounter];
        changeDrawing(drawings[changeDrawingCounter], playerName);

        addStarAnimationListener();

        if (rankingTable.get(playerName) >= 0) {
            findViewById(R.id.disconnectedText).setVisibility(View.GONE);
            enableRatingBar(playerName);
        } else {
            ratingBar.setRating(0f);
            findViewById(R.id.disconnectedText).setVisibility(View.VISIBLE);
        }
    }

    private void enableRatingBar(String playerName) {
        if (playerName != null) {
            final boolean isCurrentPlayer = playerName.equals(username);

            // Enable the rating bar only if the image is not the player's one
            ratingBar.setRating(0f);
            ratingBar.setIsIndicator(isCurrentPlayer);
            ratingBar.setAlpha(isCurrentPlayer ? 0.5f : 1f);
        }
    }

    private void setLayoutToVisible() {
        setVisibility(View.GONE, findViewById(R.id.waitingAnimationDots));
        setVisibility(View.VISIBLE, ratingBar, playerNameView, drawingView, timer);
        if (enableAnimations) {
            setVisibility(View.VISIBLE, starsAnimation);
        }
    }

    private void setAnimationWaitingBackground() {
        if (enableAnimations) {
            GlideUtils.startBackgroundAnimation(this);
            GlideUtils.startDotsWaitingAnimation(this);
        }

        setVisibility(View.VISIBLE, findViewById(R.id.waitingAnimationDots));

        // Make the layout invisible until the drawings have been downloaded
        setVisibility(View.GONE, ratingBar, playerNameView,
                drawingView, timer, starsAnimation, disconnectedText);
    }

    private void addStarAnimationListener() {
        final String playerName = playersNames[changeDrawingCounter];
        if (playerName != null) {
            final DatabaseReference playerRating = rankingRef
                    .child(playerName);

            playerRating.addValueEventListener(new OnSuccessValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Get the current rating
                    Long value = dataSnapshot.getValue(Long.class);

                    if (value != null) {
                        starsAnimation.addStars((int) (value - previousRating));
                        previousRating = value.intValue();
                    }
                }
            });
        }
    }

    // Change drawing and player name in the UI.
    private void changeDrawing(Bitmap img, String playerName) {
        drawingView.setImageBitmap(img);
        playerNameView.setText(playerName);
    }

    // Retrieve the drawings and store them in the drawings field.
    private void retrieveDrawingsFromDatabaseStorage() {
        FbDatabase.getRoomAttribute(roomId, USERS,
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference[] refs = new StorageReference[NUMBER_OF_DRAWINGS];
                        final long FIFTY_KB = 51200; // Maximum image size

                        for (int i = 0; i < NUMBER_OF_DRAWINGS; ++i) {
                            final String currentId = drawingsIds[i];
                            if (currentId != null) {
                                if (currentId
                                        .equals(Account.getInstance(getApplicationContext())
                                                .getUserId())) {
                                    // Get the image from the local database instead
                                    LocalDbForImages localDbHandler = new LocalDbHandlerForImages(
                                            getApplicationContext(), null, 1);
                                    storeBitmap(localDbHandler.getLatestBitmapFromDb(), currentId);
                                } else {
                                    refs[i] = storage.getReference().child(currentId + ".jpg");

                                    // Download the image
                                    refs[i].getBytes(FIFTY_KB).addOnCompleteListener(
                                            new OnCompleteListener<byte[]>() {
                                                @Override
                                                public void onComplete(@NonNull Task<byte[]> task) {
                                                    Bitmap bitmap;
                                                    if (task.isSuccessful()) {
                                                        final int OFFSET = 0;

                                                        // Convert the image downloaded as byte[]
                                                        // to Bitmap
                                                        bitmap = BitmapManipulator
                                                                .decodeSampledBitmapFromByteArray(
                                                                        task.getResult(), OFFSET,
                                                                        task.getResult().length,
                                                                        drawingView.getMaxWidth(),
                                                                        drawingView.getMaxHeight());
                                                    } else {

                                                        // Use a default image if unsuccessful
                                                        bitmap = BitmapManipulator
                                                                .decodeSampledBitmapFromResource(
                                                                        getResources(),
                                                                        R.drawable.default_image,
                                                                        drawingView.getMaxWidth(),
                                                                        drawingView.getMaxHeight());
                                                    }

                                                    // Store the image
                                                    storeBitmap(bitmap, currentId);

                                                    // Display the first drawing
                                                    String playerName = playersNames[0];
                                                    changeDrawing(drawings[0], playerName);

                                                    // Enable the rating bar only if the image is
                                                    // not the player's one
                                                    enableRatingBar(playerName);

                                                    // Display the voting page layout
                                                    setLayoutToVisible();
                                                }
                                            });
                                }
                            }
                        }
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

    // Send "playerName" drawing's rating to the database.
    private void sendRatingToDatabase(String playerName) {
        final int rating = ratings[changeDrawingCounter];
        final DatabaseReference playerRating = rankingRef
                .child(playerName);

        playerRating.addListenerForSingleValueEvent(
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get the current rating
                        Long value = dataSnapshot.getValue(Long.class);

                        if (value != null) {
                            // Increment the current rating
                            playerRating.setValue(value.intValue() + rating);
                        }
                    }
                });
    }

    private void startRankingFragment() {
        // Clear the UI
        setVisibility(View.GONE, R.id.ratingBar, R.id.drawing,
                R.id.playerNameView, R.id.timer);

        RankingFragment fragment = new RankingFragment();
        // Create and show the final ranking in the new fragment
        fragment.putExtra(roomId, drawings, playersNames, this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.votingPageLayout, fragment)
                .addToBackStack(null).commit();
    }


    /**
     * Displays the drawing of the winner.
     *
     * @param img the drawing of the winner
     * @param winnerName the name of the winner
     */
    public void showWinnerDrawing(Bitmap img, String winnerName) {
        changeDrawing(img, winnerName);
        setVisibility(View.GONE, R.id.ratingBar);
    }

    private void removeAllListeners() {
        FbDatabase.removeListenerFromRoomAttribute(roomId, STATE, listenerState);
        FbDatabase.removeListenerFromRoomAttribute(roomId, TIMER, listenerCounter);
    }

    /**
     * Disables the background and stars animation. This method should be called in every
     * VotingPageActivity test.
     */
    @VisibleForTesting
    public static void disableAnimations() {
        enableAnimations = false;
    }

    /**
     * Returns a deep copy of ratings.
     */
    @VisibleForTesting
    public int[] getRatings() {
        return ratings.clone();
    }


    @VisibleForTesting
    public short getChangeDrawingCounter() {
        return changeDrawingCounter;
    }

    @VisibleForTesting
    public String[] getDrawingsIds() {
        return drawingsIds;
    }

    /**
     * Runs showWinnerDrawing on the UI thread.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void callShowWinnerDrawing(final Bitmap image, final String winner) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showWinnerDrawing(image, winner);
            }
        });
    }

    /**
     * Runs changeImage on the UI thread.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void callChangeImage() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeImage();
            }
        });
    }

    /**
     * Runs onDataChanged on UI thread.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void callOnStateChange(final DataSnapshot dataSnapshot) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listenerState.onDataChange(dataSnapshot);
            }
        });
    }

}
