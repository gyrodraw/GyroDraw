package ch.epfl.sweng.SDP;

import static android.view.View.VISIBLE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.ONLINE;
import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.auth.ConstantsWrapper;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.home.HomeActivity;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.SDP.shop.Shop;
import ch.epfl.sweng.SDP.utils.ImageStorageManager;
import ch.epfl.sweng.SDP.utils.OnlineStatus;
import ch.epfl.sweng.SDP.utils.TypefaceLibrary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

/**
 * Class containing useful and widely used methods. It should be inherited by all the other
 * activities.
 */
public abstract class Activity extends AppCompatActivity {

    private static final int PERMISSION_EXTERNAL_STORAGE = 1;

    protected Typeface typeMuro;
    protected Typeface typeOptimus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceLibrary.setContext(this);

        typeMuro = TypefaceLibrary.getTypeMuro();
        typeOptimus = TypefaceLibrary.getTypeOptimus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        // Set the content to appear under the system bars so that the
        // content doesn't resize when the system bars hide and show.
        // hides UI bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        setLayoutToFullscreen();
                    }
                }
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setLayoutToFullscreen();
        }
    }

    private void setLayoutToFullscreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * Starts the specified activity.
     *
     * @param activityClass the class of the activity to launch
     */
    public void launchActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    /**
     * Sets the visibility of the views corresponding to the given ids to the given value.
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
     * Sets the visibility of the given views to the given value.
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
     * Sets typeface to the given text views.
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
     * Adds views to a layout.
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
     * Clones the {@link Account}, corresponding to the user logged in, from Firebase, and stores it
     * in the local database.
     *
     * @param snapshot the {@link DataSnapshot} corresponding to an account on Firebase Database
     */
    protected void cloneAccountFromFirebase(@NonNull DataSnapshot snapshot) {
        HashMap<String, HashMap<String, Object>> userEntry =
                (HashMap<String, HashMap<String, Object>>) snapshot.getValue();

        if (userEntry != null) {
            String currentUserId = (String) userEntry.keySet().toArray()[0];
            HashMap<String, Object> user = userEntry.get(currentUserId);

            if (user != null) {
                Account.createAccount(getApplicationContext(),
                        new ConstantsWrapper(), (String) user.get("username"),
                        (String) user.get("email"), (String) user.get("currentLeague"),
                        ((Long) user.get("trophies")).intValue(),
                        ((Long) user.get("stars")).intValue(),
                        ((Long) user.get("matchesWon")).intValue(),
                        ((Long) user.get("totalMatches")).intValue(),
                        Double.parseDouble((user.get("averageRating")).toString()),
                        ((Long) user.get("maxTrophies")).intValue(),
                        Shop.firebaseToListShopItem((HashMap<String, String>)
                                user.get("boughtItems")));

                LocalDbHandlerForAccount handler = new LocalDbHandlerForAccount(
                        getApplicationContext(), null, 1);
                handler.saveAccount(Account.getInstance(getApplicationContext()));
            }
        }
    }


    /**
     * Callback function fired when user allowed or disallowed permissions.
     * @param requestCode request code when asking for permissions
     * @param permissions permissions asked
     * @param grantResults results of the permissions asked
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE: {
                // Check if user granted permissions. If it is the case save the corresponding file
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions granted
                    ImageStorageManager.saveImage(this);

                }
                return;
            }

            default:
                // Does nothing for other permissions
        }
    }

    /**
     * Checks if the user is already online on another device. If it is the case, an error message
     * is displayed while waiting for the other device to go offline. If it is not the case, the
     * {@link MainActivity} is launched.
     *
     * @param errorMessage the {@link TextView} corresponding to the error message
     */
    protected void handleUserStatus(final TextView errorMessage) {
        final DatabaseReference statusRef = Database.getReference(format("users.%s.online",
                Account.getInstance(getApplicationContext()).getUserId()));

        statusRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        OnlineStatus isOnline = OnlineStatus.fromInteger(
                                dataSnapshot.getValue(int.class));
                        if (isOnline == ONLINE) {
                            errorMessage.setText(getString(R.string.already_logged_in));
                            errorMessage.setVisibility(VISIBLE);
                        } else {
                            statusRef.removeEventListener(this);
                            launchActivity(HomeActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }
}
