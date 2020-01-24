package ch.epfl.sweng.GyroDraw.game.drawing;

import static ch.epfl.sweng.GyroDraw.shop.ColorsShop.getColorIdFromString;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.shop.ShopItem;
import com.google.android.gms.common.util.ArrayUtils;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class representing the drawing page of the game.
 */
public abstract class DrawingActivity extends NoBackPressActivity {

    static final int MIN_WIDTH = 10;
    static final int CURR_WIDTH = 20;

    protected RelativeLayout paintViewHolder;
    protected PaintView paintView;
    protected Handler handler;
    protected SeekBar brushWidthBar;

    private ImageView[] colorButtons;

    private ImageView pencilButton;
    private ImageView bucketButton;

    private int px;

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public int getLayoutId() {
        return R.layout.activity_drawing_offline;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(getLayoutId());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pencilButton = findViewById(R.id.pencilButton);
        bucketButton = findViewById(R.id.bucketButton);

        LinearLayout layout = findViewById(R.id.colorLayout);

        List<ShopItem> myItems = Account.getInstance(this).getItemsBought();
        List<Integer> colors = new LinkedList<>();

        colorButtons = new ImageView[myItems.size() + 1];
        colorButtons[0] = findViewById(R.id.blackButton);

        px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics());

        for (int i = 0; i < myItems.size(); ++i) {
            ShopItem item = myItems.get(i);
            int color = getColorIdFromString(item.getColorItem().toString());
            colors.add(color);
            ImageView colorView = createColorImageView(color);
            // Adds the view to the layout
            layout.addView(colorView);

            colorButtons[i + 1] = colorView;
        }

        paintViewHolder = findViewById(R.id.paintViewHolder);
        paintView = findViewById(R.id.paintView);
        brushWidthBar = findViewById(R.id.brushWidthBar);

        paintView.setColors(colors);
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                paintView.invalidate();
            }
        };
        brushWidthBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brushWidthBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        brushWidthBar.setOnSeekBarChangeListener(brushWidthBarListener);
    }

    SeekBar.OnSeekBarChangeListener brushWidthBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            adjustDrawingAndCircleWidth((int) (Math.pow(CURR_WIDTH, progress / 50.)) + MIN_WIDTH);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Must be implemented, but does nothing here.
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Must be implemented, but does nothing here.
        }
    };

    private void adjustDrawingAndCircleWidth(int newVal) {
        paintView.setDrawWidth(newVal);
        paintView.updateCircleRadius();
    }

    /**
     * Creates an {@link ImageView} corresponding to a given color.
     *
     * @param color Index of the colors to be created
     * @return The ImageView of the color
     */
    public ImageView createColorImageView(int color) {
        ImageView image = new ImageView(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        params.setMargins(px / 2, 0, px / 2, px);
        image.setLayoutParams(params);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        image.setImageDrawable(getResources().getDrawable(R.drawable.color_circle));
        image.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorClickHandler(view);
            }
        });

        return image;
    }

    /**
     * Clears the entire Path in paintView.
     *
     * @param view paintView
     */
    public void clear(View view) {
        paintView.clear();
    }


    /**
     * Sets the clicked button to selected and set the corresponding color.
     *
     * @param view the clicked view
     */
    public void colorClickHandler(View view) {
        int index = ArrayUtils.toArrayList(colorButtons).indexOf(view);
        paintView.setColor(index);
        colorButtons[index].setImageResource(R.drawable.color_circle_selected);

        for (int i = 0; i < colorButtons.length; i++) {
            if (i != index) {
                colorButtons[i].setImageResource(R.drawable.color_circle);
            }
        }
    }

    /**
     * Sets the clicked button to selected and sets the corresponding color.
     *
     * @param view the clicked view
     */
    public void toolClickHandler(View view) {
        switch (view.getId()) {
            case R.id.pencilButton:
                paintView.setPencil();
                setResources(R.drawable.pencil_selected, R.drawable.bucket);
                break;
            case R.id.bucketButton:
                paintView.setBucket();
                setResources(R.drawable.pencil, R.drawable.bucket_selected);
                break;
            case R.id.undoButton:
                paintView.undo();
                break;
            case R.id.redoButton:
                paintView.redo();
                break;
            default:
        }
    }

    private void setResources(int pencil, int bucket) {
        pencilButton.setImageResource(pencil);
        bucketButton.setImageResource(bucket);
    }
}
