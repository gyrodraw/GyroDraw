package ch.epfl.sweng.SDP.game.drawing.items;

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
        assertThat(speedupItem.x, is(0));
        assertThat(speedupItem.y, is(0);
        assertThat(speedupItem.radius, is(10);
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
        assertThat(speedupItem.textFeedback(), is("SPEEDUP! "));
        SlowdownItem slowdownItem = SlowdownItem.createSlowdownItem(0, 0, 10);
        assertThat(slowdownItem.textFeedback(), is("SLOWDOWN! "));
        SwapAxisItem swapAxisItem = SwapAxisItem.createSwapAxisItem(0, 0, 10);
        assertThat(swapAxisItem.textFeedback(), is("SWAPPED! "));
        AddStarsItem addStarsItem = AddStarsItem.createAddStarsItem(0, 0, 10);
        assertThat(addStarsItem.textFeedback(), is("+3 STARS! "));
        BumpingItem bumpingItem = BumpingItem.createBumpingItem(0, 0, 10);
        assertThat(bumpingItem.textFeedback(), is(" "));
    }
}