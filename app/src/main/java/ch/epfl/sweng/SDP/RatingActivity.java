package ch.epfl.sweng.SDP;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class RatingActivity extends AppCompatActivity {

    private ImageView imageView;
    private LocalDBHandler localDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        imageView = findViewById(R.id.myImage);
        localDBHandler = new LocalDBHandler(getApplicationContext(), null, null, 1);
        //textView.setText(localDBHandler.getBitmap(0));
        Bitmap bitmap = localDBHandler.getLatestBitmapFromDB();

        imageView.setImageBitmap(bitmap);
    }
}