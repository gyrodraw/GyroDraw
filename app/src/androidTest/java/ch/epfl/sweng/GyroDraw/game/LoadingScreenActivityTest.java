package ch.epfl.sweng.GyroDraw.game;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.disableLoadingAnimations;

@RunWith(AndroidJUnit4.class)
public class LoadingScreenActivityTest {

    @Rule
    public final ActivityTestRule<LoadingScreenActivity> mActivityRule =
            new ActivityTestRule<LoadingScreenActivity>(LoadingScreenActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    disableLoadingAnimations();
                }

            };

    @Test
    public void testWordsReady() {
        ArrayList<String> words = new ArrayList<>();
        mActivityRule.getActivity().areWordsReady(words);
    }
}
