package ch.epfl.sweng.SDP.shop;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;

/**
 * Activity allowing the purchase of items such as colors.
 */
public class ShopActivity extends Activity {

    private static boolean enableAnimations = true;

    private Dialog buyDialog;
    private Dialog confirmationDialog;

    private LinearLayout shopItems;

    private Typeface typeMuro;
    private Typeface typeOptimus;

    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_shop);

        if (enableAnimations) {
            Glide.with(this).load(R.drawable.background_animation)
                    .into((ImageView) findViewById(R.id.shopBackgroundAnimation));
        }

        buyDialog = new Dialog(this);
        confirmationDialog = new Dialog(this);

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        shopItems = findViewById(R.id.shopItems);

        ((TextView) findViewById(R.id.shopMessages)).setTypeface(typeOptimus);
        ((TextView) findViewById(R.id.yourStars)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.yourStars)).setText(String.format(Locale.getDefault(),
                "%d", Account.getInstance(this).getStars()));

        fillShop();
        addColorsToShop();
    }

    /**
     * Create different layout for each available color in the shop.
     */
    public void addColorsToShop() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 40, 0, 0);

        List<ShopItem> itemsList = shop.getItemList();

        for (int i = 0; i < itemsList.size(); ++i) {
            shopItems.addView(toLayout(itemsList.get(i), i), layoutParams);
        }
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    private LinearLayout toLayout(ShopItem item, final int index) {
        LinearLayout layout;

        ColorsShop color = item.getColorItem();
        String price = Integer.toString(item.getPriceItem());

        Resources res = getResources();

        TextView colorView = createTextView(color.toString(), res.getColor(R.color.colorDrawYellow), 30,
                typeMuro, new LinearLayout
                    .LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));


        if(!item.getOwned()) {
            TextView priceView = createTextView(price, res.getColor(R.color.colorPrimaryDark),
                    30, typeMuro, new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 2));

            priceView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            priceView.setPadding(0, 0, 20, 0);

            ImageView image = new ImageView(this);
            image.setBackgroundResource(R.drawable.star);
            image.setPadding(0, 0, 30, 0);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
            image.setLayoutParams(layoutParams);

            layout = addViews(new LinearLayout(this),
                    colorView, priceView, image);
        } else {
            TextView ownedView = createTextView("OWNED", res.getColor(R.color.colorGreen),
                    30, typeMuro, new LinearLayout
                            .LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 2));
            ownedView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
            ownedView.setPadding(0, 0, 30, 0);

            layout = addViews(new LinearLayout(this),
                    colorView, ownedView);
        }


        layout.setBackgroundColor(res.getColor(R.color.colorLightGrey));
        layout.setPadding(30, 10, 30, 10);

        if(!item.getOwned()) {
            layout.setClickable(true);

            layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    touchItem(index);
                    return true;
                }
            });
        }

        layout.setTag(item.getColorItem());

        return layout;
    }

    @SuppressLint("DefaultLocale")
    private void touchItem(int index) {
        buyDialog.setContentView(R.layout.shop_pop_up_buy);

        List<ShopItem> list = shop.getItemList();

        ((TextView) buyDialog.findViewById(R.id.infoMessageView)).setText(String.format(
                "Do you really want to buy %s color for %d stars", list.get(index).getColorItem(),
                list.get(index).getPriceItem()));

        setOnBuyClick(((Button) buyDialog.findViewById(R.id.buyButton)), index);

        buyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buyDialog.show();
    }

    private void setOnBuyClick(final Button button, final int index) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSuccessful = false;
                // Check if the user has enough stars
                if(Account.getInstance(getApplicationContext()).getStars()
                        - (shop.getItemList()).get(index).getPriceItem() >= 0) {
                    Account.getInstance(getApplicationContext()).changeStars(
                            -(shop.getItemList()).get(index).getPriceItem());

                    Account.getInstance(getApplicationContext())
                            .updateItemsBought((shop.getItemList()).get(index));

                    ((shop.getItemList()).get(index)).setOwned(true);
                    isSuccessful = true;
                }

                buyDialog.dismiss();
                showConfirmationPopUp(isSuccessful);

            }
        });
    }

    /**
     * Displays a Pop up window displaying a success message or error message.
     * @param isSuccessful Boolean that tells if the purchase went successful or not
     */
    public void showConfirmationPopUp(boolean isSuccessful) {
        confirmationDialog.setContentView(R.layout.shop_pop_up_confirmation);

        if(isSuccessful) {
            ((TextView) confirmationDialog.findViewById(R.id.confirmationText))
                    .setText(getString(R.string.success));
            ((TextView) confirmationDialog.findViewById(R.id.confirmationText))
                    .setTextColor(getResources().getColor(R.color.colorGreen));
            ((TextView) confirmationDialog.findViewById(R.id.infoMessageView))
                    .setText(getString(R.string.buySuccess));
            ((TextView) findViewById(R.id.yourStars)).setText(String.format(Locale.getDefault(),
                                        "%d", Account.getInstance(this).getStars()));

            // This clears layout and updates the item bought with owned
            ((LinearLayout) findViewById(R.id.shopItems)).removeAllViews();
            addColorsToShop();

        } else {
            ((TextView) confirmationDialog.findViewById(R.id.confirmationText))
                    .setText(getString(R.string.error));
            ((TextView) confirmationDialog.findViewById(R.id.confirmationText))
                    .setTextColor(getResources().getColor(R.color.colorRed));
            ((TextView) confirmationDialog.findViewById(R.id.infoMessageView))
                    .setText(getString(R.string.buyError));
        }

        (confirmationDialog.findViewById(R.id.okButton))
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationDialog.dismiss();
            }
        });

        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmationDialog.show();

    }

    public void onCancelPopUp(View view) {
        buyDialog.dismiss();
    }

    /**
     * Fill the shop with the items available taking into account the colors the player
     * has already bought.
     */
    public void fillShop() {

        shop = new Shop();
        List<ShopItem> myItems = Account.getInstance(this).getItemsBought();

        for (ColorsShop color : ColorsShop.values()) {
            boolean owned = false;

            if (myItems.contains(new ShopItem(color, color.getPrice()))) {
                owned = true;
            }

            shop.addItem(new ShopItem(color, color.getPrice(), owned));
        }
    }

    @VisibleForTesting
    public Shop getShop() {
        return shop;
    }

    @VisibleForTesting
    public static void disableAnimations() {
        enableAnimations = false;
    }
}