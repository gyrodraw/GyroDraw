package ch.epfl.sweng.SDP.firebase;

import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import ch.epfl.sweng.SDP.utils.CheckConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CheckConnectionTest {

    @Test
    public void testInternetConnection() {
        assertThat(CheckConnection.isOnline(InstrumentationRegistry.getContext()), is(true));
    }
}
