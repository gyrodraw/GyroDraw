package ch.epfl.sweng.SDP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    /**
     * Set the visibility of the views corresponding to the given ids to the given value.
     * @param visibility the value to set the visibility at
     * @param ids the ids of the views whose visibility is to be set
     */
    public void setVisibility(int visibility, int... ids) {
        for (int id: ids) {
            findViewById(id).setVisibility(visibility);
        }
    }

    /**
     * Set the visibility of the given views to the given value.
     * @param visibility the value to set the visibility at
     * @param views the views whose visibility is to be set
     */
    public void setVisibility(int visibility, View... views) {
        for (View view: views) {
            view.setVisibility(visibility);
        }
    }
}
