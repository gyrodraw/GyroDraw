package ch.epfl.sweng.GyroDraw;

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

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.auth.ConstantsWrapper;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.home.HomeActivity;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForAccount;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.GyroDraw.shop.Shop;
import ch.epfl.sweng.GyroDraw.utils.ImageStorageManager;
import ch.epfl.sweng.GyroDraw.utils.OnlineStatus;
import ch.epfl.sweng.GyroDraw.utils.TypefaceLibrary;

import static android.view.View.VISIBLE;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.STATUS;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.ONLINE;

/**
 * Class containing useful and widely used methods. It should be inherited by all the other
 * activities.
 */
public abstract class BaseActivity extends AppCompatActivity {

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
     * Callback function fired when the user is allowed or disallowed permissions.
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
                // Check if user granted permissions. If it is the case, save the corresponding file
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissions granted
                    ImageStorageManager.saveImageFromDb(this);
                }
                return;
            }

            default:
                // Does nothing for other permissions
        }
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

                LocalDbForAccount handler = new LocalDbHandlerForAccount(
                        getApplicationContext(), null, 1);
                handler.saveAccount(Account.getInstance(getApplicationContext()));
            }
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
        final String userId = Account.getInstance(this).getUserId();
        FbDatabase.setListenerToAccountAttribute(
                userId, STATUS,
                new OnSuccessValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        OnlineStatus isOnline = OnlineStatus.fromInteger(
                                dataSnapshot.getValue(int.class));
                        if (isOnline == ONLINE) {
                            errorMessage.setText(getString(R.string.already_logged_in));
                            errorMessage.setVisibility(VISIBLE);
                        } else {
                            FbDatabase.removeListenerFromAccountAttribute(userId, STATUS, this);
                            launchActivity(HomeActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                    }
                });
    }
}
