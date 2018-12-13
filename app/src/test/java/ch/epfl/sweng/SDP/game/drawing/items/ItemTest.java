package ch.epfl.sweng.SDP.game.drawing.items;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import ch.epfl.sweng.SDP.R;
import org.junit.Test;

public class ItemTest {

    @Test
    public void testItemGetsInitializedCorrectly() {
        SpeedupItem speedupItem = new SpeedupItem(0, 0, 10);
        assertThat(speedupItem.getX(), is(0));
        assertThat(speedupItem.getY(), is(0));
        assertThat(speedupItem.getRadius(), is(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitializeItemWithIllegalArguments() {
        new SpeedupItem(-1, -1, -10);
    }

    @Test
    public void testCollisions() {
        SpeedupItem speedupItem = new SpeedupItem(0, 0, 10);
        assertThat(speedupItem.collision(21, 0, 10), is(false));
        assertThat(speedupItem.collision(20, 0, 11), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateItemWithIllegalArguments() {
        new SpeedupItem(-10, -10, -10);
    }

    @Test
    public void testTextFeedbacksFromDifferentItemClasses() {
        SpeedupItem speedupItem = new SpeedupItem(0, 0, 10);
        assertThat(speedupItem.getTextFeedback(), is("SPEEDUP ! "));
        SlowdownItem slowdownItem = new SlowdownItem(0, 0, 10);
        assertThat(slowdownItem.getTextFeedback(), is("SLOWDOWN ! "));
        SwapAxisItem swapAxisItem = new SwapAxisItem(0, 0, 10);
        assertThat(swapAxisItem.getTextFeedback(), is("SWAPPED ! "));
        AddStarsItem addStarsItem = new AddStarsItem(0, 0, 10);
        assertThat(addStarsItem.getTextFeedback(), is("+3 STARS ! "));
        BumpingItem bumpingItem = new BumpingItem(0, 0, 10);
        assertThat(bumpingItem.getTextFeedback(), is(" "));
    }

    @Test
    public void testItemsHaveTheRightColor() {
        SpeedupItem speedupItem = new SpeedupItem(0, 0, 10);
        assertThat(speedupItem.getColorId(), is(R.color.colorExitRed));
        SlowdownItem slowdownItem = new SlowdownItem(0, 0, 10);
        assertThat(slowdownItem.getColorId(), is(R.color.colorGreen));
        SwapAxisItem swapAxisItem = new SwapAxisItem(0, 0, 10);
        assertThat(swapAxisItem.getColorId(), is(R.color.colorExitRed));
        AddStarsItem addStarsItem = new AddStarsItem(0, 0, 10);
        assertThat(addStarsItem.getColorId(), is(R.color.colorGreen));
        BumpingItem bumpingItem = new BumpingItem(0, 0, 10);
        assertThat(bumpingItem.getColorId(), is(R.color.colorExitRed));
    }
}