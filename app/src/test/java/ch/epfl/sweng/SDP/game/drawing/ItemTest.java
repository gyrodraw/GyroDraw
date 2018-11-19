package ch.epfl.sweng.SDP.game.drawing;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ItemTest {

    @Test
    public void testItemGetsInitializedCorrectly() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10);
        assertThat(0, is(equalTo(speedupItem.x)));
        assertEquals(0, is(equalTo(speedupItem.y)));
        assertEquals(10, is(equalTo(speedupItem.radius)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializeItemWithIllegalArguments() {
        SpeedupItem.createSpeedupItem(-1, -1, -10);
    }

    @Test
    public void testCollisions() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10);
        assertFalse(speedupItem.collision(21, 0, 10));
        assertTrue(speedupItem.collision(20, 0, 11));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateItemWithIllegalArguments() {
        SpeedupItem.createSpeedupItem(-10, -10, -10);
    }

    @Test
    public void testTextFeedbacksFromDifferentItemClasses() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10);
        assertEquals("SPEEDUP! ", speedupItem.textFeedback());
        SlowdownItem slowdownItem = SlowdownItem.createSlowdownItem(0, 0, 10);
        assertEquals("SLOWDOWN! ", slowdownItem.textFeedback());
        SwapAxisItem swapAxisItem = SwapAxisItem.createSwapAxisItem(0, 0, 10);
        assertEquals("SWAPPED! ", swapAxisItem.textFeedback());
        AddStarsItem addStarsItem = AddStarsItem.createAddStarsItem(0, 0, 10);
        assertEquals("+10 STARS! ", addStarsItem.textFeedback());
        BumpingItem bumpingItem = BumpingItem.createBumpingItem(0, 0, 10);
        assertEquals(" ", bumpingItem.textFeedback());
    }
}