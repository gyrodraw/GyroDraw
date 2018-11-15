package ch.epfl.sweng.SDP.game.drawing;

import android.os.CountDownTimer;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomItemGenerator {

    private static final int INTERVAL = 10000;
    private static final int ITEM_RADIUS = 10;

    private RelativeLayout paintViewHolder;
    private PaintView paintView;

    private LinkedList<Item> activeItems;

    protected RandomItemGenerator(RelativeLayout paintViewHolder, PaintView paintView) {
        this.paintViewHolder = paintViewHolder;
        this.paintView = paintView;
        activeItems = new LinkedList<>();
        generateItems();
    }

    private void generateItems() {
        new CountDownTimer(INTERVAL, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                Item item = fetchItem(Items.randomItem());
                paintViewHolder.addView(item.toView());
                activeItems.add(item);
                generateItems();
            }
        }.start();

    }

    private Item fetchItem(Items item) {
        Random random = new Random();
        int x = random.nextInt(paintViewHolder.getMeasuredWidth());
        int y = random.nextInt(paintViewHolder.getMeasuredHeight());
        switch (item) {
            case SPEEDUP:
                return SpeedupItem.createSpeedupItem(x, y, ITEM_RADIUS);
            case SLOWDOWN:
                return SlowdownItem.createSlowdownItem(x, y, ITEM_RADIUS);
            case SWAPAXIS:
                return SwapAxisItem.createSwapAxisItem(x, y, ITEM_RADIUS);
            case ADDSTARS:
                return AddStarsItem.createAddStarsItem(x, y, ITEM_RADIUS);
            case LOSESTARS:
                return LoseStarsItem.createLoseStarsItem(x, y, ITEM_RADIUS);

            default:
                throw new IllegalArgumentException("Unknown item type");
        }
    }



}
