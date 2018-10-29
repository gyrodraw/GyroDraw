package ch.epfl.sweng.SDP.shop;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.test.espresso.intent.Intents;
import android.widget.Button;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {
    private final int delay = 500;
    private long stars = 0;
    private final String testString = "testString";


    @Mock
    DataSnapshot trueSnapshot;

    DataSnapshot falseSnapshot;
    DataSnapshot existingStarsSnapshot;
    DataSnapshot existingPriceSnapshot;
    DataSnapshot nonexistingSnapshot;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        existingStarsSnapshot = mock(DataSnapshot.class);
        nonexistingSnapshot = mock(DataSnapshot.class);
        when(trueSnapshot.exists()).thenReturn(true);
        when(trueSnapshot.getValue()).thenReturn(true);
        when(existingStarsSnapshot.exists()).thenReturn(true);
        when(existingStarsSnapshot.getValue()).thenReturn(stars);
        when(nonexistingSnapshot.exists()).thenReturn(false);
    }

    @Rule
    public final ActivityTestRule<ShopTestActivity> activityTestRule =
            new ActivityTestRule<>(ShopTestActivity.class);

    @Test
    public void returnIsClickable() {
        onView(ViewMatchers.withId(R.id.returnFromShop)).check(matches(isClickable()));
    }

    @Test
    public void btnHasOnClickListenerAfterSetReturn() {
        Button btn = new Button(activityTestRule.getActivity().getApplicationContext());
        assertFalse(btn.hasOnClickListeners());
        activityTestRule.getActivity().setReturn(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    @Test
    public void refreshIsClickable() {
        onView(ViewMatchers.withId(R.id.refreshShop)).check(matches(isClickable()));
    }

    @Test
    public void btnHasOnClickListenerAfterSetRefresh() {
        Button btn = new Button(activityTestRule.getActivity().getApplicationContext());
        assertFalse(btn.hasOnClickListeners());
        activityTestRule.getActivity().setRefresh(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    @Test
    public void initializeButtonIsClickable() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertTrue(btn.isClickable());
    }

    @Test
    public void initializeButtonHasCorrectText() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertEquals(testString, btn.getText());
    }

    @Test
    public void addPurchaseOnClickListenerToButtonWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertTrue(!btn.hasOnClickListeners());
        activityTestRule.getActivity().addPurchaseOnClickListenerToButton(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    @Test
    public void sufficientCurrencyWorksIfMoreStars() {
        assertTrue(activityTestRule.getActivity().sufficientCurrency(3, 0));
    }

    @Test
    public void sufficientCurrencyWorksIfEqualStars() {
        assertTrue(activityTestRule.getActivity().sufficientCurrency(3,3));
    }

    @Test
    public void sufficientCurrencyWorksIfLessStars() {
        assertFalse(activityTestRule.getActivity().sufficientCurrency(0, 3));
    }

    @Test
    public void wrapDataSnapshotValueWorksWithExistingValid() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().wrapDataSnapshotValue(wrapper, existingStarsSnapshot);
        assertEquals(stars, wrapper.getInt());
    }

    @Test
    public void wrapDataSnapshotValueWorksWithExistingInvalid() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().wrapDataSnapshotValue(wrapper, trueSnapshot);
        assertEquals(-1, wrapper.getInt());
    }

    @Test
    public void wrapDataSnapshotValueWorksWithNonExisting() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().wrapDataSnapshotValue(wrapper, nonexistingSnapshot);
        assertEquals(-1, wrapper.getInt());
    }

    @Test
    public void setTextViewMessageWorks() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().setTextViewMessage(textView, testString);
        assertEquals(testString, textView.getText());
    }

    @Test
    public void resetTextViewMessageWorks() {
        Looper.prepare();
        final TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().setTextViewMessage(textView, testString);
        activityTestRule.getActivity().resetTextViewMessage(textView, delay);
        new CountDownTimer(delay, delay) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                assertEquals("", textView.getText());
            }
        }.start();
    }

    @Test
    public void gotoHomeWorks() {
        Intents.init();
        activityTestRule.getActivity().gotoHome();
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void refreshShopWorks() {
        Intents.init();
        activityTestRule.getActivity().refreshShop();
        intended(hasComponent(ShopTestActivity.class.getName()));
        Intents.release();
    }

}
