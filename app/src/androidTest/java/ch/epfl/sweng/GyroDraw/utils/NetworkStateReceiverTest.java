package ch.epfl.sweng.GyroDraw.utils;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import ch.epfl.sweng.GyroDraw.utils.network.ConnectivityWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NetworkStateReceiverTest {
    @Test
    public void testInternetConnection() {
        assertThat(ConnectivityWrapper.isOnline(InstrumentationRegistry.getContext()),
                is(true));
    }
}
