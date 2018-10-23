package ch.epfl.sweng.SDP.game;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import ch.epfl.sweng.SDP.R;
import static android.os.SystemClock.sleep;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StarAnimationViewTest {

    @Rule
    public final ActivityTestRule<VotingPageActivity> mActivityRule =
            new ActivityTestRule<>(VotingPageActivity.class);

    @Test
    public void addStarsHandlesBigNumber() {
        StarAnimationView starsAnimation =  mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
        sleep(5000);
        starsAnimation.addStars(1000);
        assert (5 == starsAnimation.getNumStars());
    }

    @Test
    public void addStarsHandlesNegativeNumber() {
        StarAnimationView starsAnimation =  mActivityRule.getActivity()
                .findViewById(R.id.starsAnimation);
        sleep(5000);
        starsAnimation.addStars(-10);
        assert (0 == starsAnimation.getNumStars());
    }
}