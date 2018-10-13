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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
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
    private DatabaseReference rankingRef;

    private Bitmap[] drawings = new Bitmap[NUMBER_OF_DRAWINGS];
    private short drawingDownloadCounter = 0;
    private short changeDrawingCounter = 0;

    private int[] ratings;
    private short ratingToSendCounter = 0;

    private String[] playersNames;

    private ImageView drawing;
    private RatingBar ratingBar;

    private final OnSuccessListener<byte[]> listener = new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
            final int OFFSET = 0;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, OFFSET, bytes.length);
            storeBitmap(bitmap);
            drawing.setVisibility(View.VISIBLE);
            changeDrawing(drawings[0], playersNames[0]);
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

        String[] drawingsIds = new String[]{"1539331767.jpg", "1539297081.jpg", "1539331311.jpg",
                "1539331659.jpg"}; // hardcoded now, need to be given by the server/script

        playersNames = new String[]{"Player1", "Player2", "Player3",
                "Player4"}; // hardcoded now, need to be given by the server/script or retrieved from database

        drawing = findViewById(R.id.drawing);
        drawing.setVisibility(View.INVISIBLE);
        retrieveDrawingsFromDatabaseStorage(drawingsIds);

        ratings = new int[NUMBER_OF_DRAWINGS];

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratings[ratingToSendCounter] = (int) rating;
                sendRatingToDatabase(playersNames[ratingToSendCounter]);
            }
        });
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

    public void startHomeActivity(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // public for testing only
    public void changeImage(View v) {
        ++changeDrawingCounter;
        changeDrawing(drawings[changeDrawingCounter], playersNames[changeDrawingCounter]);
        ratingBar.setEnabled(true);
    }

    private void changeDrawing(Bitmap img, String playerName) {
        drawing.setImageBitmap(img);
        TextView playerNameView = findViewById(R.id.playerNameView);
        playerNameView.setText(playerName);
    }


    private String getRoomId() {
        return "123456789"; // the room ID should be given by the server/script
    }

    private void retrieveDrawingsFromDatabaseStorage(String[] drawingsIds) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference[] refs = new StorageReference[NUMBER_OF_DRAWINGS];
        final long ONE_MEGABYTE = 1024 * 1024;

        for (int i = 0; i < NUMBER_OF_DRAWINGS - 1;
                ++i) { // - 1 because I only have 4 images at the moment
            refs[i] = storage.getReference().child(drawingsIds[i]);
            refs[i].getBytes(ONE_MEGABYTE).addOnSuccessListener(listener);
        }
    }

    private void storeBitmap(Bitmap bitmap) {
        drawings[drawingDownloadCounter] = bitmap;
        ++drawingDownloadCounter;
    }

    private void sendRatingToDatabase(String playerName) {
        final int rating = ratings[ratingToSendCounter];
        final DatabaseReference playerRating = rankingRef
                .child(format(Locale.getDefault(), "%s", playerName));

        playerRating.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long value = dataSnapshot.getValue(Long.class);
                        if (value != null) {
                            playerRating.setValue(
                                    value.intValue()
                                            + rating);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        ++ratingToSendCounter;
        ratingBar.setEnabled(false);
    }

    // public for testing only, the users in the database should be already sorted by their ranking
    // before calling this
    public void showFinalRanking(View view) {

        rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] ranking = new String[NUMBER_OF_DRAWINGS];
                short counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ranking[counter] = snapshot.getKey();
                    ++counter;
                }

                Bundle bundle = new Bundle();
                bundle.putStringArray("Ranking", ranking);
                setGlobalVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.votingPageLayout, RankingFragment
                                .instantiate(getApplicationContext(),
                                        RankingFragment.class.getName(), bundle), "RankingFragment")
                        .addToBackStack(null).commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showWinnerDrawing(Bitmap img, String winnerName) {
        changeDrawing(img, winnerName);
        findViewById(R.id.ratingBar).setVisibility(View.GONE);
        findViewById(R.id.buttonChangeImage)
                .setVisibility(View.GONE); // to be removed after testing
        findViewById(R.id.rankingButton).setVisibility(View.GONE); // to be removed after testing
    }

    private void setGlobalVisibility(final int visibility) {
        findViewById(R.id.ratingBar).setVisibility(visibility);
        findViewById(R.id.drawing).setVisibility(visibility);
        findViewById(R.id.playerNameView).setVisibility(visibility);
        findViewById(R.id.buttonChangeImage)
                .setVisibility(visibility); // to be removed after testing
        findViewById(R.id.rankingButton).setVisibility(visibility); // to be removed after testing
    }
}
