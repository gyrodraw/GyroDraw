package ch.epfl.sweng.SDP.home;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import ch.epfl.sweng.SDP.BooleanVariableListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
        assertThat(boolSpy.getBoo(), is(true));
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
        assertThat(boolSpy.getListener(), is(testChangeListener));
        assertThat(varTest, is(5));
    }
}
