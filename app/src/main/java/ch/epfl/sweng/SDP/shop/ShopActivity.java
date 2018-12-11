package ch.epfl.sweng.SDP.shop;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.utils.LayoutUtils;
import ch.epfl.sweng.SDP.utils.OnSwipeTouchListener;

import static ch.epfl.sweng.SDP.utils.LayoutUtils.bounceButton;

/**
 * Activity allowing the purchase of items such as colors.
 */
public class ShopActivity extends BaseActivity {

    private static boolean enableAnimations = true;

    private Dialog buyDialog;
    private LinearLayout shopItems;
    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        setContentView(R.layout.activity_shop);

        ImageView backgroundAnimation = findViewById(R.id.shopBackgroundAnimation);
        if (enableAnimations) {
            Glide.with(this).load(R.drawable.background_animation).into(backgroundAnimation);
        }

        buyDialog = new Dialog(this);
        shopItems = findViewById(R.id.shopItems);
        TextView exitButton = findViewById(R.id.exitButton);
        TextView starsText = findViewById(R.id.yourStars);

        setTypeFace(typeMuro, exitButton, findViewById(R.id.shopMessages), starsText);
        starsText.setText(String.format(Locale.getDefault(), "%d",
                Account.getInstance(this).getStars()));

        fillShop();
        addColorsToShop();
        LayoutUtils.setSlideRightExitListener(exitButton, this);

        backgroundAnimation.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                launchActivity(HomeActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    /**
     * Fills the shop with the items available taking into account the colors the player has already
     * bought.
     */
    public void fillShop() {
        shop = new Shop();
        List<ShopItem> myItems = Account.getInstance(this).getItemsBought();
        List<String> myColors = new ArrayList<>();

        for (ShopItem item : myItems) {
            myColors.add(item.getColorItem().toString());
        }

        for (ColorsShop color : ColorsShop.values()) {
            ShopItem item = new ShopItem(color, color.getPrice());

            if (myColors.contains(item.getColorItem().toString())) {
                item = new ShopItem(color, color.getPrice(), true);
            }

            shop.addItem(item);
        }
    }

    /**
     * Creates different layout for each available color in the shop.
     */
    public void addColorsToShop() {
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 40, 0, 0);

        List<ShopItem> itemsList = shop.getItemList();
        int stars = Account.getInstance(this).getStars();

        // Create a layout for each color and set an onTouchListener if they can be purchased
        for (int i = 0; i < itemsList.size(); ++i) {
            final ShopItem item = itemsList.get(i);
            final int index = i;

            item.setLayout(stars, this);
            LinearLayout itemLayout = item.getLayout();
            shopItems.addView(itemLayout, layoutParams);

            if (!item.getOwned() && item.getPriceItem() <= stars) {
                itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bounceButton(view, ShopActivity.this);
                        touchItem(index, item);
                    }
                });
            }

            itemLayout.setTag(item.getColorItem());
        }

    }

    @SuppressLint("DefaultLocale")
    private void touchItem(int index, ShopItem item) {
        buyDialog.setContentView(R.layout.shop_pop_up_buy);

        final TextView priceText = buyDialog.findViewById(R.id.priceText);
        priceText.setTypeface(typeMuro);
        priceText.setText(String.format(Locale.getDefault(), "%d", item.getPriceItem()));

        TextView colorText = buyDialog.findViewById(R.id.colorText);
        colorText.setTypeface(typeMuro);
        colorText.setText(item.getColorItem().toString());

        Button confirmButton = buyDialog.findViewById(R.id.confirmButton);
        confirmButton.setTypeface(typeMuro);
        setConfirmClick(confirmButton, index);

        Button cancelButton = buyDialog.findViewById(R.id.cancelButton);
        cancelButton.setTypeface(typeMuro);
        setCancelClick(cancelButton);

        buyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        buyDialog.show();
    }

    private void setConfirmClick(final Button button, final int index) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account account = Account.getInstance(ShopActivity.this);
                ShopItem item = shop.getItemList().get(index);
                int stars = account.getStars();
                int price = item.getPriceItem();

                // Check if the user has enough stars
                if (stars >= price) {
                    account.changeStars(-price);
                    account.updateItemsBought(item);
                    item.setOwned(true);

                    ((TextView) findViewById(R.id.yourStars))
                            .setText(String.format(Locale.getDefault(), "%d", account.getStars()));

                    // This clears layout and updates the item bought with owned
                    ((LinearLayout) findViewById(R.id.shopItems)).removeAllViews();
                    addColorsToShop();
                }

                buyDialog.dismiss();
            }
        });
    }

    private void setCancelClick(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyDialog.dismiss();
            }
        });
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
