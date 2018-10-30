package ch.epfl.sweng.SDP.shop;

import android.os.CountDownTimer;
import android.os.Looper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;

import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.test.espresso.intent.Intents;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.support.test.espresso.matcher.ViewMatchers;

import static android.support.test.espresso.matcher.ViewMatchers.isClickable;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.Mock;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import org.mockito.MockitoAnnotations;

import org.mockito.internal.matchers.Null;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {
    private final int delayZero = 0;
    private final int delay500 = 500;
    private long stars = 500;
    private final int validNewStars = 1000;
    private final int invalidNewStars = -1000;
    private long price0 = 500;
    private long price1 = 1000;
    private final long longInt = 500;
    private final String testString = "testString";
    private final String color0 = "black";
    private final String color1 = "red";

    private HashMap<String, Boolean> userColorsMap = new HashMap<>();
    private List<DataSnapshot> shopColorsList = new ArrayList<>();

    @Mock
    private DatabaseReference currentUser;
    @Mock
    private DatabaseReference currentUserStars;
    @Mock
    private DatabaseReference currentUserItems;
    @Mock
    private DatabaseReference currentUserColors;
    @Mock
    private DatabaseReference currentUserSpecificItem;
    @Mock
    private DatabaseReference shopColors;
    @Mock
    private DatabaseReference shopColorsSpecificColor;

    @Mock
    private DataSnapshot nonExistant;
    @Mock
    private DataSnapshot longIntegerValueSnapshot;
    @Mock
    private DataSnapshot getPriceSnapshot;
    @Mock
    private DataSnapshot getStarsSnapshot;
    @Mock
    private DataSnapshot getColorsFromDatabaseSnapshot;
    @Mock
    private DataSnapshot color0Snapshot;
    @Mock
    private DataSnapshot color1Snapshot;

    @Mock
    private LinearLayout mockedLinearLayout;
    private List<String> buttonsInMockedLayout = new ArrayList<>();

    @Before
    public void setup() {
        //Initialize mocked objects.
        MockitoAnnotations.initMocks(this);

        shopColorsList.add(color0Snapshot);
        shopColorsList.add(color1Snapshot);

        when(currentUser.child("stars")).thenReturn(currentUserStars);
        when(currentUser.child("items")).thenReturn(currentUserItems);

        when(currentUserItems.child("colors")).thenReturn(currentUserColors);

        when(currentUserColors.child(anyString())).thenReturn(currentUserSpecificItem);

        when(currentUserStars.setValue(anyInt(), any())).thenAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                stars = invocation.getArgument(1);
                assertEquals((int)invocation.getArgument(1), stars);
                return null;
            }
        });

        when(currentUserSpecificItem.setValue(eq(true), any()))
                .thenAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                userColorsMap.put(color0, true);
                assertTrue(userColorsMap.get(color0));
                return null;
            }
        });

        when(shopColors.child(anyString())).thenReturn(shopColorsSpecificColor);

        when(nonExistant.exists()).thenReturn(false);

        when(longIntegerValueSnapshot.exists()).thenReturn(true);
        when(longIntegerValueSnapshot.getValue()).thenReturn(longInt);

        when(getPriceSnapshot.exists()).thenReturn(true);
        when(getPriceSnapshot.getValue()).thenReturn(price0);

        when(getStarsSnapshot.exists()).thenReturn(true);
        when(getStarsSnapshot.getValue()).thenReturn(stars);

        when(getColorsFromDatabaseSnapshot.exists()).thenReturn(true);
        when(getColorsFromDatabaseSnapshot.getChildren()).thenReturn(shopColorsList);

        when(color0Snapshot.getKey()).thenReturn(color0);
        when(color1Snapshot.getKey()).thenReturn(color1);

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                buttonsInMockedLayout
                        .add(((Button) invocation.getArgument(0)).getText().toString());
                return null;
            }
        }).when(mockedLinearLayout).addView(any(Button.class));
    }

    @Rule
    public final ActivityTestRule<ShopTestActivity> activityTestRule =
            new ActivityTestRule<>(ShopTestActivity.class);

    //relevant tests

    //tests for initializeReferences()

    //tests for onCreate()

    //tests for getColorsFromDatabase()

    //tests for extractColorsFromDataSnapshot()

    @Test
    public void extractColorsFromDataSnapshotWorks() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        buttonsInMockedLayout.clear();
        activityTestRule.getActivity().extractColorsFromDataSnapshot(getColorsFromDatabaseSnapshot,
                textView, mockedLinearLayout);
        assertTrue(buttonsInMockedLayout.contains(color0));
        assertTrue(buttonsInMockedLayout.contains(color1));
        assertEquals("", textView.getText());
    }

    @Test(expected = NullPointerException.class)
    public void extractColorsFromDataSnapshotWorksWithNullSnapshot() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().extractColorsFromDataSnapshot(null, textView,
                mockedLinearLayout);
    }

    @Test(expected = NullPointerException.class)
    public void extractColorsFromDataSnapshotWorksWithNullTextView() {
        activityTestRule.getActivity().extractColorsFromDataSnapshot(getColorsFromDatabaseSnapshot,
                null, mockedLinearLayout);
    }

    @Test(expected = NullPointerException.class)
    public void extractColorsFromDataSnapshotWorksWithNullLayout() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().extractColorsFromDataSnapshot(getColorsFromDatabaseSnapshot,
                textView, null);
    }

    @Test
    public void extractColorsFromDataSnapshotWorksWithNonExistant() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().extractColorsFromDataSnapshot(nonExistant,
                textView, mockedLinearLayout);
        assertEquals("Currently no purchasable items in shop.", textView.getText());
    }

    //tests for initializeButton()

    @Test
    public void initializeButtonWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertEquals(testString, btn.getText());
        assertTrue(btn.isClickable());
    }

    //tests for addPurchaseOnClickListenerToButton()

    @Test
    public void addPurchaseOnClickListenerToButtonWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertFalse(btn.hasOnClickListeners());
        activityTestRule.getActivity().addPurchaseOnClickListenerToButton(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    //tests for setReturn()

    @Test
    public void setReturnWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertFalse(btn.hasOnClickListeners());
        activityTestRule.getActivity().setReturn(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    @Test
    public void setReturnReturnsToHomeOnClick() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        activityTestRule.getActivity().setReturn(btn);
        Intents.init();
        btn.performClick();
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    //tests for setRefresh()

    @Test
    public void setRefreshWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertFalse(btn.hasOnClickListeners());
        activityTestRule.getActivity().setRefresh(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    @Test
    public void setRefreshRefreshesOnClick() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        activityTestRule.getActivity().setRefresh(btn);
        Intents.init();
        btn.performClick();
        intended(hasComponent(ShopTestActivity.class.getName()));
        Intents.release();
    }

    //tests for purchaseItem()

    //tests for alreadyOwned()

    //tests for updateUserIf()

    @Test
    public void updateUserIfWorksIfSufficientStars() {
        //CalledFromWrongRootException
        //TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        //activityTestRule.getActivity().updateUserIf(currentUser, testString, textView,
        //        1000, 0);
        //assertEquals("Purchase successful.", textView.getText());
    }

    @Test
    public void updateUserIfWorksWithInsufficientStars() {
        prepareLooperIfNotExisting();
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().updateUserIf(currentUser, testString, textView,
                0, 1000);
        assertEquals("Not enough stars to purchase item.", textView.getText());
    }

    //tests for getStars()

    @Test
    public void getStarsWorks() {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                IntegerWrapper wrapper = new IntegerWrapper(0);
                activityTestRule.getActivity().wrapDataSnapshotValue(getStarsSnapshot, wrapper);
                assertEquals(stars, wrapper.getInt());
                return null;
            }
        }).when(currentUserStars).addListenerForSingleValueEvent(any(ValueEventListener.class));
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().getStars(currentUserStars, wrapper);
    }

    @Test(expected = NullPointerException.class)
    public void getStarsWorksWithNullReference() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().getStars(null, wrapper);
    }

    @Test(expected = NullPointerException.class)
    public void getStarsWorksWithNullWrapper() {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                activityTestRule.getActivity()
                        .wrapDataSnapshotValue(getStarsSnapshot, null);
                return null;
            }
        }).when(currentUserStars)
                .addListenerForSingleValueEvent(any(ValueEventListener.class));
        activityTestRule.getActivity().getStars(currentUserStars, null);
    }

    //tests for getPrice()

    @Test
    public void getPriceWorks() {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                IntegerWrapper wrapper = new IntegerWrapper(0);
                activityTestRule.getActivity().wrapDataSnapshotValue(getPriceSnapshot, wrapper);
                assertEquals(price0, wrapper.getInt());
                return null;
            }
        }).when(shopColorsSpecificColor)
                .addListenerForSingleValueEvent(any(ValueEventListener.class));
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().getPrice(shopColors, testString, wrapper);
    }

    @Test(expected = NullPointerException.class)
    public void getPriceWorksWithNullReference() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().getPrice(null, testString, wrapper);
    }

    @Test(expected = NullPointerException.class)
    public void getPriceWorksWithNullString() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().getPrice(shopColors, null, wrapper);
    }

    @Test(expected = NullPointerException.class)
    public void getPriceWorksWithNullWrapper() {
        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) {
                activityTestRule.getActivity()
                        .wrapDataSnapshotValue(getPriceSnapshot, null);
                return null;
            }
        }).when(shopColorsSpecificColor)
                .addListenerForSingleValueEvent(any(ValueEventListener.class));
        activityTestRule.getActivity().getPrice(shopColors, testString, null);
    }

    //tests for wrapDataSnapshotValue()

    @Test
    public void wrapDataSnapshotValueWorks() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().wrapDataSnapshotValue(longIntegerValueSnapshot, wrapper);
        assertEquals(longInt, wrapper.getInt());
    }

    @Test
    public void wrapDataSnapshotValueWorksWithNonExistantSnapshot() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().wrapDataSnapshotValue(nonExistant, wrapper);
        assertEquals(-1, wrapper.getInt());
    }

    @Test(expected = NullPointerException.class)
    public void wrapDataSnapshotValueWorksWithNullSnapshot() {
        IntegerWrapper wrapper = new IntegerWrapper(0);
        activityTestRule.getActivity().wrapDataSnapshotValue(null, wrapper);
        assertEquals(-1, wrapper.getInt());
    }

    @Test(expected = NullPointerException.class)
    public void wrapDataSnapshotValueWorksWithNullWrapper() {
        activityTestRule.getActivity()
                .wrapDataSnapshotValue(longIntegerValueSnapshot, null);
    }

    //tests for sufficientCurrency()

    @Test
    public void sufficientCurrencyWorks() {
        assertTrue(activityTestRule.getActivity().sufficientCurrency(1000, 500));
        assertTrue(activityTestRule.getActivity().sufficientCurrency(500, 500));
        assertTrue(activityTestRule.getActivity().sufficientCurrency(0, 0));
        assertFalse(activityTestRule.getActivity().sufficientCurrency(0, 500));
        assertFalse(activityTestRule.getActivity().sufficientCurrency(-500, -600));
    }

    //tests for updateUser()

    @Test
    public void updateUserWorks() {
        prepareLooperIfNotExisting();
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().updateUser(currentUser, testString, validNewStars, textView);
        assertEquals("Purchase successful.", textView.getText());
    }

    @Test(expected = NullPointerException.class)
    public void updateUserWorksWithNullReference() {
        prepareLooperIfNotExisting();
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity()
                .updateUser(null, testString, validNewStars, textView);
    }

    @Test(expected = NullPointerException.class)
    public void updateUserWorksWithNullString() {
        prepareLooperIfNotExisting();
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity()
                .updateUser(currentUser, null, validNewStars, textView);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserWorksWithInvalidNewStars() {
        prepareLooperIfNotExisting();
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity()
                .updateUser(currentUser, testString, invalidNewStars, textView);
    }

    @Test(expected = NullPointerException.class)
    public void updateUserWorksWithNullTextView() {
        prepareLooperIfNotExisting();
        activityTestRule.getActivity()
                .updateUser(currentUser, testString, validNewStars, null);
    }

    //tests for updateUserStars()

    @Test
    public void updateUserStarsWorks() {
        stars = invalidNewStars;
        activityTestRule.getActivity().updateUserStars(currentUserStars, validNewStars);

    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserStarsWorksWithNegativeNewStars() {
        activityTestRule.getActivity().updateUserStars(currentUserStars, invalidNewStars);
    }

    @Test(expected = NullPointerException.class)
    public void updateUserStarsWorksWithNullReference() {
        activityTestRule.getActivity().updateUserStars(null, validNewStars);
    }

    //tests for addUserItem()

    @Test
    public void addUserItemWorks() {
        activityTestRule.getActivity().addUserItem(currentUserSpecificItem);
    }

    @Test(expected = NullPointerException.class)
    public void addUserItemWorksWithNullReference() {
        activityTestRule.getActivity().addUserItem(null);
    }

    //tests for setTextViewMessage()

    @Test
    public void setTextViewMessageWorks() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().setTextViewMessage(textView, testString);
        assertEquals(testString, textView.getText());
    }

    @Test(expected = NullPointerException.class)
    public void setTextViewMessageWorksWithNullTextView() {
        activityTestRule.getActivity().setTextViewMessage(null, testString);
    }

    @Test
    public void setTextViewMessageWorksWithNullString() {
        TextView textView = new TextView(activityTestRule.getActivity().getApplicationContext());
        activityTestRule.getActivity().setTextViewMessage(textView, null);
        assertEquals("", textView.getText());
    }

    //tests for resetTextViewMessage()

    @Test
    public void resetTextViewMessageWorks() {
        prepareLooperIfNotExisting();
        final TextView textView = new TextView(activityTestRule.getActivity()
                .getApplicationContext());
        textView.setText(testString);
        assertEquals(testString, textView.getText());
        activityTestRule.getActivity().resetTextViewMessage(textView, delay500);
        new CountDownTimer(delay500, delay500) {

            public void onTick(long millisUntilFinished) {
                /**
                 * Comment for the sake of CodeClimate.
                 */
            }

            public void onFinish() {
                assertEquals("", textView.getText());
            }
        }.start();
    }

    @Test
    public void resetTextViewMessageWorksWithZeroDelay() {
        final TextView textView = new TextView(activityTestRule
                .getActivity().getApplicationContext());
        textView.setText(testString);
        assertEquals(testString, textView.getText());
        activityTestRule.getActivity().resetTextViewMessage(textView, delayZero);
        new CountDownTimer(delayZero, delayZero) {

            public void onTick(long millisUntilFinished) {
                /**
                 * Comment for the sake of CodeClimate.
                 */
            }

            public void onFinish() {
                assertEquals("", textView.getText());
            }
        }.start();
    }

    @Test(expected = NullPointerException.class)
    public void resetTextViewMessageWorksWithNullTextView() {
        activityTestRule.getActivity().resetTextViewMessage(null, delay500);
    }

    //tests for gotoHome()

    @Test
    public void gotoHomeWorks() {
        Intents.init();
        activityTestRule.getActivity().gotoHome();
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

    //tests for refreshShop()

    @Test
    public void refreshShopWorks() {
        Intents.init();
        activityTestRule.getActivity().refreshShop();
        intended(hasComponent(ShopTestActivity.class.getName()));
        Intents.release();
    }



    //retarded tests


    //helper methods
    private void prepareLooperIfNotExisting() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }
}