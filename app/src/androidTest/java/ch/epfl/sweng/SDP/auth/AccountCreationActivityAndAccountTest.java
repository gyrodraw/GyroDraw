package ch.epfl.sweng.SDP.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.SDP.home.FriendsRequestState;
import ch.epfl.sweng.SDP.shop.ColorsShop;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@RunWith(AndroidJUnit4.class)
public class AccountCreationActivityAndAccountTest {

    private static final String USER_ID = "123456789";
    private static final String TEST_EMAIL = "testEmail";
    private static final String EMAIL_TAG = "email";
    private static final String USERNAME = "username";
    private static final String LEAGUE = "league1";
    private static final String USERS_TAG = "users.";
    private static final String TEST_FRIEND = "HFNDgmFKQPX92nmfmi2qAUfTzxJ3";
    private static final String TEST_FRIEND_TAG = ".friends.HFNDgmFKQPX92nmfmi2qAUfTzxJ3";

    @Rule
    public final ActivityTestRule<AccountCreationActivity> activityRule =
            new ActivityTestRule<AccountCreationActivity>(AccountCreationActivity.class) {

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(EMAIL_TAG, TEST_EMAIL);

                    return intent;
                }
            };

    private Account account;

    @Before
    public void init() {
        account = Account.getInstance(activityRule.getActivity());
        account.setUserId(USER_ID);
        account.setUsername(USERNAME);
        account.setEmail(TEST_EMAIL);
    }

    @After
    public void afterEachTest() {
        Account.deleteAccount();
    }

    // Tests for Account

    @Test
    public void testSetGetEmail() {
        account.setEmail(EMAIL_TAG);
        assertThat(account.getEmail(), is(EMAIL_TAG));
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
        account.increaseTotalMatches();
        account.changeAverageRating(3.5);
        assertThat(account.getAverageRating(), is(3.5));
    }

    @Test
    public void testChangeTrophies() {
        account.changeTrophies(20);
        assertThat(account.getTrophies(), is(20));
    }

    @Test
    public void testUpdateItemsBought() {
        List<ShopItem> itemList = new LinkedList<>();
        ShopItem shopItem = new ShopItem(ColorsShop.BLUE, 20);
        itemList.add(shopItem);
        account.updateItemsBought(shopItem);
        assertThat(account.getItemsBought(), is(itemList));
    }

    @Test
    public void testAddStars() {
        account.changeStars(20);
        assertThat(account.getStars(), is(20));
    }

    @Test
    public void testNewFriend() {
        friendsTestHelper(FriendsRequestState.SENT.ordinal());
    }

    @Test
    public void testConfirmFriend() {
        friendsTestHelper(FriendsRequestState.RECEIVED.ordinal());
    }

    private void friendsTestHelper(int state) {
        FbDatabase.setFriendValue(USER_ID, TEST_FRIEND, state);
        setListenerAndAssertToFirebaseForFriendsTest(true,
                USERS_TAG + USER_ID + TEST_FRIEND_TAG);
        account.addFriend(TEST_FRIEND);
    }

    @Test
    public void testRemoveFriend() {
        setListenerAndAssertToFirebaseForFriendsTest(false,
                USERS_TAG + USER_ID + TEST_FRIEND_TAG);
        account.removeFriend(TEST_FRIEND);
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

    @Test
    public void testHandleResponseAndRedirect() {
        Account.deleteAccount();
        activityRule.getActivity().handleResponseAndRedirect(USERNAME);
        assertThat(activityRule.getActivity().isFinishing(), is(true));
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
                0, 0, 0.0, 0,
                new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeTrophies() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, -1, 0,
                0, 0, 0.0, 0, new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeStars() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                -1, 0, 0, 0.0, 0, new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeMatchesWon() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, -1, 0, 0.0, 0, new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeTotalMatches() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, 0, -1, 0.0, 0, new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeAverageRating() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, 0, 0, -1.0, 0, new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNegativeMaxTrophies() {
        Account.createAccount(activityRule.getActivity(), new ConstantsWrapper(),
                USERNAME, TEST_EMAIL, LEAGUE, 0,
                0, 0, 0, 0.0, -1, new ArrayList<ShopItem>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeBalanceStars() {
        account.changeStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeAverageRatingWithGreaterThanTwentyValue() {
        account.changeAverageRating(26);
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
        assertThat(account, is(not(nullValue())));
    }

    @Test
    public void testCreateAccountWithTooShortUsername() {
        testIllegalUsernameGivesCorrectError("MAX", R.string.usernameTooShort, "");
    }

    @Test
    public void testCreateAccountWithTooLongUsername() {
        testIllegalUsernameGivesCorrectError("SUPERDUPERLONGUSERNAMEWHICHISSUPPOSEDTOFAIL",
                R.string.usernameTooLong, "");
    }

    @Test
    public void testCreateAccountWithDoubleSpace() {
        testIllegalUsernameGivesCorrectError("MAX   MUSTER",
                R.string.usernameIllegalChar, " double spaces");
    }

    @Test
    public void testCreateAccountWithBackslash() {
        testIllegalUsernameGivesCorrectError("MAX" + '\\' + "MUSTER",
                R.string.usernameIllegalChar, " \\");
    }

    @Test
    public void testCreateAccountWithPercent() {
        testIllegalUsernameGivesCorrectError("MAX" + '%' + "MUSTER",
                R.string.usernameIllegalChar, " %");
    }

    @Test
    public void testCreateAccountWithDoubleQuotes() {
        testIllegalUsernameGivesCorrectError("MAX\"MUSTER",
                R.string.usernameIllegalChar, " \"");
    }

    @Test
    public void testCreateAccountWithQuotes() {
        testIllegalUsernameGivesCorrectError("MAX\'MUSTER",
                R.string.usernameIllegalChar, " '");
    }

    @Test
    public void testCreateAccountWithEmptyUsername() {
        onView(withId(R.id.usernameInput)).perform(typeText("T"));
        onView(withId(R.id.usernameInput)).perform(pressKey(KeyEvent.KEYCODE_DEL),
                closeSoftKeyboard());
        TextView feedback = activityRule.getActivity().findViewById(R.id.usernameTaken);
        assertThat(feedback.getText().toString(),
                is(equalTo(activityRule.getActivity().getResources()
                        .getString(R.string.usernameMustNotBeEmpty))));
    }

    @Test
    public void testCreateNewAccountSucceeds() {
        Account.deleteAccount();
        setListenerAndAssertToFirebaseForFriendsTest(true, USERS_TAG + USER_ID);
        onView(withId(R.id.usernameInput))
                .perform(typeText("TESTINGUSERNAME"), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.createAccount)).perform(click());
    }

    private void setListenerAndAssertToFirebaseForFriendsTest(
            final boolean state, String path) {
        final CountingIdlingResource countingResource =
                new CountingIdlingResource("WaitForFirebase");
        countingResource.increment();
        final ValueEventListener valueEventListener = new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assertThat(dataSnapshot.exists(), is(state));
                countingResource.decrement();
            }
        };

        FbDatabase.getReference(path)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    private void testIllegalUsernameGivesCorrectError(String username, int errorId, String append) {
        onView(withId(R.id.usernameInput)).perform(typeText(username), closeSoftKeyboard());
        TextView feedback = activityRule.getActivity().findViewById(R.id.usernameTaken);
        assertThat(feedback.getText().toString(),
                is(equalTo(activityRule.getActivity().getResources().getString(errorId)
                        + append)));
        Button createAccount = activityRule.getActivity().findViewById(R.id.createAccount);
        assertThat(createAccount.isEnabled(), is(false));
    }
}
