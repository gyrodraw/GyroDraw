package ch.epfl.sweng.SDP;

/**
 * Class to be inherited in activities where one wants to prevent the user from pressing the
 * back button.
 */
public abstract class NoBackPressActivity extends BaseActivity {

    /**
     * Prevents the user from pressing the back button.
     */
    @Override
    public void onBackPressed() {
        // Does nothing in here because we want to disable back button
    }
}