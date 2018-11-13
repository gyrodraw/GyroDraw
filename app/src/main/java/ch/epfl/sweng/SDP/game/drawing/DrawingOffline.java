package ch.epfl.sweng.SDP.game.drawing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DrawingOffline extends GyroDrawingActivity implements SensorEventListener {

    public void exitClick(View view) {
        Log.d(TAG, "Exiting drawing view");
        this.finish();
    }

}
