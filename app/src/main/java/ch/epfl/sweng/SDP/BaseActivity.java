package ch.epfl.sweng.SDP;

import android.os.Bundle;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Prevents the user from pressing the back button.
     */
    @Override
    public void onBackPressed() {}
}