package ch.epfl.sweng.GyroDraw.game;

import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.FRIENDS;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.ROOM_ID;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.WORD_1;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.WORD_2;
import static ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity.disableLoadingAnimations;
import static ch.epfl.sweng.GyroDraw.home.HomeActivity.GAME_MODE;
import static org.mockito.ArgumentMatchers.isA;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class LoadingScreenActivityTest {

    private static final String USER_ID = "123456789";

    @Rule
    public final ActivityTestRule<LoadingScreenActivity> activityRule =
            new ActivityTestRule<LoadingScreenActivity>(LoadingScreenActivity.class) {

                @Override
                protected void beforeActivityLaunched() {
                    disableLoadingAnimations();
                }

                @Override
                protected Intent getActivityIntent() {
                    Intent intent = new Intent();
                    intent.putExtra(ROOM_ID, "1234567890");
                    intent.putExtra(WORD_1, "lalala");
                    intent.putExtra(WORD_2, "lilili");
                    intent.putExtra(GAME_MODE, "mode");
                    return intent;
                }
            };

    @Test
    public void testWordsReady() {
        activityRule.getActivity().wordsVotesRef = Mockito.mock(DatabaseReference.class);
        Mockito.doNothing().when(activityRule.getActivity().wordsVotesRef)
                .removeEventListener(isA(ValueEventListener.class));
        activityRule.getActivity().isRoomReady.setBool(true);
        activityRule.getActivity().areWordsReady.setBool(true);
        activityRule.getActivity().listenerRoomReady.onChange();

        FbDatabase.getAccountAttribute(USER_ID, FRIENDS,
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        activityRule.getActivity().listenerWords.onDataChange(dataSnapshot);
                    }
                });
    }
}
