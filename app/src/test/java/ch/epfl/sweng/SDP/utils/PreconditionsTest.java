package ch.epfl.sweng.SDP.utils;

import org.junit.Test;

public class PreconditionsTest {

    @Test
    public void testCheckPreconditionWithoutMsgTrue() {
        Preconditions.checkPrecondition(1 > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckPreconditionWithoutMsgFalse() {
        Preconditions.checkPrecondition(0 > 1);
    }

    @Test
    public void testCheckPreconditionWithMsgTrue() {
        Preconditions.checkPrecondition(1 > 0, "should not be displayed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckPreconditionWithMsgFalse() {
        Preconditions.checkPrecondition(0 > 1, "Impossible!");
    }

}
