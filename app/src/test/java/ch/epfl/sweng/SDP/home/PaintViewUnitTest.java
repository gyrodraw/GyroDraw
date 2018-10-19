package ch.epfl.sweng.SDP.home;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Size;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.jar.Attributes;

import ch.epfl.sweng.SDP.game.drawing.PaintView;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.doNothing;

public class PaintViewUnitTest {

    private PaintView paintView;
    private Context mockContext;
    private AttributeSet mockAttributesSet;

    @Before
    public void init(){
        mockContext = Mockito.mock(Context.class);
        mockAttributesSet = Mockito.mock(AttributeSet.class);
        paintView = new PaintView(mockContext, this.mockAttributesSet);
    }

    @Test
    public void testSetGetCircleX(){
        paintView.setCircleX(10);
        assertEquals(paintView.getCircleX(), 10);
    }

    @Test
    public void testSetGetCircleY(){
        paintView.setCircleY(10);
        assertEquals(paintView.getCircleY(), 10);
    }

    @Test
    public void testSetGetCircleRadius(){
        paintView.setCircleRadius(15);
        assertEquals(paintView.getCircleRadius(), 15);
    }

    @Test
    public void testSetGetDraw(){
        paintView.setDraw(true);
        assertEquals(paintView.getDraw(), true);
    }

    @Test
    public void testSetSizeAndInit(){
        Point point = new Point(100, 100);
        paintView.setCircleRadius(10);
        paintView.setSizeAndInit(point);
        assertEquals(paintView.getCircleX(), 100/2 - 10);
    }
}
