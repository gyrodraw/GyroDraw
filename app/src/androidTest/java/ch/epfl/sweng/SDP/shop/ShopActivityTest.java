package ch.epfl.sweng.SDP.shop;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;

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
import static ch.epfl.sweng.SDP.game.WaitingPageActivityTest.waitForVisibility;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {

    private final static String USER_ID = "123456789";
    private static DatabaseReference usersRef = Database.getReference("users."
                                                    + USER_ID + ".boughtItems");

    private DataSnapshot mockDataSnapshot;
    private Account mockAccount;

    @Rule
    public final ActivityTestRule<ShopActivity> mActivityRule =
            new ActivityTestRule<ShopActivity>(ShopActivity.class) {
                @Override
                protected void beforeActivityLaunched() {
                    ShopActivity.disableAnimations();
                    usersRef.removeValue();
                    ShopActivity.enableTesting();
                }
            };

    @Before
    public void init() {
        mockDataSnapshot = Mockito.mock(DataSnapshot.class);
        mockAccount = Mockito.mock(Account.class);
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
        //waitForVisibility(mActivityRule.getActivity().findViewById(R.id.shopItems), View.VISIBLE);
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

    @Test
    public void extractColorsFromFirebaseTest() {
        List<DataSnapshot> dsList = new LinkedList<>();
        when(mockDataSnapshot.getValue(int.class)).thenReturn(500);
        when(mockDataSnapshot.getKey()).thenReturn("red");
        dsList.add(mockDataSnapshot);

        when(mockDataSnapshot.getChildren()).thenReturn(dsList);
        when(mockAccount.getItemsBought()).thenReturn(new LinkedList<ShopItem>());

        mActivityRule.getActivity().extractColorsFromDataSnapshot(mockDataSnapshot, mockAccount);

        Shop shop = new Shop();

        shop.addItem(new ShopItem("red", 500));

        assertEquals(mActivityRule.getActivity()
                .getShop().getItemList().get(0).getColorItem(), shop.getItemList()
                                                                .get(0).getColorItem());

    }
}
