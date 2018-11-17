package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Random;

public class RandomItemGenerator {

    private static final int INTERVAL = 10000;
    private static final int ITEM_RADIUS = 80;

    PaintView paintView;

    protected RandomItemGenerator(PaintView paintView) {
        this.paintView = paintView;
    }

    protected Item generateItem() {
        Items item = Items.randomItem();
        Random random = new Random();
        int x = 2*ITEM_RADIUS + random.nextInt(paintView.getWidth() - 4*ITEM_RADIUS);
        int y = 2*ITEM_RADIUS + random.nextInt(paintView.getHeight() - 4*ITEM_RADIUS);
        switch (item) {
            case SPEEDUP:
                return SpeedupItem.createSpeedupItem(paintView.getContext(), x, y, ITEM_RADIUS, INTERVAL);
            case SLOWDOWN:
                return SlowdownItem.createSlowdownItem(paintView.getContext(), x, y, ITEM_RADIUS, INTERVAL);
            case SWAPAXIS:
                return SwapAxisItem.createSwapAxisItem(paintView.getContext(), x, y, ITEM_RADIUS, INTERVAL);
            //case ADDSTARS:
            //    return AddStarsItem.createAddStarsItem(paintView.getContext(), x, y, ITEM_RADIUS, INTERVAL);
            //case LOSESTARS:
            //    return LoseStarsItem.createLoseStarsItem(paintView.getContext(), x, y, ITEM_RADIUS, INTERVAL);

            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }



}
