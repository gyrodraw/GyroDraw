package ch.epfl.sweng.SDP.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;

/**
 * Class representing the leagues' list which can be opened from the {@link HomeActivity}.
 */
public class LeaguesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leagues);

        final ScrollView scrollView = findViewById(R.id.scrollView);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");
        setGlobalTypeface(typeOptimus);

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.leaguesBackgroundAnimation));
    }

    private void setGlobalTypeface(Typeface typeface) {
        ((TextView) findViewById(R.id.league1Name)).setTypeface(typeface);
        ((TextView) findViewById(R.id.league2Name)).setTypeface(typeface);
        ((TextView) findViewById(R.id.league3Name)).setTypeface(typeface);

        ((TextView) findViewById(R.id.league1Text)).setTypeface(typeface);
        ((TextView) findViewById(R.id.league2Text)).setTypeface(typeface);
        ((TextView) findViewById(R.id.league3Text)).setTypeface(typeface);
    }
}
