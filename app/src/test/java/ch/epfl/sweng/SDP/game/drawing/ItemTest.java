package ch.epfl.sweng.SDP.game.drawing;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ItemTest {

    @Test
    public void testItemGetsInitializedCorrectly() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10, 10);
        assertEquals(speedupItem.x, 0);
        assertEquals(speedupItem.y, 0);
        assertEquals(speedupItem.radius, 10);
        assertEquals(speedupItem.interval, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializeItemWithIllegalArguments() {
        SpeedupItem.createSpeedupItem(-1, -1, -10, -10);
    }

    @Test
    public void testCollisions() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10, 10);
        assertFalse(speedupItem.collision(20, 0, 10));
        assertTrue(speedupItem.collision(20, 0, 11));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollisionsWithIllegalArguments() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10, 10);
        speedupItem.collision(-20, -10, -10);
    }

    @Test
    public void testTextFeedbacksFromDifferentItemClasses() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10, 10);
        assertEquals(speedupItem.textFeedback(), "SPEEDUP! ");
        SlowdownItem slowdownItem = SlowdownItem.createSlowdownItem(0, 0, 10, 10);
        assertEquals(slowdownItem.textFeedback(), "SLOWDOWN! ");
        SwapAxisItem swapAxisItem = SwapAxisItem.createSwapAxisItem(0, 0, 10, 10);
        assertEquals(swapAxisItem.textFeedback(), "SWAPPED! ");
    }
}