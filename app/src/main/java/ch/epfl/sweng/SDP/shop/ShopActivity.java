package ch.epfl.sweng.SDP.shop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;

class BooleanWrapper {
    private boolean b;
    BooleanWrapper(boolean b) {
        this.b = b;
    }
    protected void setBoolean(boolean newb) {
        b = newb;
    }
    protected boolean getBoolean() {
        return b;
    }
}

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

public class ShopActivity extends Activity {
    final FirebaseDatabase db = FirebaseDatabase.getInstance("https://gyrodraw.firebaseio.com/");
    final DatabaseReference dbRef = db.getReference();
    final DatabaseReference usersRef = dbRef.child("users");
    final DatabaseReference shopColorsRef = dbRef.child("items").child("colors");
    final DatabaseReference userColorsRef = usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("items").child("colors");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> shopColors = getColorsFromDatabase(); //does not wait until database responded
        LinearLayout linearLayout = findViewById(R.id.linearLayout); //why is it null?
        try {
            wait(5000);
        }
        catch(Exception e) {
            System.out.println("exception");
        }
        if (shopColors.size() < 1) {
            TextView t = new TextView(this);
            t.setText("Currently unable to find any shop items.");
            linearLayout.addView(t);
        }
        else {
            for (String s : shopColors) {
                Button b = initializeButton(s);
                addOnClickListenerToButton(b);
                linearLayout.addView(b);
            }
        }
        setContentView(R.layout.shop_activity);
        setReturn();
    }


    private ArrayList<String> getColorsFromDatabase() {
        final ArrayList<String> colors = new ArrayList<>();
        shopColorsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.getKey());
                    colors.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        return colors;
    }

    private Button initializeButton(String s) {
        Button b = new Button(this);
        b.setText(s);
        return b;
    }

    private void addOnClickListenerToButton(final Button b) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseItem(b.getText().toString());
            }
        };
        b.setOnClickListener(onClickListener);
    }

    private void setReturn() {
        Button ret = findViewById(R.id.returnFromShop);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoHome();
            }
        });
    }

    private void purchaseItem(String s) {
        final BooleanWrapper b = new BooleanWrapper(false);
        final IntegerWrapper stars = new IntegerWrapper(0);
        final IntegerWrapper price = new IntegerWrapper(0);
        alreadyOwned(s, b);
        getStars(stars);
        getPrice(price, s);
        if (!b.getBoolean() && sufficientCurrency(stars.getInt(), price.getInt())) {
            updateUser(s, stars.getInt() - price.getInt());
        }
    }

    private void alreadyOwned(String s, final BooleanWrapper b) throws DatabaseException {
        userColorsRef.orderByKey().equalTo(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                b.setBoolean(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void getStars(final IntegerWrapper i) throws DatabaseException {
        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("stars").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    i.setInt((int) dataSnapshot.getValue());
                }
                else {
                    i.setInt(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private void getPrice(final IntegerWrapper i, String item) throws IllegalArgumentException, DatabaseException {
        shopColorsRef.child(item).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    i.setInt((int) dataSnapshot.getValue());
                }
                else {
                    throw new IllegalArgumentException("Item does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    private boolean sufficientCurrency(int stars, int price) {
        return (stars - price) >= 0;
    }

    private void updateUser(String item, int newStars) {
        usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("stars").setValue(newStars, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
            }
        });
        userColorsRef.child(item).setValue(true, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw databaseError.toException();
                }
            }
        });
    }

    public void gotoHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}