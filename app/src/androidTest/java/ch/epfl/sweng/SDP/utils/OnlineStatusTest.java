package ch.epfl.sweng.SDP.utils;

import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.ONLINE;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.SDP.firebase.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OnlineStatusTest {

    private static final String USER_ID = "no_user";

    @Test(expected = IllegalArgumentException.class)
    public void testChangeOnlineStatusNullContext() {
        OnlineStatus.changeOnlineStatus(null, OFFLINE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeOnlineStatusUnknownStatus() {
        OnlineStatus.changeOnlineStatus(USER_ID, null);
    }

    @Test
    public void testChangeOnlineStatusOnline() {
        OnlineStatus.changeOnlineStatus(USER_ID, ONLINE);
        assertOnlineStatus(ONLINE.ordinal());
    }

    @Test
    public void testChangeOnlineStatusOffline() {
        OnlineStatus.changeOnlineStatus(USER_ID, OFFLINE);
        assertOnlineStatus(OFFLINE.ordinal());
    }


    private void assertOnlineStatus(final int status) {
        Database.getReference(format("users.%s.online", USER_ID))
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int value = dataSnapshot.getValue(int.class);
                                assertThat(value, is(status));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                throw databaseError.toException();
                            }
                        });
    }
}
