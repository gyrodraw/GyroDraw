package ch.epfl.sweng.SDP;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;
import org.junit.Rule;

@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityTest {
    @Rule
    public final ActivityTestRule<AccountCreationActivity> accountCreationActivityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    /**
     * tests dependent on logged in / deconnected FirebaseUser -> how to simulate
     */
}