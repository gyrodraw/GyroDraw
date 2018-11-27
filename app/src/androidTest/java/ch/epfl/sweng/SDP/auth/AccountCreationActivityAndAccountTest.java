package ch.epfl.sweng.SDP.auth;

import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.FriendsRequestState;

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


@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityAndAccountTest {

    private static final String USER_ID = "123456789";
    private static final String TEST_EMAIL = "testEmail";
    private static final String USERNAME = "username";
    private static final String LEAGUE = "league1";

    @Rule
    public final ActivityTestRule<AccountCreationActivity> activityRule =
            new ActivityTestRule<>(AccountCreationActivity.class);

    private Account account;

    @Before
    public void init() {
        account = Account.getInstance(activityRule.getActivity());
        account.setUserId(USER_ID);
    }

    @After
    public void afterEachTest() {
        Account.deleteAccount();
    }

    // Tests for Account

    @Test
    public void testSetGetEmail() {
        account.setEmail("email");
        assertThat(account.getEmail(), is("email"));
    }

    @Test
    public void testSetGetTrophies() {
        account.setTrophies(1);
        assertThat(account.getTrophies(), is(1));
    }

    @Test
    public void testSetGetStars() {
        account.setStars(1);
        assertThat(account.getStars(), is(1));
    }


    @Test
    public void testSetGetCurrentLeague() {
        account.setCurrentLeague("test");
        assertThat(account.getCurrentLeague(), is("test"));
    }

    @Test
    public void testSetGetMatchesWon() {
        account.setMatchesWon(1);
        assertThat(account.getMatchesWon(), is(1));
    }

    @Test
    public void testSetGetTotalMatches() {
        account.setTotalMatches(1);
        assertThat(account.getTotalMatches(), is(1));
    }

    @Test
    public void testSetGetMaxTrophies() {
        account.setMaxTrophies(1);
        assertThat(account.getMaxTrophies(), is(1));
    }

    @Test
    public void testSetGetAverageRating() {
        account.setAverageRating(1.0);
        assertThat(account.getAverageRating(), is(1.0));
    }

    @Test
    public void testSetGetUserId() {
        account.setUserId("123");
        assertThat(account.getUserId(), is("123"));
    }

    @Test
    public void testSetGetUsername() {
        account.setUsername("user");
        assertThat(account.getUsername(), is("user"));
    }

    @Test
    public void testSetUsersRef() {
        DatabaseReference databaseReference = Mockito.mock(DatabaseReference.class);
        account.setUsersRef(databaseReference);
    }

    @Test
    public void testIncreaseMatchesWon() {
        account.increaseMatchesWon();
        assertThat(account.getMatchesWon(), is(1));
    }

    @Test
    public void testIncreaseTotalMatches() {
        account.increaseTotalMatches();
        assertThat(account.getTotalMatches(), is(1));
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
    public void testNewFriend() {
        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .setValue(FriendsRequestState.SENT.ordinal());
        setListenerAndAssertToFirebaseForFriendsTest(true);
        account.addFriend("HFNDgmFKQPX92nmfmi2qAUfTzxJ3");
    }

    @Test
    public void testConfirmFriend() {
        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .setValue(FriendsRequestState.RECEIVED.ordinal());
        setListenerAndAssertToFirebaseForFriendsTest(true);
        account.addFriend("HFNDgmFKQPX92nmfmi2qAUfTzxJ3");
    }

    @Test
    public void testRemoveFriend() {
        setListenerAndAssertToFirebaseForFriendsTest(false);
        account.removeFriend("HFNDgmFKQPX92nmfmi2qAUfTzxJ3");
    }

    @Test
    public void testUpdateUsername() {
        final String newUsername = "987654321";
        account.setUsername(newUsername);
        assertThat(account.getUsername(), is(newUsername));
    }

    @Test
    public void testRegisterAccount() {
        account.registerAccount();
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateAccountWhenAlreadyCreated() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME,
                TEST_EMAIL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullContext() {
        Account.createAccount(null, new ConstantsWrapper(), USERNAME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullConstantsWrapper() {
        Account.createAccount(activityRule.getActivity(), null,
                USERNAME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullUsername() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                null, TEST_EMAIL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullEmail() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullCurrentLeague() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, null, 0, 0,
                0, 0, 0.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeTrophies() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, -1, 0,
                0, 0, 0.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeStars() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                -1, 0, 0, 0.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeMatchesWon() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, -1, 0, 0.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeTotalMatches() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, 0, -1, 0.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeAverageRating() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, 0, 0, -1.0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeMaxTrophies() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, 0, 0, 0.0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateUsernameWithNull() {
        account.updateUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeBalanceStars() {
        account.changeStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeAverageRatingWithZero() {
        account.changeAverageRating(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeAverageRatingWithGreaterThanFiveValue() {
        account.changeAverageRating(6);
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
        onView(ViewMatchers.withId(R.id.createAccount)).check(matches(isClickable()));
    }

    @Test
    public void testCreateAccountWithNullName() {
        onView(ViewMatchers.withId(R.id.createAccount)).perform(click());
        onView(ViewMatchers.withId(R.id.usernameTaken))
                .check(matches(withText("Username must not be empty")));
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
                .perform(typeText("PICASSO"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.createAccount)).perform(click());
        assertNotEquals(null, account);
    }

    private void setListenerAndAssertToFirebaseForFriendsTest(final boolean state) {
        final CountingIdlingResource countingResource =
                new CountingIdlingResource("WaitForFirebase");
        countingResource.increment();
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countingResource.decrement();
                assertThat(dataSnapshot.exists(), is(state));
                Database.getReference("users."
                        + USER_ID
                        + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                        .removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        };

        Database.getReference("users."
                + USER_ID + ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3")
                .addValueEventListener(valueEventListener);
    }

}