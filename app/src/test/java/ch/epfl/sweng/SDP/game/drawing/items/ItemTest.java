package ch.epfl.sweng.SDP.game.drawing.items;

import org.junit.Test;

import ch.epfl.sweng.SDP.R;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ItemTest {

    @Test
    public void testItemGetsInitializedCorrectly() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10);
        assertThat(speedupItem.x, is(0));
        assertThat(speedupItem.y, is(0));
        assertThat(speedupItem.radius, is(10));
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
        assertThat(speedupItem.getTextFeedback(), is("SPEEDUP ! "));
        SlowdownItem slowdownItem = SlowdownItem.createSlowdownItem(0, 0, 10);
        assertThat(slowdownItem.getTextFeedback(), is("SLOWDOWN ! "));
        SwapAxisItem swapAxisItem = SwapAxisItem.createSwapAxisItem(0, 0, 10);
        assertThat(swapAxisItem.getTextFeedback(), is("SWAPPED ! "));
        AddStarsItem addStarsItem = AddStarsItem.createAddStarsItem(0, 0, 10);
        assertThat(addStarsItem.getTextFeedback(), is("+3 STARS ! "));
        BumpingItem bumpingItem = BumpingItem.createBumpingItem(0, 0, 10);
        assertThat(bumpingItem.getTextFeedback(), is(" "));
    }

    @Test
    public void testItemsHaveTheRightColor() {
        SpeedupItem speedupItem = SpeedupItem.createSpeedupItem(0, 0, 10);
        assertThat(speedupItem.getColorId(), is(R.color.colorExitRed));
        SlowdownItem slowdownItem = SlowdownItem.createSlowdownItem(0, 0, 10);
        assertThat(slowdownItem.getColorId(), is(R.color.colorGreen));
        SwapAxisItem swapAxisItem = SwapAxisItem.createSwapAxisItem(0, 0, 10);
        assertThat(swapAxisItem.getColorId(), is(R.color.colorExitRed));
        AddStarsItem addStarsItem = AddStarsItem.createAddStarsItem(0, 0, 10);
        assertThat(addStarsItem.getColorId(), is(R.color.colorGreen));
        BumpingItem bumpingItem = BumpingItem.createBumpingItem(0, 0, 10);
        assertThat(bumpingItem.getColorId(), is(R.color.colorExitRed));
    }
}