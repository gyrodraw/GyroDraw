package ch.epfl.sweng.SDP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.auth.ConstantsWrapper;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.SDP.shop.Shop;

/**
 * Class containing useful and widely used methods. It should be inherited by all the other
 * activities.
 */
public abstract class Activity extends AppCompatActivity {

    /**
     * Start the specified activity.
     *
     * @param activityClass the class of the activity to launch
     */
    public void launchActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    /**
     * Set the visibility of the views corresponding to the given ids to the given value.
     *
     * @param visibility the value to set the visibility at
     * @param ids        the ids of the views whose visibility is to be set
     */
    public void setVisibility(int visibility, int... ids) {
        for (int id : ids) {
            findViewById(id).setVisibility(visibility);
        }
    }

    /**
     * Set the visibility of the given views to the given value.
     *
     * @param visibility the value to set the visibility at
     * @param views      the views whose visibility is to be set
     */
    public void setVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    /**
     * Set typeface to the given text views.
     *
     * @param typeface the typeface to be set
     * @param views    the text views whose typeface is to be set
     */
    public void setTypeFace(Typeface typeface, View... views) {
        for (View view : views) {
            ((TextView) view).setTypeface(typeface);
        }
    }

    /**
     * This methods creates a TextView according to the given parameters.
     *
     * @param text         String displayed in textview
     * @param color        Color of the textview
     * @param size         Size of the textview
     * @param typeface     Typeface of the textview
     * @param layoutParams Layout parameters of the textview
     * @return The newly created textview
     */
    @SuppressLint("NewApi")
    public TextView createTextView(String text, int color, int size, Typeface typeface,
                                   LinearLayout.LayoutParams layoutParams) {
        TextView textView = new TextView(this);
        styleView(textView, text, color,
                size, typeface, layoutParams);

        return textView;

    }

    /**
     * Add views to a layout.
     *
     * @param layout Layout where the views will be added
     * @param views  Views to be added
     * @return The layout with the views added
     */
    public LinearLayout addViews(LinearLayout layout, View... views) {
        for (View view : views) {
            layout.addView(view);
        }

        return layout;
    }

    private void styleView(TextView view, String text, int color, int size, Typeface typeface,
                           LinearLayout.LayoutParams layoutParams) {
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(typeface);
        view.setLayoutParams(layoutParams);
    }

    /**
     * Clones the {@link Account}, corresponding to the user logged in,
     * from Firebase, and stores it in the local database.
     *
     * @param snapshot the {@link DataSnapshot} corresponding to an account on Firebase Database
     */
    protected void cloneAccountFromFirebase(@NonNull DataSnapshot snapshot) {
        HashMap<String, HashMap<String, Object>> userEntry =
                (HashMap<String, HashMap<String, Object>>) snapshot.getValue();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (userEntry != null && currentUser != null) {
            HashMap<String, Object> user = userEntry
                    .get(currentUser.getUid());
            if (user != null) {
                Account.createAccount(getApplicationContext(),
                        new ConstantsWrapper(), (String) user.get("username"),
                        (String) user.get("email"), (String) user.get("currentLeague"),
                        ((Long) user.get("trophies")).intValue(),
                        ((Long) user.get("stars")).intValue(),
                        ((Long) user.get("matchesWon")).intValue(),
                        ((Long) user.get("totalMatches")).intValue(),
                        (Long) user.get("averageRating"),
                        ((Long) user.get("maxTrophies")).intValue(),
                        Shop.firebaseToListShopItem((HashMap<String, String>)
                                user.get("boughtItems")));


                LocalDbHandlerForAccount handler = new LocalDbHandlerForAccount(
                        getApplicationContext(), null, 1);
                handler.saveAccount(Account.getInstance(getApplicationContext()));
            }
        }
    }
}
