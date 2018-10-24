package ch.epfl.sweng.SDP.shop;

import android.view.View;
import android.widget.Button;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

@RunWith(AndroidJUnit4.class)
public class ShopActivityTest {
    private final String testString = "testString";
    private final FirebaseDatabase db = FirebaseDatabase
            .getInstance("https://gyrodraw.firebaseio.com/");
    private final DatabaseReference dbRef = db.getReference();
    private final DatabaseReference testUsersReference = dbRef.child("testUsers");
    private final DatabaseReference testShopColorsReference = dbRef.child("testItems")
            .child("colors");
    private final DatabaseReference testShopNoColorsReference = dbRef.child("testItems")
            .child("noColors");

    private final HashSet<String> colors =
            new HashSet<>(Arrays.asList("testColor0", "testColor1", "testColor2"));
    private final IntegerWrapper integerWrapper = new IntegerWrapper(-1);


    @Rule
    public final ActivityTestRule<ShopTestActivity> activityTestRule =
            new ActivityTestRule<>(ShopTestActivity.class);

    @Test
    public void returnIsClickable() {
        onView(ViewMatchers.withId(R.id.returnFromShop)).check(matches(isClickable()));
    }

    @Test
    public void refreshIsClickable() {
        onView(ViewMatchers.withId(R.id.refreshShop)).check(matches(isClickable()));
    }

    @Test
    public void initializeButtonWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertEquals(testString, btn.getText());
    }

    @Test
    public void addOnClickListenerToButtonWorks() {
        Button btn = activityTestRule.getActivity().initializeButton(testString);
        assertTrue(!btn.hasOnClickListeners());
        activityTestRule.getActivity().addOnClickListenerToButton(btn);
        assertTrue(btn.hasOnClickListeners());
    }

    @Test
    public void getColorsFromDatabaseWorksWithCorrectRef() {
        LinearLayout linearLayout = activityTestRule.getActivity().findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        activityTestRule.getActivity().getColorsFromDatabase(testShopColorsReference);
        final int childrenCount = linearLayout.getChildCount();
        assertEquals(3, childrenCount);
        HashSet<String> found = new HashSet<>();
        for (int i = 0; i < childrenCount; i++) {
            found.add(((Button)linearLayout.getChildAt(i)).getText().toString());
        }
        assertTrue(colors.equals(found));
    }

    @Test
    public void getColorsFromDatabaseWorksWithNoColorsAvailable() {
        LinearLayout linearLayout = activityTestRule.getActivity().findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        activityTestRule.getActivity().getColorsFromDatabase(testShopNoColorsReference);
        final int childrenCount = linearLayout.getChildCount();
        assertEquals(0, childrenCount);
        TextView textView = activityTestRule.getActivity().findViewById(R.id.shopMessages);
        String currentMessage = textView.getText().toString();
        assertEquals(currentMessage, "Currently no purchasable items in shop.");
    }


    //fix with @expected
    @Test
    public void getColorsFromDatabaseWorksWithInvalidRef() {
        LinearLayout linearLayout = activityTestRule.getActivity().findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        activityTestRule.getActivity().getColorsFromDatabase(null);
    }

    @Test
    public void sufficientCurrencyWorks() {
        assertTrue(activityTestRule.getActivity().sufficientCurrency(3,0));
        assertTrue(activityTestRule.getActivity().sufficientCurrency(3,3));
        assertTrue(!activityTestRule.getActivity().sufficientCurrency(0, 3));
    }

    @Test
    public void getStarsWorksWithCorrectRef() {

    }

    @Test
    public void getStarsWorksOnNonExistantStars() {

    }

    //fix with @expected
    @Test
    public void getStarsWorksOnCancelled() {

    }

}
