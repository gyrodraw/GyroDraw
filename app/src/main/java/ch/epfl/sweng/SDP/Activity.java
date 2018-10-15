package ch.epfl.sweng.SDP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Class containing useful and widely used methods. It should be inherited by all the other
 * activities.
 */
public abstract class Activity extends AppCompatActivity {

    /**
     * Start the specified activity.
     *
     * @param activityClass the class of the activity to launch
     */
    public void launchActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
