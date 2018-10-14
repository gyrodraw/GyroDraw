package ch.epfl.sweng.SDP;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccountUnitTest {

    @Test(expected = NullPointerException.class)
    public void NullUsernameConstructorOne() {
        Account acc =  new Account(null);
    }

    @Test(expected = NullPointerException.class)
    public void NullUsernameConstructorTwo() {
        Account acc = new Account(null, 0, 0);
    }

    @Test
    public void accountHasRightUsername() {
        Account acc = new Account("Max Muster");
        assertEquals("Max Muster", acc.getUsername());
    }
}