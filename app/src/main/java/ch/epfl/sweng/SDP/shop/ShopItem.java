package ch.epfl.sweng.SDP.shop;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
    private LinearLayout layout;

    /**
     * Constructor of a ShopItem.
     *
     * @param color the color of the item
     * @param price the price of the item
     */
    public ShopItem(ColorsShop color, int price) {
        this.price = price;
        this.color = color;
        owned = false;
    }


    /**
     * Constructor of a ShopItem.
     *
     * @param color the color of the item
     * @param price the price of the item
     * @param owned is this item owned by the player
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

    public boolean getOwned() {
        return owned;
    }

    public LinearLayout getLayout() {
        return layout;
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

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ShopItem) {
            ShopItem item = (ShopItem) obj;

            return item.getPriceItem() == this.getPriceItem()
                    && (item.getColorItem()).equals(this.getColorItem());
        }
        return false;
    }

    /**
     * @param stars   the current amount of stars
     * @param context the context of the shop
     */
    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    public void setLayout(int stars, ShopActivity context) {
        Resources res = context.getResources();
        String colorName = color.toString();
        Typeface typeMuro = Typeface.createFromAsset(context.getAssets(), "fonts/Muro.otf");

        ImageView colorImageView = new ImageView(context);
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);

        colorImageView.setLayoutParams(params);
        colorImageView.setPadding(0, 0, DEFAULT_PADDING, 0);
        colorImageView.setImageDrawable(res.getDrawable(R.drawable.color_circle));
        colorImageView.setColorFilter(res.getColor(ColorUtils.getColorFromString(colorName)),
                PorterDuff.Mode.SRC_ATOP);

        if (!owned) {
            TextView colorTextView = context.createTextView(colorName,
                    res.getColor(R.color.colorDrawYellow), DEFAULT_PADDING, typeMuro,
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4));

            ImageView starView = new ImageView(context);
            LayoutParams layoutParams = new LayoutParams(0, ActionBar.LayoutParams.MATCH_PARENT, 0.5f);
            starView.setLayoutParams(layoutParams);
            starView.setImageResource(R.drawable.star);

            TextView priceView = context.createTextView(Integer.toString(price),
                    price <= stars ? res.getColor(R.color.colorGreenStar) :
                            res.getColor(R.color.colorExitRed),
                    DEFAULT_PADDING, typeMuro, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));

            priceView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            priceView.setPadding(0, 0, 10, 0);
            layout = context.addViews(new LinearLayout(context), colorImageView,
                    colorTextView, priceView, starView);
            layout.setBackgroundColor(res.getColor(R.color.colorLightGrey));
        } else {
            TextView colorTextView = context.createTextView(colorName,
                    res.getColor(R.color.colorDrawYellow), DEFAULT_PADDING, typeMuro,
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 6.5f));

            colorImageView.setImageDrawable(res.getDrawable(R.drawable.color_circle_selected));
            layout = context.addViews(new LinearLayout(context), colorImageView, colorTextView);
            layout.setBackgroundColor(res.getColor(R.color.colorGrey));
        }

        layout.setPadding(DEFAULT_PADDING, 10, DEFAULT_PADDING, 10);
        layout.setClickable(price <= stars && !owned);
    }
}

