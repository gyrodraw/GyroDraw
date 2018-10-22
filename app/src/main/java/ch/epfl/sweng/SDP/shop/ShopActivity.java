package ch.epfl.sweng.SDP.shop;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;

/**
 * Helper class facilitating getting and synchronizing data from the database.
 */
class IntegerWrapper {
    private int i;
    IntegerWrapper(int i) {
        this.i = i;
    }
    protected void setInt(int newi) {
        i = newi;
    }
    protected int getInt() {
        return i;
    }
}

/**
 * Activity allowing the purchase of itmes such as colors.
 */
public class ShopActivity extends Activity {
    //to be replaced with whatever we use to store all these refs
    final FirebaseDatabase db = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
    final DatabaseReference dbRef = db.getReference();
    final DatabaseReference usersRef = dbRef.child("users");
    final DatabaseReference shopColorsRef = dbRef.child("items").child("colors");
    final DatabaseReference userColorsRef = usersRef.child(FirebaseAuth.getInstance()
            .getCurrentUser().getUid()).child("items").child("colors");

    private final IntegerWrapper stars = new IntegerWrapper(-1);
    private final IntegerWrapper price = new IntegerWrapper(-1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);
        getColorsFromDatabase();
        setReturn();
        setRefresh();
    }

    /**
     * Accesses the database, gets all the available colors and creates a button in the ScrollView
     * for each item found.
     */
    private void getColorsFromDatabase() {
        shopColorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    LinearLayout linearLayout = findViewById(R.id.linearLayout);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Button b = initializeButton(snapshot.getKey());
                        addOnClickListenerToButton(b);
                        linearLayout.addView(b);
                    }
                }
                else {
                    setShopMessage("Currently no purchasable items in shop.");
                    resetShopMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Initializes a button with the text it shows.
     * @param itemName String to be displayed on the button.
     * @return Button displaying itemName.
     */
    private Button initializeButton(String itemName) {
        Button b = new Button(this);
        b.setText(itemName);
        return b;
    }

    /**
     * Adds an onClickListener to a button, which on Click tries to purchase the item the button
     * corresponds to.
     * @param b Button to which a listener is added.
     */
    private void addOnClickListenerToButton(final Button b) {
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                purchaseItem(b.getText().toString());
            }
        };
        b.setOnClickListener(onClickListener);
    }

    /**
     * Sets the return button that makes the user return to the HomeActivity.
     */
    private void setReturn() {
        Button ret = findViewById(R.id.returnFromShop);
        ret.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gotoHome();
            }
        });
    }

    /**
     * Sets the button that allows the user to refresh the ShopActivity
     */
    private void setRefresh() {
        Button refresh = findViewById(R.id.refreshShop);
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
    private void purchaseItem(String itemName) {
        alreadyOwned(itemName);
    }

    /**
     * Checks if an item is already owned by the user. If not, gets the users current Stars and
     * the items price, verifies if the user has enough Stars and if so, updates the users
     * inventory.
     * @param itemName Item to be purchased.
     * @throws DatabaseException
     */
    private void alreadyOwned(final String itemName) throws DatabaseException {
        userColorsRef.orderByKey().equalTo(itemName).addListenerForSingleValueEvent(
                new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    setShopMessage("Item already owned.");
                    resetShopMessage();
                }
                else {
                    stars.setInt(-1);
                    price.setInt(-1);
                    getStars(stars);
                    getPrice(price, itemName);
                    new CountDownTimer(2500, 500) {
                        public void onTick(long millisUntilFinished) {
                            if(stars.getInt() > -1 && price.getInt() > -1) {
                                this.cancel();
                                updateUserIf(itemName);
                            }
                        }
                        public void onFinish() {
                            if(stars.getInt() < 0 || price.getInt() < 0) {
                                setShopMessage("Unable to read from database in time.");
                                resetShopMessage();
                            }
                            else {
                                updateUserIf(itemName);
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
     */
    private void updateUserIf(String itemName) {
        if(sufficientCurrency(stars.getInt(), price.getInt())) {
            updateUser(itemName, stars.getInt() - price.getInt());
        }
    }

    /**
     * Accesses the database, and puts the users current stars into the wrapper.
     * @param starsWrapper Wrapper to retrieve stars from database.
     * @throws DatabaseException
     */
    private void getStars(final IntegerWrapper starsWrapper) throws DatabaseException {
        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("stars")
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    starsWrapper.setInt((int) Math.max(Math.min((long) dataSnapshot.getValue(),
                            Integer.MAX_VALUE), Integer.MIN_VALUE));
                }
                else {
                    starsWrapper.setInt(-1);
                }
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
     * @throws IllegalArgumentException
     * @throws DatabaseException
     */
    private void getPrice(final IntegerWrapper priceWrapper, final String itemName)
            throws IllegalArgumentException, DatabaseException {
        shopColorsRef.child(itemName).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    priceWrapper.setInt((int) Math.max(Math.min((long) dataSnapshot.getValue(),
                            Integer.MAX_VALUE), Integer.MIN_VALUE));
                }
                else {
                    priceWrapper.setInt(-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    /**
     * Checks if the user has enough currency to buy an item.
     * @param stars Current stars of user.
     * @param price Price of the item to purchase.
     * @return true iff stars >= price.
     */
    private boolean sufficientCurrency(int stars, int price) {
        boolean sufficient = stars > price;
        if (!sufficient) {
            setShopMessage("Not enough stars to purchase item.");
            resetShopMessage();
        }
        return sufficient;
    }

    /**
     * Updates the users data in the database, e.g. sets their stars to newStars and adds the item
     * to their inventory.
     * @param itemName Name of the item added.
     * @param newStars New amount of stars after purchase.
     */
    private void updateUser(String itemName, int newStars) {
        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("stars")
                .setValue(newStars, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
            }
        });
        userColorsRef.child(itemName).setValue(true, new DatabaseReference.CompletionListener() {

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
     * Sets the shop message.
     * @param message Message to be displayed.
     */
    private void setShopMessage(String message) {
        TextView t = findViewById(R.id.shopMessages);
        t.setText(message);
    }

    /**
     * Sets the shop message to the empty string after 5 seconds.
     */
    private void resetShopMessage() {
        new CountDownTimer(5000, 5000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                setShopMessage("");
            }
        }.start();
    }

    /**
     * Closes ShopActivity and returns to the HomeActivity.
     */
    private void gotoHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Restarts the ShopActivity.
     */
    private void refreshShop() {
        startActivity(getIntent());
        finish();
    }
}