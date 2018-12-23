package ch.epfl.sweng.GyroDraw.utils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BooleanVariableListenerUnitTest {

    private BooleanVariableListener boolSpy;
    private int varTest = 0;

    @Before
    public void init() {
        boolSpy = Mockito.spy(new BooleanVariableListener());
    }

    @Test
    public void setValueTest() {
        boolSpy.setBool(true);
        assertThat(boolSpy.getBool(), is(true));
    }

    @Test
    public void listenerTest() {
        BooleanVariableListener.ChangeListener testChangeListener = new BooleanVariableListener.ChangeListener() {
            @Override
            public void onChange() {
                varTest = 5;
            }
        };

        boolSpy.setListener(testChangeListener);
        boolSpy.setBool(true);
        assertThat(boolSpy.getListener(), is(testChangeListener));
        assertThat(varTest, is(5));
    }
}