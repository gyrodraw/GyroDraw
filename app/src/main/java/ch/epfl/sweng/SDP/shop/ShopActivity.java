package ch.epfl.sweng.SDP.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.home.LeaderboardActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity allowing the purchase of items such as colors.
 */
public class ShopActivity extends Activity {
    //to be replaced with whatever we use to store all these refs
    protected FirebaseDatabase database;
    protected DatabaseReference currentUser;
    protected DatabaseReference shopColorsRef;

    private final String colors = "colors";
    private final String items = "items";

    private final int delayToClear = 5000;

    private TextView shopTextView;
    private Button retFromShop;
    private Button refresh;
    private LinearLayout shopItems;
    private Map<String, Integer> itemsList;

    private Typeface typeMuro;
    private Typeface typeOptimus;

    private Shop shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shopColorsRef = Database.getReference("shop.colors");

        typeMuro = Typeface.createFromAsset(getAssets(), "fonts/Muro.otf");
        typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        setContentView(R.layout.shop_activity);
        shopTextView = findViewById(R.id.shopMessages);

        getColorsFromDatabase(shopColorsRef, shopTextView);

        retFromShop = findViewById(R.id.returnFromShop);
        setReturn(retFromShop);
        refresh = findViewById(R.id.refreshShop);
        setRefresh(refresh);

        shopItems = findViewById(R.id.ShopItems);

        ((TextView) findViewById(R.id.shopMessages)).setTypeface(typeOptimus);
        ((TextView) findViewById(R.id.yourStars)).setTypeface(typeMuro);
        ((TextView) findViewById(R.id.yourStars)).setText(""+Account.getInstance(this).getStars());
    }

    /**
     * Accesses the database, gets all the available colors and creates a button in the ScrollView
     * for each item found.
     * @param shopColorsReference  Reference to where colors are stored in shop.
     * @param textView TextView to display user relevant messages.
     */
    protected void getColorsFromDatabase(DatabaseReference shopColorsReference,
                                         final TextView textView) {
        shopColorsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                extractColorsFromDataSnapshot(dataSnapshot, textView);
                addColorsToShop();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Tries to extract colors from a snapshot and creates a Button for each.
     * @param dataSnapshot DataSnapshot from which colors should be extracted.
     * @param textView TextView for user relevant messages.
     */
    protected void extractColorsFromDataSnapshot(DataSnapshot dataSnapshot, TextView textView) {
        if (dataSnapshot == null || textView == null) {
            throw new NullPointerException();
        }

        if(dataSnapshot.exists()) {
            itemsList = new LinkedHashMap<>();

            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                itemsList.put(ds.getKey(), ds.getValue(Integer.class));
            }

            shop = new Shop(itemsList);

        } else {
            setTextViewMessage(textView,"Currently no purchasable items in shop.");
            resetTextViewMessage(textView, delayToClear);
        }
    }

    public void addColorsToShop() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 40, 0, 0);

        for (int i = 0; i < itemsList.size(); ++i) {
            shopItems.addView(toLayout(i), layoutParams);
        }
    }

    @SuppressLint("NewApi")
    private LinearLayout toLayout(int index) {
        Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)itemsList.entrySet().toArray()[index];
        String color = entry.getKey();
        String price = entry.getValue().toString();

        TextView colorView = new TextView(this);
        Resources res = getResources();
        styleView(colorView, color, res.getColor(R.color.colorDrawYellow),
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));

        TextView priceView = new TextView(this);
        styleView(priceView, price,
                res.getColor(R.color.colorPrimaryDark),
                new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        priceView.setTextAlignment(RelativeLayout.TEXT_ALIGNMENT_TEXT_END);
        priceView.setPadding(0, 0, 20, 0);

        ImageView image = new ImageView(this);
        image.setBackgroundResource(R.drawable.star_shop);
        image.setPadding(0, 0, 30, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        image.setLayoutParams(layoutParams);

        LinearLayout layout = addViews(new LinearLayout(this),
                colorView, priceView, image);

        layout.setBackgroundColor(res.getColor(R.color.colorLightGrey));
        layout.setPadding(30, 10, 30, 10);

        return layout;
    }

    private LinearLayout addViews(LinearLayout layout, View... views) {
        for(View view: views) {
            layout.addView(view);
        }

        return layout;
    }

    private void styleView(TextView view, String text, int color,
                           LinearLayout.LayoutParams layoutParams) {
        view.setText(text);
        view.setTextSize(30);
        view.setTextColor(color);
        view.setTypeface(typeMuro);
        view.setLayoutParams(layoutParams);
    }

    /**
     * Sets the return button that makes the user return to the HomeActivity.
     * @param ret Button which on clicked should return to the HomeActivity.
     */
    protected void setReturn(Button ret) {
        ret.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gotoHome();
            }
        });
    }

    /**
     * Sets the button that allows the user to refresh the ShopActivity.
     * @param refresh Button which on clicked should refresh the current activity.
     */
    protected void setRefresh(Button refresh) {
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                refreshShop();
            }
        });
    }

    /**
     * Tries to purchase a given item.
     * @param itemName Item to be purchased.
     */
    protected void purchaseItem(String itemName) {
        alreadyOwned(currentUser.child(items).child(colors), itemName, shopTextView);
    }

    /**
     * Checks if an item is already owned by the user. If not, gets the users current Stars and
     * the items price, verifies if the user has enough Stars and if so, updates the users
     * inventory.
     * @param itemName Item to be purchased.
     * @param userColorsReference Reference to where the colors of current user are stored.
     * @param textView TextView to display user relevant messages.
     * @throws DatabaseException If read does go wrong.
     */
    private void alreadyOwned(DatabaseReference userColorsReference, final String itemName,
                              final TextView textView)
            throws DatabaseException {
        userColorsReference.orderByKey().equalTo(itemName).addListenerForSingleValueEvent(
                new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    setTextViewMessage(textView,"Item already owned.");
                    resetTextViewMessage(textView, delayToClear);
                }
                else {
                    final IntegerWrapper stars = new IntegerWrapper(-1);
                    final IntegerWrapper price = new IntegerWrapper(-1);
                    getStars(currentUser.child("stars"), stars);
                    getPrice(shopColorsRef, itemName, price);
                    new CountDownTimer(5000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if(stars.getInt() > -1 && price.getInt() > -1) {
                                this.cancel();
                                updateUserIf(currentUser, itemName, textView,
                                        stars.getInt(), price.getInt());
                            }
                        }

                        public void onFinish() {
                            if(stars.getInt() < 0 || price.getInt() < 0) {
                                setTextViewMessage(textView,
                                        "Unable to read from database in time.");
                                resetTextViewMessage(textView, delayToClear);
                            }
                            else {
                                updateUserIf(currentUser, itemName, textView,
                                        stars.getInt(), price.getInt());
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }


    /**
     * Updates the users inventory with new Stars and item if he has enough Stars to buy it.
     * @param itemName Item to be purchased.
     * @param textView TextView to display user relevant messages.
     */
    protected void updateUserIf(DatabaseReference currentUserRef, String itemName,
                                TextView textView, int stars, int price) {
        if(sufficientCurrency(stars, price)) {
            updateUser(currentUserRef, itemName, stars - price,
                    shopTextView);
        }
        else {
            setTextViewMessage(textView, "Not enough stars to purchase item.");
            resetTextViewMessage(textView, delayToClear);
        }
    }

    /**
     * Accesses the database, and puts the users current stars into the wrapper.
     * @param starsWrapper Wrapper to retrieve stars from database.
     * @param userStarsReference Reference to where the stars of the current user are stored.
     * @throws DatabaseException If read does go wrong.
     */
    protected void getStars(DatabaseReference userStarsReference, final IntegerWrapper starsWrapper)
            throws DatabaseException {
        if(userStarsReference == null) {
            throw new NullPointerException();
        }
        userStarsReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wrapDataSnapshotValue(dataSnapshot, starsWrapper);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Accesses the database and puts the price of the current item into the wrapper.
     * @param priceWrapper Wrapper to retrieve price from database.
     * @param itemName Name if the item whose price we want to get.
     * @param shopColorsReference Reference to where colors are stored in shop.
     * @throws DatabaseException If read does go wrong.
     */
    protected void getPrice(DatabaseReference shopColorsReference, final String itemName,
                            final IntegerWrapper priceWrapper) throws DatabaseException {
        if(shopColorsReference == null || itemName == null) {
            throw new NullPointerException();
        }
        shopColorsReference.child(itemName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    wrapDataSnapshotValue(dataSnapshot, priceWrapper);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Extracts the long value of a DataSnapshot and wraps in into an IntegerWrapper, or sets the
     * value of the IntegerWraper to -1 if any problems occur.
     * @param wrapper Wrapper that is given value.
     * @param dataSnapshot Snapshot to extract value from.
     */
    public void wrapDataSnapshotValue(DataSnapshot dataSnapshot, final IntegerWrapper wrapper) {
        if(wrapper == null) {
            throw new NullPointerException();
        }
        if (dataSnapshot.exists()) {
            try {
                wrapper.setInt((int) Math.max(Math.min((long) dataSnapshot.getValue(),
                        Integer.MAX_VALUE), Integer.MIN_VALUE));
            }
            catch (Exception e) {
                wrapper.setInt(-1);
            }
        }
        else {
            wrapper.setInt(-1);
        }
    }

    /**
     * Checks if the user has enough currency to buy an item.
     * @param stars Current stars of user.
     * @param price Price of the item to purchase.
     * @return true iff stars >= price.
     */
    protected boolean sufficientCurrency(int stars, int price) {
        boolean sufficient = stars >= 0 && stars >= price;
        return sufficient;
    }

    /**
     * Updates the users data in the database, e.g. sets their stars to newStars and adds the item
     * to their inventory.
     * @param itemName Name of the item added.
     * @param newStars New amount of stars after purchase.
     * @param currentUserRef Reference to the current user in database.
     */
    protected void updateUser(DatabaseReference currentUserRef, String itemName, int newStars,
                            final TextView textView) {
        if (itemName == null || textView == null) {
            throw new NullPointerException();
        }
        updateUserStars(currentUserRef.child("stars"), newStars);
        addUserItem(currentUserRef.child(items).child(colors).child(itemName));
        setTextViewMessage(textView, "Purchase successful.");
        resetTextViewMessage(textView, delayToClear);
    }

    /**
     * Updates the users stars after a purchase.
     * @param newStars New amount of stars the user posesses.
     * @param currentUserStarsRef Reference to where the users stars are stored in the database.
     */
    protected void updateUserStars(DatabaseReference currentUserStarsRef, int newStars) {
        if (currentUserStarsRef == null) {
            throw new NullPointerException();
        }
        if (newStars < 0) {
            throw new IllegalArgumentException("newStars must be bigger/equal zero");
        }
        currentUserStarsRef.setValue(newStars, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError,
                                           @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            throw databaseError.toException();
                        }
                    }
                });
    }

    /**
     * Adds an item to a users database account after a purchase.
     * @param currentUserSpecificItem Reference to where the users items are stored in the
     *                                database.
     */
    protected void addUserItem(DatabaseReference currentUserSpecificItem) {
        if(currentUserSpecificItem == null) {
            throw new NullPointerException();
        }
        currentUserSpecificItem.setValue(true, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
            }
        });
    }


    /**
     * Sets the message of a TextView.
     * @param textView TextView that should show a certain message.
     * @param message String to be displayed.
     */
    protected void setTextViewMessage(TextView textView, String message) {
        if(textView == null) {
            throw new NullPointerException();
        }
        if(message == null) {
            setTextViewMessage(textView, "");
        }
        textView.setText(message);
    }

    /**
     * Clears the TextView after a certain delay.
     * @param textView TextView to be cleared.
     * @param delay Delay until message is cleared in milliseconds.
     */
    protected void resetTextViewMessage(final TextView textView, int delay) {
        if(textView == null) {
            throw new NullPointerException();
        }
        new CountDownTimer(delay, delay) {
            public void onTick(long millisUntilFinished) {
                /**
                 * Does nothing on tick, only once the countdown reaches zero action is needed.
                 */
            }

            public void onFinish() {
                setTextViewMessage(textView, "");
            }
        }.start();
    }

    /**
     * Closes ShopActivity and returns to the HomeActivity.
     */
    protected void gotoHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Restarts the ShopActivity.
     */
    protected void refreshShop() {
        startActivity(getIntent());
        finish();
    }
}