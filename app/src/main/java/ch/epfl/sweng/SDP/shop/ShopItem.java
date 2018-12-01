package ch.epfl.sweng.SDP.shop;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.ColorUtils;

/**
 * Item that can be bought in the shop for the moment only colors can be bought.
 */
public class ShopItem {

    private static final int DEFAULT_PADDING = 30;

    private int price;
    private ColorsShop color;
    private boolean owned;

    /**
     * Constructor of a ShopItem.
     *
     * @param color Color of the item
     * @param price Price of the item
     */
    public ShopItem(ColorsShop color, int price) {
        this.price = price;
        this.color = color;
        this.owned = false;
    }

    /**
     * Constructor of a ShopItem.
     *
     * @param color Color of the item
     * @param price Price of the item
     * @param owned Is this item owned by the player
     */
    public ShopItem(ColorsShop color, int price, boolean owned) {
        this.price = price;
        this.color = color;
        this.owned = owned;
    }

    public ColorsShop getColorItem() {
        return color;
    }

    public int getPriceItem() {
        return price;
    }

    public void setPriceItem(int price) {
        this.price = price;
    }

    public void setColorItem(ColorsShop color) {
        this.color = color;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public boolean getOwned() {
        return this.owned;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ShopItem) {
            ShopItem item = (ShopItem) obj;

            return item.getPriceItem() == this.getPriceItem()
                    && (item.getColorItem()).equals(this.getColorItem());
        }
        return false;
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    public LinearLayout toLayout(final ShopActivity context) {
        Resources res = context.getResources();
        String colorName = color.toString();
        Typeface typeMuro = Typeface.createFromAsset(context.getAssets(), "fonts/Muro.otf");

        TextView colorTextView = context.createTextView(colorName,
                res.getColor(R.color.colorDrawYellow), DEFAULT_PADDING, typeMuro,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));

        ImageView colorImageView = new ImageView(context);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);

        colorImageView.setLayoutParams(params);
        colorImageView.setPadding(0, 0, DEFAULT_PADDING, 0);
        colorImageView.setImageDrawable(res.getDrawable(R.drawable.color_circle));
        colorImageView.setColorFilter(res.getColor(ColorUtils.getColorFromString(colorName)),
                PorterDuff.Mode.SRC_ATOP);

        LinearLayout layout;

        if (!owned) {
            TextView priceView = context.createTextView(Integer.toString(price),
                    res.getColor(R.color.colorPrimaryDark),
                    DEFAULT_PADDING, typeMuro,
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

            priceView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            priceView.setPadding(0, 0, DEFAULT_PADDING, 0);

            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.star);
            image.setPadding(0, 0, 0, DEFAULT_PADDING);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(75, 75);
            image.setLayoutParams(layoutParams);

            layout = context.addViews(new LinearLayout(context), colorImageView,
                    colorTextView, priceView, image);
        } else {
            TextView ownedView = context.createTextView("✔", res.getColor(R.color.colorGreen),
                    DEFAULT_PADDING, typeMuro,
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            ownedView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            ownedView.setPadding(0, 0, DEFAULT_PADDING, 0);
            colorImageView.setImageDrawable(res.getDrawable(R.drawable.color_circle_selected));
            layout = context.addViews(new LinearLayout(context), colorImageView, colorTextView, ownedView);
        }


        layout.setBackgroundColor(res.getColor(R.color.colorLightGrey));
        layout.setPadding(DEFAULT_PADDING, 10, DEFAULT_PADDING, 10);

        return layout;
    }
}

