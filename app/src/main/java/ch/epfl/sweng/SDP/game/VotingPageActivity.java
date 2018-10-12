package ch.epfl.sweng.SDP;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class VotingPageActivity extends AppCompatActivity {

    private static final int NUMBER_OF_DRAWINGS = 5;
    private Bitmap[] drawings;
    private int[] ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_page);
        drawings = retrieveDrawingsFromDB();
        ratings = new int[NUMBER_OF_DRAWINGS];
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void changeDrawing(int resId) {
        ImageView drawing = findViewById(R.id.drawing);
        drawing.setImageResource(resId);
    }

    private Bitmap[] retrieveDrawingsFromDB() {
        // TODO
        return null;
    }

    private void sendRatingsToDB() {
        // TODO
    }

    private void showFinalRanking() {
        // TODO
    }

    private void showWinnerDrawing() {
        // TODO
    }
}
