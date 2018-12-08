package ch.epfl.sweng.SDP.utils;

import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.ONLINE;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OnlineStatusTest {

    private static final String USER_ID = "123456789";

    private Context context;

    @Before
    public void init() {
        context = InstrumentationRegistry.getContext();

        Account account = Account.getInstance(context);
        account.setUserId(USER_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeOnlineStatusNullContext() {
        OnlineStatus.changeOnlineStatus(null, OFFLINE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeOnlineStatusUnknownStatus() {
        OnlineStatus.changeOnlineStatus(context, null);
    }

    @Test
    public void testChangeOnlineStatusOnline() {
        OnlineStatus.changeOnlineStatus(context, ONLINE);
        assertOnlineStatus(ONLINE.ordinal());
    }

    @Test
    public void testChangeOnlineStatusOffline() {
        OnlineStatus.changeOnlineStatus(context, OFFLINE);
        assertOnlineStatus(OFFLINE.ordinal());
    }


    private void assertOnlineStatus(final int status) {
        Database.getReference(format("users.%s.online", Account.getInstance(context).getUserId()))
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
