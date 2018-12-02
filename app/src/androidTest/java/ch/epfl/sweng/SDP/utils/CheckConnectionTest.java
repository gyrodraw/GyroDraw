package ch.epfl.sweng.SDP.utils;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import ch.epfl.sweng.SDP.firebase.CheckConnection;

import static org.junit.Assert.assertTrue;

public class CheckConnectionTest {

    @Test
    public void testInternetConnection() {
        assertTrue(CheckConnection.isOnline(InstrumentationRegistry.getContext()));
    }
}
