package ch.epfl.sweng.SDP.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.Manifest;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.CheckConnection;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.LoadingScreenActivity;
import ch.epfl.sweng.SDP.game.drawing.DrawingOffline;
import ch.epfl.sweng.SDP.game.drawing.DrawingOfflineItems;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.SDP.shop.ShopActivity;

import ch.epfl.sweng.SDP.utils.ImageStorageManager;
import ch.epfl.sweng.SDP.utils.LayoutUtils.AnimMode;

import static ch.epfl.sweng.SDP.utils.LayoutUtils.bounceButton;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueColorId;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueImageId;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueTextId;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getMainAmplitude;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getMainFrequency;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.pressButton;
import static java.lang.String.format;

import com.facebook.FacebookSdk;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";
    private static final String MURO_PATH = "fonts/Muro.otf";

    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;

    private static boolean enableBackgroundAnimation = true;

    private Dialog profileWindow;
    private Dialog friendRequestWindow;

    private ValueEventListener listenerFriendsRequest = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Integer stateValue = child.getValue(Integer.class);
                if (stateValue != null) {
                    FriendsRequestState state =
                            FriendsRequestState.fromInteger(stateValue);

                    if (state == FriendsRequestState.RECEIVED) {
                        final String id = child.getKey();
                        Database.getReference(format("users.%s.username", id))
                                .addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(
                                                    @NonNull DataSnapshot dataSnapshot) {
                                                String name = dataSnapshot
                                                        .getValue(String.class);
                                                showFriendRequestPopup(name, id);
                                            }

                                            @Override
                                            public void onCancelled(
                                                    @NonNull DatabaseError databaseError) {
                                                throw databaseError.toException();
                                            }
                                        });
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };

    /**
     * Disables the background animation. Call this method in every HomeActivity test.
     */
    public static void disableBackgroundAnimation() {
        enableBackgroundAnimation = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_home);
        profileWindow = new Dialog(this);
        friendRequestWindow = new Dialog(this);
        friendRequestWindow.setCancelable(false);

        overridePendingTransition(0, 0);
        isStoragePermissionGranted();

        if (enableBackgroundAnimation) {
            Glide.with(this).load(R.drawable.background_animation)
                    .into((ImageView) findViewById(R.id.homeBackgroundAnimation));
        }

        LocalDbHandlerForAccount localDb = new LocalDbHandlerForAccount(this, null, 1);
        localDb.retrieveAccount(Account.getInstance(this));

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.league_1);
        ImageStorageManager.saveImage(bm, "HEY",getApplicationContext());
        shareDrawingToFacebook(bm);

        addListenerForFriendsRequests();

        final ImageView drawButton = findViewById(R.id.drawButton);
        final ImageView practiceButton = findViewById(R.id.practiceButton);
        final ImageView mysteryButton = findViewById(R.id.mysteryButton);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final ImageView leaderboardButton = findViewById(R.id.leaderboardButton);
        final ImageView battleLogButton = findViewById(R.id.battleLogButton);
        final ImageView trophiesButton = findViewById(R.id.trophiesButton);
        final TextView trophiesCount = findViewById(R.id.trophiesCount);
        final ImageView starsButton = findViewById(R.id.starsButton);
        final TextView starsCount = findViewById(R.id.starsCount);
        final ImageView leagueImage = findViewById(R.id.leagueImage);

        practiceButton.setColorFilter(new LightingColorFilter(Color.WHITE,
                getResources().getColor(R.color.colorButtonBlue)));
        mysteryButton.setColorFilter(new LightingColorFilter(Color.WHITE,
                getResources().getColor(R.color.colorButtonBlue)));

        usernameButton.setText(Account.getInstance(this).getUsername());
        trophiesCount.setText(String.valueOf(Account.getInstance(this).getTrophies()));
        starsCount.setText(String.valueOf(Account.getInstance(this).getStars()));

        TextView leagueText = findViewById(R.id.leagueText);
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), MURO_PATH);
        Typeface typeOptimus = Typeface.createFromAsset(getAssets(), "fonts/Optimus.otf");

        leagueText.setTypeface(typeOptimus);
        usernameButton.setTypeface(typeMuro);
        trophiesCount.setTypeface(typeMuro);
        starsCount.setTypeface(typeMuro);

        setListener(drawButton, DRAW_BUTTON_AMPLITUDE, DRAW_BUTTON_FREQUENCY);
        setListener(leaderboardButton, getMainAmplitude(), getMainFrequency());
        setListener(battleLogButton, getMainAmplitude(), getMainFrequency());
        setListener(trophiesButton, getMainAmplitude(), getMainFrequency());
        setListener(starsButton, getMainAmplitude(), getMainFrequency());
        setListener(leagueImage, getMainAmplitude(), LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, getMainAmplitude(), getMainFrequency());
        setListener(practiceButton, getMainAmplitude(), getMainFrequency());
        setListener(mysteryButton, getMainAmplitude(), getMainFrequency());

        setLeague();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }

    private void shareDrawingToFacebook(Bitmap bitmap) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/gyrodraw.appspot.com/o/user1.jpg?alt=media&token=8db5c201-aadd-4614-bd10-dbc44dbbee6c"))
                .build();

        ShareDialog.show(this,linkContent);
    }

    private void shareDrawing(Bitmap drawing) {

        saveDrawingToCache(drawing);

        // Get image file
        File imagePath = new File(getApplicationContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.myapp.fileprovider", newFile);

        // Share
        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

    }

    private void saveDrawingToCache(Bitmap bitmap) {
        // save bitmap to cache directory
        try {

            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addListenerForFriendsRequests() {
        Database.getReference(format("users.%s.friends", Account.getInstance(this).getUserId()))
                .addValueEventListener(listenerFriendsRequest);
    }

    // Launch the LeaguesActivity.
    private void showLeagues() {
        launchActivity(LeaguesActivity.class);
    }

    /**
     * Signs the current user out and starts the {@link MainActivity}.
     */
    private void signOut() {
        final Toast toastSignOut = makeAndShowToast("Signing out...");

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Account.deleteAccount();
                            toastSignOut.cancel();
                            launchActivity(MainActivity.class);
                            finish();
                        } else {
                            Log.e(TAG, "Sign out failed!");
                        }
                    }
                });
        profileWindow.dismiss();
    }

    private Toast makeAndShowToast(String msg) {
        assert msg != null;
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    private void setListener(final View view, final double amplitude, final int frequency) {
        final Context context = this;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int id = view.getId();
                AnimMode animMode;

                switch (id) {
                    case R.id.starsButton:
                        animMode = AnimMode.LEFT;
                        break;
                    case R.id.trophiesButton:
                        animMode = AnimMode.LEFT;
                        break;
                    case R.id.practiceButton:
                        animMode = AnimMode.LEFT;
                        break;
                    case R.id.mysteryButton:
                        animMode = AnimMode.RIGHT;
                        break;
                    default:
                        animMode = AnimMode.CENTER;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (id == R.id.drawButton) {
                            ((ImageView) view)
                                    .setImageResource(R.drawable.draw_button_pressed);
                        }
                        pressButton(view, animMode, context);
                        break;
                    case MotionEvent.ACTION_UP:
                        listenerEventSelector(view, id);
                        bounceButton(view, amplitude, frequency, animMode, context);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    // Set the listener for the friend request popup buttons using the given userId
    // as the id of the sender of the request
    private void setFriendsRequestListener(final View view, final String userId) {
        final Context context = this;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int id = view.getId();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressButton(view, AnimMode.CENTER, context);
                        break;
                    case MotionEvent.ACTION_UP:
                        listenerFriendsRequestEventSelector(id, userId);
                        bounceButton(view, context);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    private void listenerEventSelector(final View view, int resourceId) {
        switch (resourceId) {
            case R.id.drawButton:
                if (CheckConnection.isOnline(this)) {
                    ((ImageView) view).setImageResource(R.drawable.draw_button);
                    launchActivity(LoadingScreenActivity.class);
                } else {
                    Toast.makeText(this, R.string.no_internet,
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.leaderboardButton:
                launchActivity(LeaderboardActivity.class);
                break;
            case R.id.battleLogButton:
                launchActivity(BattleLogActivity.class);
                break;
            case R.id.leagueImage:
                showLeagues();
                break;
            case R.id.usernameButton:
                showProfilePopup();
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.crossText:
                profileWindow.dismiss();
                break;
            case R.id.practiceButton:
                launchActivity(DrawingOffline.class);
                break;
            case R.id.mysteryButton:
                launchActivity(DrawingOfflineItems.class);
                break;
            default:
        }
    }

    // Listener for the friends request popup buttons
    private void listenerFriendsRequestEventSelector(int resourceId, String userId) {
        switch (resourceId) {
            case R.id.acceptButton:
                Account.getInstance(this).addFriend(userId);
                friendRequestWindow.dismiss();
                break;
            case R.id.rejectButton:
                Account.getInstance(this).removeFriend(userId);
                friendRequestWindow.dismiss();
                break;
            default:
        }
    }

    private void setMuroFont() {
        Typeface typeMuro = Typeface.createFromAsset(getAssets(), MURO_PATH);

        TextView profileTextView = profileWindow.findViewById(R.id.profileText);
        profileTextView.setTypeface(typeMuro);
        TextView gamesWonText = profileWindow.findViewById(R.id.gamesWonText);
        gamesWonText.setTypeface(typeMuro);
        TextView gamesLostText = profileWindow.findViewById(R.id.gamesLostText);
        gamesLostText.setTypeface(typeMuro);
        TextView averageStarsText = profileWindow.findViewById(R.id.averageStarsText);
        averageStarsText.setTypeface(typeMuro);
        TextView maxTrophiesText = profileWindow.findViewById(R.id.maxTrophiesText);
        maxTrophiesText.setTypeface(typeMuro);
        TextView gamesWonNumber = profileWindow.findViewById(R.id.gamesWonNumber);
        gamesWonNumber.setTypeface(typeMuro);
        TextView gamesLostNumber = profileWindow.findViewById(R.id.gamesLostNumber);
        gamesLostNumber.setTypeface(typeMuro);
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setTypeface(typeMuro);
        TextView maxTrophiesNumber = profileWindow.findViewById(R.id.maxTrophiesNumber);
        maxTrophiesNumber.setTypeface(typeMuro);
        TextView crossText = profileWindow.findViewById(R.id.crossText);
        crossText.setTypeface(typeMuro);
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        signOutButton.setTypeface(typeMuro);
    }

    private void showProfilePopup() {
        profileWindow.setContentView(R.layout.activity_profile_pop_up);

        Account userAccount = Account.getInstance(this);

        this.setMuroFont();

        TextView gamesWonNumber = profileWindow.findViewById(R.id.gamesWonNumber);
        gamesWonNumber.setText(String.valueOf(userAccount.getMatchesWon()));
        TextView gamesLostNumber = profileWindow.findViewById(R.id.gamesLostNumber);
        gamesLostNumber.setText(String.valueOf(userAccount.getTotalMatches()));
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setText(String.valueOf(userAccount.getAverageRating()));
        TextView maxTrophiesNumber = profileWindow.findViewById(R.id.maxTrophiesNumber);
        maxTrophiesNumber.setText(String.valueOf(userAccount.getMaxTrophies()));
        TextView crossText = profileWindow.findViewById(R.id.crossText);
        setListener(crossText, getMainAmplitude(), getMainFrequency());
        Button signOutButton = profileWindow.findViewById(R.id.signOutButton);
        setListener(signOutButton, getMainAmplitude(), getMainFrequency());

        profileWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileWindow.show();
    }


    /**
     * Method called when shop button is clicked. Starts shop activity.
     * @param view View referring the shop button
     */
    public void onShopButtonClicked(View view) {
        launchActivity(ShopActivity.class);
    }

    @VisibleForTesting
    public void showFriendRequestPopup(String name, String id) {
        assert name != null : "name is null";

        friendRequestWindow.setContentView(R.layout.activity_friend_request_pop_up);

        Typeface typeMuro = Typeface.createFromAsset(getAssets(), MURO_PATH);

        TextView requestSender = friendRequestWindow.findViewById(R.id.requestSender);
        requestSender.setTypeface(typeMuro);
        requestSender.setText(name);

        TextView requestMessage = friendRequestWindow.findViewById(R.id.requestMessage);
        requestMessage.setTypeface(typeMuro);

        Button acceptButton = friendRequestWindow.findViewById(R.id.acceptButton);
        acceptButton.setTypeface(typeMuro);
        setFriendsRequestListener(acceptButton, id);

        Button rejectButton = friendRequestWindow.findViewById(R.id.rejectButton);
        rejectButton.setTypeface(typeMuro);
        setFriendsRequestListener(rejectButton, id);

        friendRequestWindow.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        friendRequestWindow.show();
    }

    private void setLeague() {
        String league = Account.getInstance(this).getCurrentLeague();
        TextView leagueText = findViewById(R.id.leagueText);

        leagueText.setText(getLeagueTextId(league));
        leagueText.setTextColor(getResources().getColor(getLeagueColorId(league)));
        ((ImageView) findViewById(R.id.leagueImage)).setImageResource(getLeagueImageId(league));
    }
}
