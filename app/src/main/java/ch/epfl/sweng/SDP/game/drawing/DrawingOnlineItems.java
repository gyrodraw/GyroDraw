package ch.epfl.sweng.SDP.game.drawing;

import android.os.Bundle;
import android.widget.RelativeLayout;

import ch.epfl.sweng.SDP.R;

public class DrawingOnlineItems extends DrawingOnline {

    protected RelativeLayout paintViewHolder = findViewById(R.id.paintViewHolder);
    protected PaintView paintView = super.paintView;
    protected RandomItemGenerator randomItemGenerator = new RandomItemGenerator(paintViewHolder, paintView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
