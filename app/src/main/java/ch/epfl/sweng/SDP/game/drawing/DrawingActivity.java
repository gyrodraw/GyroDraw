package ch.epfl.sweng.SDP.game.drawing;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.android.gms.common.util.ArrayUtils;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.shop.ShopItem;
import ch.epfl.sweng.SDP.utils.ColorUtils;

public class DrawingActivity extends BaseActivity {
    protected static final String TAG = "DrawingActivity";
    protected PaintView paintView;
    protected Handler handler;

    private ImageView[] colorButtons;

    private ImageView pencilButton;
    private ImageView eraserButton;
    private ImageView bucketButton;

    protected int getLayoutId() {
        return R.layout.activity_drawing_offline;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.fui_slide_in_right,
                R.anim.fui_slide_out_left);
        setContentView(getLayoutId());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pencilButton = findViewById(R.id.pencilButton);
        eraserButton = findViewById(R.id.eraserButton);
        bucketButton = findViewById(R.id.bucketButton);

        LinearLayout layout = findViewById(R.id.colorLayout);

        List<ShopItem> myItems = Account.getInstance(this).getItemsBought();
        List<Integer> colors = new LinkedList<>();

        colorButtons = new ImageView[myItems.size() + 1];
        colorButtons[0] = findViewById(R.id.blackButton);

        for(int i = 0; i < myItems.size(); ++i) {
            ShopItem item = myItems.get(i);
            int color = ColorUtils.getColorFromString(item.getColorItem().toString());
            colors.add(color);
            ImageView colorView = createColorImageView(color);
            // Adds the view to the layout
            layout.addView(colorView);

            colorButtons[i+1] = colorView;
        }

        paintView = findViewById(R.id.paintView);
        paintView.setColors(colors);

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                paintView.invalidate();
            }
        };
    }

    /**
     * Create an imageview corresponding to a given color.
     * @param color Index of the colors to be created
     * @return The imageview of the color
     */
    public ImageView createColorImageView(int color) {
        ImageView image = new ImageView(this);

        TableLayout.LayoutParams params = new TableLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams
                                        .MATCH_PARENT, 1f);

        // Convert dp into px
        int px = (int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics()
        );

        params.setMargins(0, px, 0, px);
        image.setLayoutParams(params);

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
     * Sets the clicked button to selected and sets the corresponding color.
     *
     * @param view the clicked view
     */
    public void colorClickHandler(View view) {
        int index = ArrayUtils.indexOf(colorButtons, view);
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
                pencilButton.setImageResource(R.drawable.pencil_selected);
                eraserButton.setImageResource(R.drawable.eraser);
                bucketButton.setImageResource(R.drawable.bucket);
                break;
            case R.id.eraserButton:
                paintView.setEraser();
                pencilButton.setImageResource(R.drawable.pencil);
                eraserButton.setImageResource(R.drawable.eraser_selected);
                bucketButton.setImageResource(R.drawable.bucket);
                break;
            case R.id.bucketButton:
                paintView.setBucket();
                pencilButton.setImageResource(R.drawable.pencil);
                eraserButton.setImageResource(R.drawable.eraser);
                bucketButton.setImageResource(R.drawable.bucket_selected);
                break;
            default:
        }
    }

}
