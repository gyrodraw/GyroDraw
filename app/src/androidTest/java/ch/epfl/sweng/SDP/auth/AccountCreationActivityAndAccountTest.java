package ch.epfl.sweng.SDP.auth;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.SDP.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityAndAccountTest {

    @Rule
    public final ActivityTestRule<AccountCreationActivity> activityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    private ConstantsWrapper mockConstantsWrapper;
    private Account mockAccount;
    private Account account;

    @Before
    public void init() {
        mockConstantsWrapper = mock(ConstantsWrapper.class);
        mockAccount = mock(Account.class);
        DatabaseReference mockReference = mock(DatabaseReference.class);
        Query mockQuery = mock(Query.class);

        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn("123456789");

        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.orderByChild(isA(String.class))).thenReturn(mockQuery);

        when(mockQuery.equalTo(isA(String.class))).thenReturn(mockQuery);

        doNothing().when(mockReference)
                .setValue(isA(Integer.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference)
                .setValue(isA(Boolean.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference)
                .removeValue(isA(DatabaseReference.CompletionListener.class));

        doNothing().when(mockQuery).addListenerForSingleValueEvent(isA(ValueEventListener.class));

        doNothing().when(mockAccount).updateUsername(isA(String.class));

        Account.createAccount(activityRule.getActivity(), mockConstantsWrapper, "123456789");
        account = Account.getInstance(activityRule.getActivity());
        account.setUserId("123456789");
        Account.enableTesting();
    }

    // Tests for Account

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullUsername() {
        Account.createAccount(activityRule.getActivity(), mockConstantsWrapper, null);
    }

    @Test
    public void testSetTrophies() {
        account.setTrophies(1);
    }

    @Test
    public void testSetStars() {
        account.setStars(1);
    }


    @Test
    public void testSetCurrentLeague() {
        account.setCurrentLeague("test");
    }

    @Test
    public void testSetMatchesWon() {
        account.setMatchesWon(1);
    }

    @Test
    public void testSetMatchesLost() {
        account.setMatchesLost(1);
    }

    @Test
    public void testSetMaxTrophies() {
        account.setMaxTrophies(1);
    }

    @Test
    public void testSetAverageRating() {
        account.setAverageRating(1.0);
    }

    @Test
    public void testSetUsersRef() {
        DatabaseReference databaseReference = Mockito.mock(DatabaseReference.class);
        account.setUsersRef(databaseReference);
    }

    @Test
    public void testCurrentLeague() {
        assertThat(account.getCurrentLeague(), is("league1"));
    }

    @Test
    public void testGetStars() {
        assertThat(account.getStars(),
                is(0));
    }

    @Test
    public void testGetUserId() {
        assertThat(account.getUserId(), is("123456789"));
    }

    @Test
    public void testGetUsername() {
        assertThat(account.getUsername(), is("123456789"));
    }

    @Test
    public void testGetTrophies() {
        assertThat(account.getTrophies(), is(0));
    }

    @Test
    public void testGetMatchesWon() {
        assertThat(account.getMatchesWon(), is(0));
    }

    @Test
    public void testGetMatchesLost() {
        assertThat(account.getMatchesLost(), is(0));
    }

    @Test
    public void testGetAverageRating() {
        assertThat(account.getAverageRating(), is(0.0));
    }

    @Test
    public void testGetMaxTrophies() {
        assertThat(account.getMaxTrophies(), is(0));
    }

    @Test
    public void testIncreaseMatchesWon() {
        account.increaseMatchesWon();
        assertThat(account.getMatchesWon(), is(1));
    }

    @Test
    public void testIncreaseMatchesLost() {
        account.increaseMatchesLost();
        assertThat(account.getMatchesLost(), is(1));
    }

    @Test
    public void testChangeAverageRating() {
        account.changeAverageRating(3.5);
        assertThat(account.getAverageRating(), is(3.5));
    }

    @Test
    public void testChangeTrophies() {
        account.changeTrophies(20);
        assertThat(account.getTrophies(), is(20));
    }

    @Test
    public void testAddStars() {
        account.changeStars(20);
        assertThat(account.getStars(), is(20));
    }

    @Test
    public void testAddFriend() {
        account.addFriend("EPFLien");
    }

    @Test
    public void testRemoveFriend() {
        account.removeFriend("EPFLien");
    }

    @Test
    public void testUpdateUsername() {
        mockAccount.updateUsername("987654321");
        account.setUsername("987654321");
        assertThat(account.getUsername(), is("987654321"));
    }

    @Test
    public void testRegisterAccount() {
        account.registerAccount();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUsername() {
        account.updateUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeBalanceStars() {
        account.changeStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullFriend() {
        account.addFriend(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullFriend() {
        account.removeFriend(null);
    }

    // Tests for AccountCreationActivity

    @Test
    public void testCreateAccIsClickable() {
        onView(ViewMatchers.withId(R.id.createAcc)).check(matches(isClickable()));
    }

    @Test
    public void testCreateAccountWithNullName() {
        onView(ViewMatchers.withId(R.id.createAcc)).perform(click());
        onView(ViewMatchers.withId(R.id.usernameTaken))
                .check(matches(withText("Username must not be empty.")));
    }

    @Test
    public void testUsernameInputInputsCorrectly() {
        onView(withId(R.id.usernameInput))
                .perform(typeText("Max Muster"), closeSoftKeyboard())
                .check(matches(withText(R.string.test_name)));
    }

    @Test
    public void testCreateAccountWithValidInput() {
        onView(withId(R.id.usernameInput))
                .perform(typeText("Max Muster"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.createAcc)).perform(click());
        assertNotEquals(null, Account.getInstance(activityRule.getActivity()));
    }


}