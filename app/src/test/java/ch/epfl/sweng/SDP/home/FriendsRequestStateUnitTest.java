package ch.epfl.sweng.SDP.home;

import org.junit.Test;

import static ch.epfl.sweng.SDP.home.FriendsRequestState.FRIENDS;
import static ch.epfl.sweng.SDP.home.FriendsRequestState.RECEIVED;
import static ch.epfl.sweng.SDP.home.FriendsRequestState.SENT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FriendsRequestStateUnitTest {

    @Test
    public void testFromInteger0() {
        assertThat(FriendsRequestState.fromInteger(0), is(SENT));
    }

    @Test
    public void testFromInteger1() {
        assertThat(FriendsRequestState.fromInteger(1), is(RECEIVED));
    }

    @Test
    public void testFromInteger2() {
        assertThat(FriendsRequestState.fromInteger(2), is(FRIENDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromIntegerWrongInteger() {
        FriendsRequestState.fromInteger(5);
    }
}
