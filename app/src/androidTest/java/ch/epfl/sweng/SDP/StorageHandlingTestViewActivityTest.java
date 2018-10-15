package ch.epfl.sweng.SDP;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StorageHandlingTestViewActivityTest {

    @Rule
    public final ActivityTestRule<StorageHandlingTestView> activityRule =
           new ActivityTestRule<>(StorageHandlingTestView.class);

    @Test
    public void testClickOnButtonAddAndRetrieveDBEntry() {
        onView(withId(R.id.button1)).perform(click());
    }
}
