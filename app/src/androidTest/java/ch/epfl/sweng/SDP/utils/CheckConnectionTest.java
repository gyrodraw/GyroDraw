package ch.epfl.sweng.SDP.firebase;

import android.support.test.InstrumentationRegistry;

import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

public class CheckConnectionTest {

    @Test
    public void testInternetConnection() {
        assertTrue(CheckConnection.isOnline(InstrumentationRegistry.getContext()));
    }
}
