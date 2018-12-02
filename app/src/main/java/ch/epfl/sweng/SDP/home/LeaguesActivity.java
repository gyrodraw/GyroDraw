package ch.epfl.sweng.SDP.home;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.LayoutUtils;

public class LeaguesActivity extends BaseActivity {

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
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");

        ((TextView) findViewById(R.id.league1Name)).setTypeface(typeOptimus);
        ((TextView) findViewById(R.id.league2Name)).setTypeface(typeOptimus);
        ((TextView) findViewById(R.id.league3Name)).setTypeface(typeOptimus);

        ((TextView) findViewById(R.id.league1Text)).setTypeface(typeOptimus);
        ((TextView) findViewById(R.id.league2Text)).setTypeface(typeOptimus);
        ((TextView) findViewById(R.id.league3Text)).setTypeface(typeOptimus);

        ((TextView) findViewById(R.id.league1Difficulty)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.league2Difficulty)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.league3Difficulty)).setTypeface(typeMuro);

        Glide.with(this).load(R.drawable.background_animation)
                .into((ImageView) findViewById(R.id.leaguesBackgroundAnimation));
        LayoutUtils.setFadingExitListener(findViewById(R.id.exitButton), this);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
