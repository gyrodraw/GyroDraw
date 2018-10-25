package ch.epfl.sweng.SDP;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class StorageHandlingTestViewActivityTest {

    @Rule
    public final ActivityTestRule<StorageHandlingTestView> activityRule =
            new ActivityTestRule<>(StorageHandlingTestView.class);

    @Test
    public void testGetFromEmptyDb() {
        onView(ViewMatchers.withId(R.id.button1)).perform(click());
    }

    @Test
    public void testAddAndRetrieveSuccessfully() {
        onView(withId(R.id.button2)).perform(click());
    }

    @Test
    public void testPutAndGetFromStorage() {
        onView(withId(R.id.button3)).perform(click());
    }

    @Test
    public void testGetNullFromStorage() {
        onView(withId(R.id.button4)).perform(click());
    }

    @Test
    public void testOverrideDatabase() {
        onView(withId(R.id.button5)).perform(click());
    }
}
