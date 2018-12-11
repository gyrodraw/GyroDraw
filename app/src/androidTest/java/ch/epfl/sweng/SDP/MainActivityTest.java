package ch.epfl.sweng.SDP;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.auth.ConstantsWrapper;
import ch.epfl.sweng.SDP.auth.LoginActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String TEST_USERNAME = "TESTUSERNAME";
    private static final String TEST_EMAIL = "testEmail";
    private static final String TEST_USER_ID = "no_user";
    private static final String TEST_LEAGUE = "leagueOne";

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    // Add a monitor for the login activity
    private final Instrumentation.ActivityMonitor monitor = getInstrumentation()
            .addMonitor(LoginActivity.class.getName(), null, false);

    @Test
    public void testCanOpenLoginActivity() {
        onView(withId(R.id.login_button)).perform(click());
        Activity loginActivity = getInstrumentation()
                .waitForMonitorWithTimeout(monitor, 5000);
        assertThat(loginActivity, is(not(nullValue())));
    }

    @Test
    public void testCloneAccountFromFirebase() {
        HashMap<String, Object> values = new HashMap<>();
        initializeAccountHashMap(values);

        HashMap<String, HashMap<String, Object>> account = new HashMap<>();
        account.put(TEST_USER_ID, values);

        DataSnapshot snapshot = Mockito.mock(DataSnapshot.class);
        when(snapshot.getValue()).thenReturn(account);

        mActivityRule.getActivity().cloneAccountFromFirebase(snapshot);

        Account.deleteAccount();
        Context context = mActivityRule.getActivity().getApplicationContext();
        LocalDbHandlerForAccount localDbHandlerForAccount = new LocalDbHandlerForAccount(
                context, null, 1);
        Account.createAccount(context, new ConstantsWrapper(),
                TEST_USERNAME, TEST_EMAIL);
        localDbHandlerForAccount.retrieveAccount(Account.getInstance(context));
        Account newAccount = Account.getInstance(context);

        assertThatAccountWasInitializedCorrectly(newAccount);
    }

    /**
     * Populates the given HashMap with test values.
     *
     * @param values HashMap to be populated
     */
    private void initializeAccountHashMap(HashMap<String, Object> values) {
        values.put("username", TEST_USERNAME);
        values.put("userId", TEST_USER_ID);
        values.put("trophies", 10L);
        values.put("stars", 20L);
        values.put("matchesWon", 30L);
        values.put("totalMatches", 40L);
        values.put("averageRating", 3.5);
        values.put("email", TEST_EMAIL);
        values.put("currentLeague", TEST_LEAGUE);
        values.put("maxTrophies", 100L);
    }

    /**
     * Tests that the given account has been saved correctly.
     *
     * @param newAccount to be checked
     */
    private void assertThatAccountWasInitializedCorrectly(Account newAccount) {
        assertThat(newAccount.getUserId(), is(equalTo(TEST_USER_ID)));
        assertThat(newAccount.getUsername(), is(equalTo(TEST_USERNAME)));
        assertThat(newAccount.getTrophies(), is(10));
        assertThat(newAccount.getStars(), is(20));
        assertThat(newAccount.getMatchesWon(), is(30));
        assertThat(newAccount.getTotalMatches(), is(40));
        assertThat(newAccount.getAverageRating(), is(3.5));
        assertThat(newAccount.getEmail(), is(equalTo(TEST_EMAIL)));
        assertThat(newAccount.getCurrentLeague(), is(equalTo(TEST_LEAGUE)));
        assertThat(newAccount.getMaxTrophies(), is(100));
    }

}