package ch.epfl.sweng.SDP.game.drawing;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.RelativeLayout;

import java.util.LinkedList;

import ch.epfl.sweng.SDP.R;

public class DrawingOnlineItems extends DrawingOnline {

    private static final int INTERVAL = 10000;

    protected RelativeLayout paintViewHolder;
    protected PaintView paintView;
    protected RandomItemGenerator randomItemGenerator;
    private LinkedList<Item> displayedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paintViewHolder = findViewById(R.id.paintViewHolder);
        paintView = super.paintView;
        randomItemGenerator = new RandomItemGenerator(paintView);
        displayedItems = new LinkedList<>();
        generateItems();
    }

    @Override
    public void updateValues(float coordinateX, float coordinateY) {
        Item activatedItem = null;
        for(Item item : displayedItems) {
            if(item.collision(paintView.circleX, paintView.circleY, paintView.getCircleRadius())) {
                activatedItem = item;
            }
        }

        if(activatedItem != null) {
            activatedItem.activate(paintView);
            paintViewHolder.removeView(activatedItem.getView());
            displayedItems.remove(activatedItem);
        }
        paintView.updateCoordinates(coordinateX, coordinateY);
    }

    private void generateItems() {
        new CountDownTimer(INTERVAL, 1000) {

            public void onTick(long millisUntilFinished) {
                // nüt
            }

            public void onFinish() {
                Item newItem = randomItemGenerator.generateItem();
                paintViewHolder.addView(newItem.getView());
                displayedItems.add(newItem);
                generateItems();
            }
        }.start();
    }

}
