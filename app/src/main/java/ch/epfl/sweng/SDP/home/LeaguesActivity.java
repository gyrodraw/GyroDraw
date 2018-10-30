package ch.epfl.sweng.SDP.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import com.bumptech.glide.Glide;

public class LeaguesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);

        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");
        setGlobalTypeface(typeOptimus);

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.leaguesBackgroundAnimation));
    }

    private void setGlobalTypeface(Typeface typeface) {
        ((TextView) findViewById(R.id.league1Name)).setTypeface(typeface);
        ((TextView) findViewById(R.id.league2Name)).setTypeface(typeface);
        ((TextView) findViewById(R.id.league3Name)).setTypeface(typeface);
    }
}
