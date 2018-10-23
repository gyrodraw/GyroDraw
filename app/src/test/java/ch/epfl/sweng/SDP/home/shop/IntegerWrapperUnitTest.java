package ch.epfl.sweng.SDP.home.shop;

import ch.epfl.sweng.SDP.shop.IntegerWrapper;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IntegerWrapperUnitTest {
    private final int testIntOne = 100;
    private final int testIntTwo = 200;
    private IntegerWrapper wrapper = new IntegerWrapper(testIntOne);

    @Test
    public void getterWorksCorrectly() {
        assertEquals(testIntOne, wrapper.getInt());
    }

    @Test
    public void setterAndGetterWorkCorrectly() {
        wrapper.setInt(testIntTwo);
        assertEquals(testIntTwo, wrapper.getInt());
    }
}
