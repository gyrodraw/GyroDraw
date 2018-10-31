package ch.epfl.sweng.SDP.game.drawing;


import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;

import android.util.Log;

import android.view.Display;
import android.view.KeyEvent;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

import ch.epfl.sweng.SDP.Activity;

import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.LocalDbHandler;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.VotingPageActivity;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.matchmaking.GameStates;
import ch.epfl.sweng.SDP.matchmaking.Matchmaker;

import com.google.android.gms.common.util.ArrayUtils;

// TODO: refactor code so this is a subclass
public class DrawingActivity extends Activity {
    protected static final String TAG = "DrawingActivity";
    protected PaintView paintView;
    protected Handler handler;

    private String roomID;
    private String winningWord;
    ToggleButton flyDraw;

    private ImageView[] colorButtons;

    private ImageView pencilButton;
    private ImageView eraserButton;
    private ImageView bucketButton;


    int getLayoutid() {
        return R.layout.activity_drawing_offline;
    }

    private final Database database = Database.INSTANCE;
    private DatabaseReference timerRef;
    private DatabaseReference stateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.fui_slide_in_right,
                R.anim.fui_slide_out_left);
        setContentView(getLayoutid());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        roomID = intent.getStringExtra("RoomID");
        winningWord = intent.getStringExtra("WinningWord");

        colorButtons = new ImageView[]{findViewById(R.id.blackButton),
                findViewById(R.id.blueButton), findViewById(R.id.greenButton),
                findViewById(R.id.yellowButton), findViewById(R.id.redButton)};

        pencilButton = findViewById(R.id.pencilButton);
        eraserButton = findViewById(R.id.eraserButton);
        bucketButton = findViewById(R.id.bucketButton);

        Resources res = getResources();
        colorButtons[1].setColorFilter(res.getColor(R.color.colorBlue), PorterDuff.Mode.SRC_ATOP);
        colorButtons[2].setColorFilter(res.getColor(R.color.colorGreen), PorterDuff.Mode.SRC_ATOP);
        colorButtons[3].setColorFilter(res.getColor(R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
        colorButtons[4].setColorFilter(res.getColor(R.color.colorRed), PorterDuff.Mode.SRC_ATOP);

        paintView = findViewById(R.id.paintView);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE);
        // Set the content to appear under the system bars so that the
        // content doesn't resize when the system bars hide and show.

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                paintView.invalidate();
            }
        };
    }

    /**
     * Clears the entire Path in paintView.
     *
     * @param view paintView
     */
    public void clear(View view) {
        paintView.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Matchmaker.getInstance(new ConstantsWrapper())
                    .leaveRoom(roomID);
            launchActivity(HomeActivity.class);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Sets the clicked button to selected and sets the corresponding color.
     *
     * @param view the clicked view
     */
    public void colorClickHandler(View view) {
        int index = ArrayUtils.indexOf(colorButtons, view);
        paintView.setColor(index);
        colorButtons[index].setImageResource(R.drawable.color_circle_selected);

        for (int i = 0; i < colorButtons.length; i++) {
            if (i != index) {
                colorButtons[i].setImageResource(R.drawable.color_circle);
            }
        }
    }

    /**
     * Sets the clicked button to selected and sets the corresponding color.
     *
     * @param view the clicked view
     */
    public void toolClickHandler(View view) {
        switch (view.getId()) {
            case R.id.pencilButton:
                paintView.setPencil();
                pencilButton.setImageResource(R.drawable.pencil_selected);
                eraserButton.setImageResource(R.drawable.eraser);
                bucketButton.setImageResource(R.drawable.bucket);
                break;
            case R.id.eraserButton:
                paintView.setEraser();
                pencilButton.setImageResource(R.drawable.pencil);
                eraserButton.setImageResource(R.drawable.eraser_selected);
                bucketButton.setImageResource(R.drawable.bucket);
                break;
            case R.id.bucketButton:
                paintView.setBucket();
                pencilButton.setImageResource(R.drawable.pencil);
                eraserButton.setImageResource(R.drawable.eraser);
                bucketButton.setImageResource(R.drawable.bucket_selected);
                break;
            default:
        }
    }

    public void exitClick(View view) {
        Log.d(TAG, "Exiting drawing view");
        this.finish();
    }


}
