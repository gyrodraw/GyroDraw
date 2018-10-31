package ch.epfl.sweng.SDP.home;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.epfl.sweng.SDP.BooleanVariableListener;

import static org.junit.Assert.assertEquals;

public class BooleanListenerUnitTest {

    private BooleanVariableListener boolSpy;
    private BooleanVariableListener.ChangeListener testChangeListener;
    private int varTest = 0;

    @Before
    public void init() {
        boolSpy = Mockito.spy(new BooleanVariableListener());
    }

    @Test
    public void setValueTest() {
        boolSpy.setBoo(true);
        assertEquals(boolSpy.getBoo(), true);
    }

    @Test
    public void listenerTest() {
        testChangeListener = new BooleanVariableListener.ChangeListener() {
            @Override
            public void onChange() {
                varTest = 5;
            }
        };

        boolSpy.setListener(testChangeListener);
        boolSpy.setBoo(true);
        assertEquals(boolSpy.getListener(), testChangeListener);
        assertEquals(varTest, 5);
    }
}
