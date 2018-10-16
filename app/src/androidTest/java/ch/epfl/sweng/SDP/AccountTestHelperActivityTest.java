package ch.epfl.sweng.SDP;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountTestHelperActivityTest {

    @Rule
    public final ActivityTestRule<AccountTestHelperActivity> accountFunctionsActivityRule =
            new ActivityTestRule<>(AccountTestHelperActivity.class);

    @Test
    public void testAccountFunctions1() {
        onView(withId(R.id.button1)).perform(click());
    }

    @Test
    public void testAccountFunctions2() {
        onView(withId(R.id.button2)).perform(click());
    }

    @Test
    public void testAccountFunctions3() {
        onView(withId(R.id.button3)).perform(click());
    }

    @Test
    public void testAccountFunctions4() {
        onView(withId(R.id.button4)).perform(click());
    }

    @Test
    public void testAccountFunctions5() {
        onView(withId(R.id.button5)).perform(click());
    }

    @Test
    public void testAccountFunctions6() {
        onView(withId(R.id.button6)).perform(click());
    }

    @Test
    public void testAccountFunctions7() {
        onView(withId(R.id.button7)).perform(click());
    }

    @Test
    public void testAccountFunctions8() {
        onView(withId(R.id.button8)).perform(click());
    }

    @Test
    public void testAccountFunctions9() {
        onView(withId(R.id.button9)).perform(click());
    }

    @Test
    public void testAccountFunctions10() {
        onView(withId(R.id.button10)).perform(click());
    }

    @Test
    public void testAccountFunctions11() {
        onView(withId(R.id.button10)).perform(click());
    }
}