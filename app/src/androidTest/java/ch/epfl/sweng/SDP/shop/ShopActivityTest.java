package ch.epfl.sweng.SDP.shop;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.auth.ConstantsWrapper;
import ch.epfl.sweng.SDP.firebase.Database;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {

    private static final String USER_ID = "no_user";
    private static DatabaseReference usersRef = Database.getReference("users."
                                                    + USER_ID + ".boughtItems");

    private static final String TEST_EMAIL = "testEmail";
    private static final String USERNAME = "username";

    private static ConstantsWrapper mockConstantsWrapper;
    private static Account mockAccount;
    private static Account account;

    @Rule
    public final ActivityTestRule<ShopActivity> mActivityRule =
            new ActivityTestRule<ShopActivity>(ShopActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    ShopActivity.disableAnimations();
                    usersRef.removeValue();
                    //ShopActivity.enableTesting();
                }
            };

    @BeforeClass
    public static void init() {
        mockConstantsWrapper = mock(ConstantsWrapper.class);
        mockAccount = mock(Account.class);
        DatabaseReference mockReference = mock(DatabaseReference.class);
        Query mockQuery = mock(Query.class);

        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn(USER_ID);

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


        Account.createAccount(InstrumentationRegistry.getContext(), mockConstantsWrapper, USERNAME,
                TEST_EMAIL);
        account = Account.getInstance(InstrumentationRegistry.getContext());
        account.setUserId(USER_ID);
    }

    @After
    public void afterEachTest() {
        Account.deleteAccount();
    }

    @Test
    public void testPressItemAndCancel() {
        SystemClock.sleep(5000);
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.cancelButton)).perform(click());
        onView(withId(R.id.buyButton)).check(doesNotExist());
    }

    @Test
    public void testPressBuyItemNoStars() {
        SystemClock.sleep(5000);
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(0);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.buyButton)).perform(click());

        onView(withId(R.id.confirmationText)).check(matches(withText("Error")));

        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());


    }

    @Test
    public void testPressBuyItemSuccess() {
        Account.createAccount(mActivityRule.getActivity(), new ConstantsWrapper(), USER_ID
                                                                    , "test@test.com");
        Account.getInstance(mActivityRule.getActivity()).setStars(100);
        SystemClock.sleep(5000);
        LinearLayout layout = mActivityRule.getActivity().findViewById(R.id.shopItems);
        LinearLayout layoutChild = (LinearLayout) layout.getChildAt(1);
        int id = View.generateViewId();
        layoutChild.setId(id);

        onView(withId(id)).perform(click());
        onView(withId(R.id.buyButton)).perform(click());

        onView(withId(R.id.confirmationText)).check(matches(withText("Success")));

        onView(withId(R.id.okButton)).perform(click());
        onView(withId(R.id.okButton)).check(doesNotExist());
    }
}
