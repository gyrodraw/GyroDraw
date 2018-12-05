package ch.epfl.sweng.SDP.utils;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CheckConnectionTest {

    @Test
    public void testInternetConnection() {
        assertThat(CheckConnection.isOnline(InstrumentationRegistry.getContext()), is(true));
    }
}
