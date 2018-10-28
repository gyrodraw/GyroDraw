package ch.epfl.sweng.SDP.game;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.sweng.SDP.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.SDP.game.LoadingScreenActivity.disableLoadingAnimations;
import static ch.epfl.sweng.SDP.game.LoadingScreenActivity.setOnTest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class LoadingScreenActivityTest {

    @Rule
    public final ActivityTestRule<LoadingScreenActivity> mActivityRule =
            new ActivityTestRule<LoadingScreenActivity>(LoadingScreenActivity.class) {

            @Override
            protected void beforeActivityLaunched() {
                disableLoadingAnimations();
                setOnTest();
            }
    };

    @Test
    public void drawableMatchTest() {
        onView(withId(R.id.waitingBackgroundAnimation)).perform(click());
        onView(withId(R.id.waitingAnimationDots)).perform(click());
        onView(withId(R.id.waitingBackgroundImage)).perform(click());
    }

    @Test
    public void areViewDisplayed() {
        onView(withId(R.id.waitingBackgroundAnimation)).check(matches(isDisplayed()));
        onView(withId(R.id.waitingAnimationDots)).check(matches(isDisplayed()));
        onView(withId(R.id.waitingBackgroundImage)).check(matches(isDisplayed()));
    }

    @Test
    public void testLookingForRoom() {
        LoadingScreenActivity mock = Mockito.spy(mActivityRule.getActivity());
        doNothing().when(mock).lookingForRoom();

        mock.lookingForRoom();
        verify(mock, atLeastOnce()).lookingForRoom();
    }
}
