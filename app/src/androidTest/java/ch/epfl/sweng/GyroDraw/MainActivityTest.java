package ch.epfl.sweng.GyroDraw;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;

import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.auth.ConstantsWrapper;
import ch.epfl.sweng.GyroDraw.auth.LoginActivity;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForAccount;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForAccount;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.GyroDraw.auth.LoginActivityTest.executeOnUiThread;
import static ch.epfl.sweng.GyroDraw.firebase.FbDatabase.createCompletionListener;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.ONLINE;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.changeOnlineStatus;
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
    private static final String TEST_LEAGUE = "league1";

    @Rule
    public final ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    private MainActivity activity;

    // Add a monitor for the login activity
    private final Instrumentation.ActivityMonitor loginMonitor = getInstrumentation()
            .addMonitor(LoginActivity.class.getName(), null, false);

    @Before
    public void init() {
        activity = activityRule.getActivity();
    }

    @Test
    public void testCanOpenLoginActivity() {
        onView(withId(R.id.login_button)).perform(click());
        Activity loginActivity = getInstrumentation()
                .waitForMonitorWithTimeout(loginMonitor, 5000);
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

        activity.cloneAccountFromFirebase(snapshot);

        Account.deleteAccount();
        LocalDbForAccount localDbHandlerForAccount = new LocalDbHandlerForAccount(
                activity, null, 1);
        Account.createAccount(activity, new ConstantsWrapper(),
                TEST_USERNAME, TEST_EMAIL);
        localDbHandlerForAccount.retrieveAccount(Account.getInstance(activity));
        Account newAccount = Account.getInstance(activity);

        assertThatAccountWasInitializedCorrectly(newAccount);
    }

    @Test
    public void testHandleUserStatusOnline() {
        changeOnlineStatus(TEST_USER_ID, ONLINE, createCompletionListener());
        SystemClock.sleep(3000);

        TextView errorMessage = new TextView(activity);
        activity.handleUserStatus(errorMessage);
        SystemClock.sleep(3000);

        assertThat(errorMessage.getText().toString(),
                is(activity.getString(R.string.already_logged_in)));
        assertThat(errorMessage.getVisibility(), is(View.VISIBLE));
        changeOnlineStatus(TEST_USER_ID, OFFLINE, createCompletionListener());
    }

    @Test
    public void testHandleUserStatusOffline() {
        changeOnlineStatus(TEST_USER_ID, OFFLINE, createCompletionListener());
        SystemClock.sleep(3000);

        TextView errorMessage = new TextView(activity);
        activity.handleUserStatus(errorMessage);
        SystemClock.sleep(3000);

        assertThat(activity.isFinishing(), is(true));
    }

    @Test
    public void testRedirectionToHome() {
        HashMap<String, Object> values = new HashMap<>();
        initializeAccountHashMap(values);

        HashMap<String, HashMap<String, Object>> account = new HashMap<>();
        account.put(TEST_USER_ID, values);

        final DataSnapshot snapshot = Mockito.mock(DataSnapshot.class);
        when(snapshot.exists()).thenReturn(true);
        when(snapshot.getValue()).thenReturn(account);

        final DataSnapshot snapshot2 = Mockito.mock(DataSnapshot.class);
        when(snapshot2.exists()).thenReturn(false);

        executeOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().handleRedirection(snapshot);
                activityRule.getActivity().handleRedirection(snapshot2);
            }
        });
    }

    @Test
    public void testRedirectionToMain() {

        final DataSnapshot snapshot = Mockito.mock(DataSnapshot.class);
        when(snapshot.exists()).thenReturn(false);

        executeOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().handleRedirection(snapshot);
            }
        });

        assertThat(this.activity.isFinishing(), is(false));
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
