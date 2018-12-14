package ch.epfl.sweng.SDP.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CheckConnectionTest {

    @Test
    public void testInternetConnection() {
        assertThat(CheckConnection.isOnline(InstrumentationRegistry.getContext()), is(true));
    }
}
