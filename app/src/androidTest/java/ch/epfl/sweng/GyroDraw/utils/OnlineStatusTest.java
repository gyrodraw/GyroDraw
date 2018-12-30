package ch.epfl.sweng.GyroDraw.utils;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;

import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.STATUS;
import static ch.epfl.sweng.GyroDraw.firebase.FbDatabase.createCompletionListener;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.ONLINE;
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
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int value = dataSnapshot.getValue(int.class);
                        assertThat(value, is(status));
                    }
                });
    }
}
