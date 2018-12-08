package ch.epfl.sweng.SDP.utils;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NetworkStateReceiverTest {

    @Test
    public void testInternetConnection() {
        assertThat(NetworkStateReceiver.isOnline(InstrumentationRegistry.getContext()), is(true));
    }
}
