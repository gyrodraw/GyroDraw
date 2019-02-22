package ch.epfl.sweng.GyroDraw.home;

import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.FRIENDS;
import static ch.epfl.sweng.GyroDraw.firebase.AccountAttributes.USERNAME;
import static ch.epfl.sweng.GyroDraw.firebase.FbDatabase.checkForDatabaseError;
import static ch.epfl.sweng.GyroDraw.firebase.FbDatabase.getAccountAttribute;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.bounceButton;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.getLeagueColorId;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.getLeagueImageId;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.getLeagueTextId;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.getMainAmplitude;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.getMainFrequency;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.isPointInsideView;
import static ch.epfl.sweng.GyroDraw.utils.LayoutUtils.pressButton;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.ONLINE;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.changeOnlineStatus;
import static ch.epfl.sweng.GyroDraw.utils.OnlineStatus.changeToOfflineOnDisconnect;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.sweng.GyroDraw.MainActivity;
import ch.epfl.sweng.GyroDraw.NoBackPressActivity;
import ch.epfl.sweng.GyroDraw.R;
import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.FbAuthentication;
import ch.epfl.sweng.GyroDraw.firebase.FbDatabase;
import ch.epfl.sweng.GyroDraw.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.GyroDraw.game.LoadingScreenActivity;
import ch.epfl.sweng.GyroDraw.game.drawing.DrawingOfflineActivity;
import ch.epfl.sweng.GyroDraw.home.battleLog.BattleLogActivity;
import ch.epfl.sweng.GyroDraw.home.gallery.GalleryActivity;
import ch.epfl.sweng.GyroDraw.home.leaderboard.LeaderboardActivity;
import ch.epfl.sweng.GyroDraw.home.leagues.LeaguesActivity;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbForAccount;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.GyroDraw.shop.ShopActivity;
import ch.epfl.sweng.GyroDraw.utils.GlideUtils;
import ch.epfl.sweng.GyroDraw.utils.LayoutUtils.AnimMode;
import ch.epfl.sweng.GyroDraw.utils.OnSwipeTouchListener;
import ch.epfl.sweng.GyroDraw.utils.network.ConnectivityWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Class representing the homepage of the app.
 */
public class HomeActivity extends NoBackPressActivity {

    public static final String GAME_MODE = "mode";

    private static final String TAG = "HomeActivity";

    private static final int DRAW_BUTTON_FREQUENCY = 20;
    private static final int LEAGUE_IMAGE_FREQUENCY = 30;
    private static final double DRAW_BUTTON_AMPLITUDE = 0.2;
    
    private static final int NORMAL_MODE = 0;
    private static final int SPECIAL_MODE = 1;

    private static boolean enableBackgroundAnimation = true;

    private Dialog profileWindow;
    private Dialog friendRequestWindow;

    private ValueEventListener listenerFriendsRequest = new OnSuccessValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Integer stateValue = child.getValue(Integer.class);
                if (stateValue != null) {
                    FriendsRequestState state = FriendsRequestState.fromInteger(stateValue);

                    if (state == FriendsRequestState.RECEIVED) {
                        getFriendsUsernameAndShowPopUp(child.getKey());
                    }
                }
            }
        }
    };

    /**
     * Disables the background animation. This method should be in every HomeActivity test.
     */
    @VisibleForTesting
    public static void disableBackgroundAnimation() {
        enableBackgroundAnimation = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profileWindow = new Dialog(this);

        friendRequestWindow = new Dialog(this);
        friendRequestWindow.setCancelable(false);

        if (enableBackgroundAnimation) {
            GlideUtils.startBackgroundAnimation(this);
        }

        LocalDbForAccount localDb = new LocalDbHandlerForAccount(this, null, 1);
        localDb.retrieveAccount(Account.getInstance(this));

        // Update the user online status on Firebase and set the onDisconnect listener
        updateUserStatusOnFirebase();

        // Add listener to check for new friends requests
        addListenerForFriendsRequests();

        final ImageView drawButton = findViewById(R.id.drawButton);
        final ImageView practiceButton = findViewById(R.id.practiceButton);
        final ImageView mysteryButton = findViewById(R.id.mysteryButton);
        final Button usernameButton = findViewById(R.id.usernameButton);
        final ImageView leaderboardButton = findViewById(R.id.leaderboardButton);
        final ImageView shopButton = findViewById(R.id.shopButton);
        final ImageView galleryButton = findViewById(R.id.galleryButton);
        final ImageView battleLogButton = findViewById(R.id.battleLogButton);
        final TextView shopText = findViewById(R.id.shopText);
        final TextView galleryText = findViewById(R.id.galleryText);
        final TextView leaderboardText = findViewById(R.id.leaderboardText);
        final TextView battleLogText = findViewById(R.id.battleLogText);
        final ImageView trophiesButton = findViewById(R.id.trophiesButton);
        final TextView trophiesCount = findViewById(R.id.trophiesCount);
        final ImageView starsButton = findViewById(R.id.starsButton);
        final TextView starsCount = findViewById(R.id.starsCount);
        final ImageView leagueImage = findViewById(R.id.leagueImage);
        final TextView leagueText = findViewById(R.id.leagueText);

        usernameButton.setText(Account.getInstance(this).getUsername());
        trophiesCount.setText(String.valueOf(Account.getInstance(this).getTrophies()));
        starsCount.setText(String.valueOf(Account.getInstance(this).getStars()));

        leagueText.setTypeface(typeOptimus);
        usernameButton.setTypeface(typeMuro);
        trophiesCount.setTypeface(typeMuro);
        starsCount.setTypeface(typeMuro);
        shopText.setTypeface(typeMuro);
        galleryText.setTypeface(typeMuro);
        leaderboardText.setTypeface(typeMuro);
        battleLogText.setTypeface(typeMuro);

        setListener(drawButton, DRAW_BUTTON_AMPLITUDE, DRAW_BUTTON_FREQUENCY);
        setListener(leaderboardButton, getMainAmplitude(), getMainFrequency());
        setListener(shopButton, getMainAmplitude(), getMainFrequency());
        setListener(galleryButton, getMainAmplitude(), getMainFrequency());
        setListener(battleLogButton, getMainAmplitude(), getMainFrequency());
        setListener(trophiesButton, getMainAmplitude(), getMainFrequency());
        setListener(starsButton, getMainAmplitude(), getMainFrequency());
        setListener(leagueImage, getMainAmplitude(), LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, getMainAmplitude(), getMainFrequency());
        setListener(practiceButton, getMainAmplitude(), getMainFrequency());
        setListener(mysteryButton, getMainAmplitude(), getMainFrequency());

        findViewById(R.id.backgroundAnimation).setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                launchActivity(ShopActivity.class);
            }
        });

        setLeague();
    }

    /**
     * Changes the user's state on Firebase to online.
     */
    private void updateUserStatusOnFirebase() {
        String userId = Account.getInstance(getApplicationContext())
                .getUserId();

        changeOnlineStatus(userId, ONLINE, FbDatabase.createCompletionListener());

        // On user disconnection, update Firebase
        changeToOfflineOnDisconnect(userId);
    }

    private void addListenerForFriendsRequests() {
        FbDatabase.setListenerToAccountAttribute(Account.getInstance(this).getUserId(),
                FRIENDS, listenerFriendsRequest);
    }

    @VisibleForTesting
    public Dialog getFriendRequestWindow() {
        return friendRequestWindow;
    }

    /**
     * Signs the current user out and starts the {@link MainActivity}.
     */
    private void signOut() {
        FbAuthentication.signOut(this, new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    final Toast toastSignOut = Toast.makeText(getApplicationContext(),
                            "Signing out...", Toast.LENGTH_SHORT);
                    toastSignOut.show();

                    // Update Firebase, delete the account instance and launch MainActivity
                    changeOnlineStatus(
                            Account.getInstance(getApplicationContext()).getUserId(),
                            OFFLINE, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(
                                        @Nullable DatabaseError databaseError,
                                        @NonNull DatabaseReference databaseReference) {
                                    checkForDatabaseError(databaseError);
                                    successfulSignOut(toastSignOut);
                                }
                            });
                } else {
                    Log.e(TAG, "Sign out failed!");
                }
            }
        });
        profileWindow.dismiss();
    }

    private void successfulSignOut(Toast toast) {
        Account.deleteAccount();
        toast.cancel();
        launchActivity(MainActivity.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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
                        if (id == R.id.drawButton) {
                            ((ImageView) view)
                                    .setImageResource(R.drawable.draw_button);
                        }
                        bounceButton(view, amplitude, frequency, animMode, context);
                        if (isPointInsideView(event.getRawX(), event.getRawY(), view)) {
                            listenerEventSelector(view, id);
                        }
                        break;
                    default:
                }
                return true;
            }
        });
    }

    // Sets the listener for the friend request popup buttons using the given userId
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
                ((ImageView) view).setImageResource(R.drawable.draw_button);
                launchOnlineGame(NORMAL_MODE);
                break;
            case R.id.leaderboardButton:
                launchActivity(LeaderboardActivity.class);
                break;
            case R.id.shopButton:
                launchActivity(ShopActivity.class);
                break;
            case R.id.galleryButton:
                launchActivity(GalleryActivity.class);
                break;
            case R.id.battleLogButton:
                launchActivity(BattleLogActivity.class);
                break;
            case R.id.leagueImage:
                launchActivity(LeaguesActivity.class);
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
                launchActivity(DrawingOfflineActivity.class);
                break;
            case R.id.mysteryButton:
                launchOnlineGame(SPECIAL_MODE);
                break;
            default:
        }
    }

    private void launchOnlineGame(int gameMode) {
        if (ConnectivityWrapper.isOnline(this)) {
            // Prevents that the user launches two online games at the same time.
            setGameButtons(false);
            Intent intent = new Intent(this, LoadingScreenActivity.class);
            intent.putExtra(GAME_MODE, gameMode);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
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

        double roundedAverage = Math.round(userAccount.getAverageRating() * 10) / 10.;
        TextView averageStarsNumber = profileWindow.findViewById(R.id.averageStarsNumber);
        averageStarsNumber.setText(String.valueOf(roundedAverage));

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
     * Gets the username corresponding to the given id and shows the friends request popup.
     *
     * @param id id from user that has sent a request
     */
    @VisibleForTesting
    public void getFriendsUsernameAndShowPopUp(final String id) {
        getAccountAttribute(id, USERNAME, new OnSuccessValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                showFriendRequestPopup(name, id);
            }
        });
    }

    /**
     * Displays the friends request popup.
     *
     * @param name name of the user that sent the request
     * @param id id of the user that sent the request
     */
    @VisibleForTesting
    public void showFriendRequestPopup(String name, String id) {
        assert name != null : "name is null";

        friendRequestWindow.setContentView(R.layout.activity_friend_request_pop_up);

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

    @Override
    public void onResume() {
        super.onResume();

        Account account = Account.getInstance(this);

        // Update values of the text views
        ((TextView) findViewById(R.id.starsCount)).setText(String.valueOf(account.getStars()));
        ((TextView) findViewById(R.id.trophiesCount)).setText(String.valueOf(
                account.getTrophies()));
        setGameButtons(true);
    }

    /**
     * Sets the game buttons locked or unlocked.
     *
     * @param state the target state
     */
    private void setGameButtons(boolean state) {
        findViewById(R.id.practiceButton).setEnabled(state);
        findViewById(R.id.drawButton).setEnabled(state);
        findViewById(R.id.mysteryButton).setEnabled(state);
    }
}
