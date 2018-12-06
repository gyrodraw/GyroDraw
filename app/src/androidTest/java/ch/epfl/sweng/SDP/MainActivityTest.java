package ch.epfl.sweng.SDP;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.auth.ConstantsWrapper;
import ch.epfl.sweng.SDP.auth.LoginActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

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
        HashMap<String,Object> values = new HashMap<>();
        initializeAccountHashMap(values);

        HashMap<String, HashMap<String, Object>> account = new HashMap<>();
        account.put("no_user", values);

        DataSnapshot snapshot = Mockito.mock(DataSnapshot.class);
        when(snapshot.getValue()).thenReturn(account);

        mActivityRule.getActivity().cloneAccountFromFirebase(snapshot);

        Account.deleteAccount();
        Context context = mActivityRule.getActivity().getApplicationContext();
        LocalDbHandlerForAccount localDbHandlerForAccount = new LocalDbHandlerForAccount(
                context, null, 1);
        Account.createAccount(context, new ConstantsWrapper(),
                "TESTUSERNAME", "testEmail");
        localDbHandlerForAccount.retrieveAccount(Account.getInstance(context));
        Account newAccount = Account.getInstance(context);

        assertThatAccountWasInitializedCorrectly(newAccount);
    }

    public void initializeAccountHashMap(HashMap<String, Object> values) {
        values.put("username", "TESTUSERNAME");
        values.put("userId", "no_user");
        values.put("trophies", new Long(10));
        values.put("stars", new Long(20));
        values.put("matchesWon", new Long(30));
        values.put("totalMatches", new Long(40));
        values.put("averageRating", new Double(3.5));
        values.put("email", "testEmail");
        values.put("currentLeague", "leagueOne");
        values.put("maxTrophies", new Long(100));
    }

    public void assertThatAccountWasInitializedCorrectly(Account newAccount) {
        assertThat(newAccount.getUserId(), is(equalTo("no_user")));
        assertThat(newAccount.getUsername(), is(equalTo("TESTUSERNAME")));
        assertThat(newAccount.getTrophies(), is(10));
        assertThat(newAccount.getStars(), is(20));
        assertThat(newAccount.getMatchesWon(), is(30));
        assertThat(newAccount.getTotalMatches(), is(40));
        assertThat(newAccount.getAverageRating(), is(3.5));
        assertThat(newAccount.getEmail(), is(equalTo("testEmail")));
        assertThat(newAccount.getCurrentLeague(), is(equalTo("leagueOne")));
        assertThat(newAccount.getMaxTrophies(), is(100));
    }

}