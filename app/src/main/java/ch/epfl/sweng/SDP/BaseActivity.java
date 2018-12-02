package ch.epfl.sweng.SDP;

import android.os.Bundle;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Prevents the user from pressing the back button.
     */
    @Override
    public void onBackPressed() {
        // Does nothing in here because we want to disable back button
    }
}
