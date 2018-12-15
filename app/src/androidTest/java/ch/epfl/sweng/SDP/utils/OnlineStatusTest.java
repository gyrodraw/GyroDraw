package ch.epfl.sweng.SDP.utils;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import ch.epfl.sweng.SDP.firebase.FbDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import static ch.epfl.sweng.SDP.firebase.AccountAttributes.STATUS;
import static ch.epfl.sweng.SDP.firebase.FbDatabase.createCompletionListener;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.ONLINE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class OnlineStatusTest {

    private static final String USER_ID = "no_user";

    @Test(expected = IllegalArgumentException.class)
    public void testChangeOnlineStatusNullContext() {
        OnlineStatus.changeOnlineStatus(null, OFFLINE, createCompletionListener());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangeOnlineStatusUnknownStatus() {
        OnlineStatus.changeOnlineStatus(USER_ID, null, createCompletionListener());
    }

    @Test
    public void testChangeOnlineStatusOnline() {
        OnlineStatus.changeOnlineStatus(USER_ID, ONLINE, createCompletionListener());
        assertOnlineStatus(ONLINE.ordinal());
    }

    @Test
    public void testChangeOnlineStatusOffline() {
        OnlineStatus.changeOnlineStatus(USER_ID, OFFLINE, createCompletionListener());
        assertOnlineStatus(OFFLINE.ordinal());
    }


    private void assertOnlineStatus(final int status) {
        FbDatabase.getAccountAttribute(USER_ID, STATUS,
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
