package ch.epfl.sweng.SDP.game;

import static java.lang.String.format;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
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

public class VotingPageActivity extends AppCompatActivity {

    private static final int NUMBER_OF_DRAWINGS = 5;

    // For the moment it is defined as a constant
    private static final int WAITING_TIME = 20;
    private final String path = "mockRooms.ABCDE";
    private final String user = "aa";
    private DatabaseReference rankingRef;
    private DatabaseReference counterRef;
    private DatabaseReference endVotingRef;
    private DatabaseReference usersRef;
    private DatabaseReference endVotingUsersRef;

    private Bitmap[] drawings = new Bitmap[NUMBER_OF_DRAWINGS];
    private int count = 0;
    private int counter = 0; // for testing only

    private int[] ratings;
    private int ratingCounter = 0;
    // private String[] playersNames; to retrieve from the database

    private ProgressBar progressBar;

    private RatingBar ratingBar;

    private final OnSuccessListener<byte[]> listener = new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
            final int OFFSET = 0;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, OFFSET, bytes.length);
            storeBitmap(bitmap);
        }
    };

    private final ValueEventListener listenerCounter = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.getValue(Integer.class) != null) {
                Integer value = dataSnapshot.getValue(Integer.class);

                if(value != progressBar.getProgress()) {
                    progressBar.setProgress(WAITING_TIME - value);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Does nothing for the moment.
        }
    };

    private final ValueEventListener listenerEndVoting = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.getValue(Integer.class) != null) {
                Integer value = dataSnapshot.getValue(Integer.class);

                if(value == 1) {
                    // TODO create constants for states
                    usersRef.setValue(2);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Does nothing for the moment
        }
    };

    private final ValueEventListener listenerEndUsersVoting = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.getValue(Integer.class) != null) {
                Integer value = dataSnapshot.getValue(Integer.class);

                if(value == 1) {
                    // Start new activity
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Does nothing for the moment
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);
        ratingBar = findViewById(R.id.ratingBar);

        Database database = Database.getInstance();
        rankingRef = database
                .getReference(format(Locale.getDefault(), "rooms.%s.ranking", getRoomId()));
        counterRef = database.getReference(path + ".timer.observableTime");
        endVotingRef = database.getReference(path + ".timer.endVoting");
        usersRef = database.getReference(path + ".connectedUsers." + user);
        endVotingUsersRef = database.getReference(path + ".timer.usersEndVoting");

        String[] drawingsIds = new String[]{"1539331767.jpg", "1539297081.jpg", "1539331311.jpg",
                "1539331659.jpg"}; // hardcoded now, need to be given by the server/script
        retrieveDrawingsFromDatabaseStorage(drawingsIds);

        ratings = new int[NUMBER_OF_DRAWINGS];

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratings[ratingCounter] = (int) rating;
                sendRatingToDatabase();
            }
        });

        initProgressBar();
        counterRef.addValueEventListener(listenerCounter);
        endVotingRef.addValueEventListener(listenerEndVoting);
        endVotingUsersRef.addValueEventListener(listenerEndUsersVoting);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (rankingRef != null) {
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

    public void changeImage(View v) {
        changeDrawing(drawings[counter]);
        ++counter;
        ratingBar.setEnabled(true);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void changeDrawing(Bitmap img) {
        ImageView drawing = findViewById(R.id.drawing);
        drawing.setImageBitmap(img);
    }

    private void retrieveDrawingsFromDatabaseStorage(String[] drawingsIds) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference[] refs = new StorageReference[NUMBER_OF_DRAWINGS];
        final long ONE_MEGABYTE = 1024 * 1024;

        for (int i = 0; i < NUMBER_OF_DRAWINGS - 1;
                ++i) { // - 1 because I only have 4 images at the moment
            refs[i] = storage.getReference().child(drawingsIds[i]);
            refs[i].getBytes(ONE_MEGABYTE).addOnSuccessListener(listener);
            // need to retrieve the player name too
        }
    }

    private void storeBitmap(Bitmap bitmap) {
        drawings[count] = bitmap;
        ++count;
    }

    private void sendRatingToDatabase() {
        final int rating = ratings[ratingCounter];
        final DatabaseReference playerRating = rankingRef
                .child(format(Locale.getDefault(), "Player%d", ratingCounter));

        playerRating.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long value = dataSnapshot.getValue(Long.class);
                        if (value != null) {
                            playerRating.setValue(
                                    value.intValue()
                                            + rating); // the hardcoded player name should be replaced by the one retrieved from the database
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        ++ratingCounter;
        ratingBar.setEnabled(false);

    }

    private String getRoomId() {
        return "123456789"; // the room ID should be given by the server/script TODO
    }

    private void showFinalRanking() {
        rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // TODO
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showWinnerDrawing(Bitmap img) {
        ImageView drawing = findViewById(R.id.drawing);
        drawing.setImageBitmap(img);
        findViewById(R.id.ratingBar).setVisibility(View.GONE);
    }

}
