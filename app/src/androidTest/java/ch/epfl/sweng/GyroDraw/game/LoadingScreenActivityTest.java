package ch.epfl.sweng.GyroDraw.game;

import static ch.epfl.sweng.GyroDraw.firebase.RoomAttributes.WORDS;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.disableLoadingAnimations;

import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import com.google.firebase.database.DataSnapshot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoadingScreenActivityTest {

    @Rule
    public final ActivityTestRule<LoadingScreenActivity> activityRule =
            new ActivityTestRule<LoadingScreenActivity>(LoadingScreenActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    disableLoadingAnimations();
                }
            };

    @Test
    public void testWordsReady() {
        activityRule.getActivity().listenerRoomReady.onChange();

        FbDatabase.getRoomAttribute(activityRule.getActivity().getRoomId(), WORDS,
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        activityRule.getActivity().listenerWords.onDataChange(dataSnapshot);
                    }
                });
    }
}
