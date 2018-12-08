package ch.epfl.sweng.SDP.shop;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.annotation.VisibleForTesting;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Comparator;
import java.util.Objects;

import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.utils.TypefaceLibrary;

import static ch.epfl.sweng.SDP.shop.ColorsShop.getColorIdFromString;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Class representing an item that can be bought in the shop. For the moment, only colors can be
 * bought.
 */
public class ShopItem {

    private static final int DEFAULT_PADDING = 30;
    private static final ShopItemComparator comparator = new ShopItemComparator();

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
        checkPrecondition(price >= 0, "price is negative");
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
        checkPrecondition(price >= 0, "price is negative");
        this.price = price;
        this.color = color;
        this.owned = owned;
    }

    public static ShopItemComparator getComparator() {
        return comparator;
    }

    public ColorsShop getColorItem() {
        return color;
    }

    @VisibleForTesting
    void setColorItem(ColorsShop color) {
        this.color = color;
    }

    public int getPriceItem() {
        return price;
    }

    @VisibleForTesting
    void setPriceItem(int price) {
        this.price = price;
    }

    public boolean getOwned() {
        return owned;
    }

    void setOwned(boolean owned) {
        this.owned = owned;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ShopItem shopItem = (ShopItem) obj;
        return getPriceItem() == shopItem.getPriceItem()
                && getColorItem() == shopItem.getColorItem();
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, color);
    }

    /**
     * Create the layout of this item.
     *
     * @param stars   the current amount of stars
     * @param context the context of the shop
     */
    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    void setLayout(int stars, ShopActivity context) {
        Resources res = context.getResources();
        String colorName = color.toString();
        Typeface typeMuro = TypefaceLibrary.getTypeMuro();

        // Create the color image
        ImageView colorImageView = new ImageView(context);
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);

        colorImageView.setLayoutParams(params);
        colorImageView.setPadding(0, 0, DEFAULT_PADDING, 0);
        colorImageView.setImageDrawable(res.getDrawable(R.drawable.color_circle));
        colorImageView.setColorFilter(res.getColor(getColorIdFromString(colorName)),
                PorterDuff.Mode.SRC_ATOP);

        if (!owned) {
            // Create the star image
            ImageView starView = new ImageView(context);
            LayoutParams layoutParams =
                    new LayoutParams(0, ActionBar.LayoutParams.MATCH_PARENT, 0.5f);
            starView.setLayoutParams(layoutParams);
            starView.setImageResource(R.drawable.star);

            // Create the price text
            TextView priceView = context.createTextView(Integer.toString(price),
                    price <= stars ? res.getColor(R.color.colorGreenStar) :
                            res.getColor(R.color.colorExitRed),
                    DEFAULT_PADDING, typeMuro, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2));
            priceView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            priceView.setPadding(0, 0, 10, 0);

            // Create the color text
            TextView colorTextView = context.createTextView(colorName,
                    res.getColor(R.color.colorDrawYellow), DEFAULT_PADDING, typeMuro,
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 4));

            // Create the global layout
            layout = context.addViews(new LinearLayout(context), colorImageView,
                    colorTextView, priceView, starView);
            layout.setBackgroundColor(res.getColor(R.color.colorLightGrey));
        } else {
            // Create the color text
            TextView colorTextView = context.createTextView(colorName,
                    res.getColor(R.color.colorDrawYellow), DEFAULT_PADDING, typeMuro,
                    new LayoutParams(0, LayoutParams.WRAP_CONTENT, 6.5f));

            colorImageView.setImageDrawable(res.getDrawable(R.drawable.color_circle_selected));

            // Create the global layout
            layout = context.addViews(new LinearLayout(context), colorImageView, colorTextView);
            layout.setBackgroundColor(res.getColor(R.color.colorGrey));
        }

        layout.setPadding(DEFAULT_PADDING, 10, DEFAULT_PADDING, 10);
        layout.setClickable(price <= stars && !owned);
    }

    private static class ShopItemComparator implements Comparator<ShopItem> {

        @Override
        public int compare(ShopItem item1, ShopItem item2) {
            return item1.getColorItem().compareTo(item2.getColorItem());
        }
    }
}
