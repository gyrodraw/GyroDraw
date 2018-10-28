package ch.epfl.sweng.SDP.firebase;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class ConnectionTest {

    @Test
    public void testInternetConnection() {
        assertTrue(Connection.isOnline(InstrumentationRegistry.getContext()));
    }
}
