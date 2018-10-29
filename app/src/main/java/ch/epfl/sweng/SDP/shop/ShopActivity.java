package ch.epfl.sweng.SDP.shop;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.home.HomeActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity allowing the purchase of items such as colors.
 */
public class ShopActivity extends Activity {
    //to be replaced with whatever we use to store all these refs
    protected FirebaseDatabase database;
    protected DatabaseReference dbRef;
    protected DatabaseReference usersRef;
    protected DatabaseReference currentUser;
    protected DatabaseReference shopColorsRef;

    private final String colors = "colors";
    private final String items = "items";

    private final int delayToClear = 5000;

    private TextView shopTextView;
    private Button retFromShop;
    private Button refresh;

    private final IntegerWrapper stars = new IntegerWrapper(-1);
    private final IntegerWrapper price = new IntegerWrapper(-1);

    /**
     * Not sure if we keep this in the final version.
     */
    protected void initializeReferences() {
        database = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
        dbRef = database.getReference();
        usersRef = dbRef.child("users");
        currentUser = usersRef.child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        shopColorsRef = dbRef.child(items).child(colors);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeReferences();
        setContentView(R.layout.shop_activity);
        shopTextView = findViewById(R.id.shopMessages);
        getColorsFromDatabase(shopColorsRef, shopTextView);
        retFromShop = findViewById(R.id.returnFromShop);
        setReturn(retFromShop);
        refresh = findViewById(R.id.refreshShop);
        setRefresh(refresh);
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
                if(dataSnapshot.exists()) {
                    LinearLayout linearLayout = findViewById(R.id.linearLayout);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Button btn = initializeButton(snapshot.getKey());
                        addPurchaseOnClickListenerToButton(btn);
                        linearLayout.addView(btn);
                    }
                }
                else {
                    setTextViewMessage(textView,"Currently no purchasable items in shop.");
                    resetTextViewMessage(textView, delayToClear);
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
    protected Button initializeButton(String itemName) {
        Button btn = new Button(this);
        btn.setText(itemName);
        return btn;
    }

    /**
     * Adds an onClickListener to a button, which on Click tries to purchase the item the button
     * corresponds to.
     * @param btn Button to which a listener is added.
     */
    protected void addPurchaseOnClickListenerToButton(final Button btn) {
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                purchaseItem(btn.getText().toString());
            }
        };
        btn.setOnClickListener(onClickListener);
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
        alreadyOwned(itemName, currentUser.child(items).child(colors), shopTextView);
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
    private void alreadyOwned(final String itemName, DatabaseReference userColorsReference,
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
                    stars.setInt(-1);
                    price.setInt(-1);
                    getStars(stars, currentUser.child("stars"));
                    getPrice(price, itemName, shopColorsRef);
                    new CountDownTimer(5000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if(stars.getInt() > -1 && price.getInt() > -1) {
                                this.cancel();
                                updateUserIf(itemName, textView);
                            }
                        }

                        public void onFinish() {
                            if(stars.getInt() < 0 || price.getInt() < 0) {
                                setTextViewMessage(textView,
                                        "Unable to read from database in time.");
                                resetTextViewMessage(textView, delayToClear);
                            }
                            else {
                                updateUserIf(itemName, textView);
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
    private void updateUserIf(String itemName, TextView textView) {
        if(sufficientCurrency(stars.getInt(), price.getInt())) {
            updateUser(itemName, stars.getInt() - price.getInt(), currentUser,
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
    protected void getStars(final IntegerWrapper starsWrapper, DatabaseReference userStarsReference)
            throws DatabaseException {
        userStarsReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wrapDataSnapshotValue(starsWrapper, dataSnapshot);
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
    protected void getPrice(final IntegerWrapper priceWrapper, final String itemName,
                          DatabaseReference shopColorsReference) throws DatabaseException {
        shopColorsReference.child(itemName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wrapDataSnapshotValue(priceWrapper, dataSnapshot);
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
    public void wrapDataSnapshotValue(final IntegerWrapper wrapper, DataSnapshot dataSnapshot) {
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
        boolean sufficient = stars >= price;
        return sufficient;
    }

    /**
     * Updates the users data in the database, e.g. sets their stars to newStars and adds the item
     * to their inventory.
     * @param itemName Name of the item added.
     * @param newStars New amount of stars after purchase.
     * @param currentUserRef Reference to the current user in database.
     */
    protected void updateUser(String itemName, int newStars, DatabaseReference currentUserRef,
                            final TextView textView) {
        updateUserStars(newStars, currentUserRef.child("stars"));
        addUserItem(currentUserRef.child(items).child(colors).child(itemName));
        setTextViewMessage(textView, "Purchase successful.");
        resetTextViewMessage(textView, delayToClear);
    }

    /**
     * Updates the users stars after a purchase.
     * @param newStars New amount of stars the user posesses.
     * @param currentUserStarsRef Reference to where the users stars are stored in the database.
     */
    protected void updateUserStars(int newStars, DatabaseReference currentUserStarsRef) {
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
        textView.setText(message);
    }

    /**
     * Clears the TextView after a certain delay.
     * @param textView TextView to be cleared.
     * @param delay Delay until message is cleared in milliseconds.
     */
    protected void resetTextViewMessage(final TextView textView, int delay) {
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