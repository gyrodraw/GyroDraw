package ch.epfl.sweng.SDP.home;

import static ch.epfl.sweng.SDP.utils.LayoutUtils.bounceButton;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueColorId;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueImageId;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getLeagueTextId;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getMainAmplitude;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.getMainFrequency;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.pressButton;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.OFFLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.ONLINE;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.changeOnlineStatus;
import static ch.epfl.sweng.SDP.utils.OnlineStatus.changeToOfflineOnDisconnect;
import static java.lang.String.format;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.sweng.SDP.BaseActivity;
import ch.epfl.sweng.SDP.MainActivity;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.firebase.Database;
import ch.epfl.sweng.SDP.game.LoadingScreenActivity;
import ch.epfl.sweng.SDP.game.drawing.DrawingOffline;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.SDP.shop.ShopActivity;
import ch.epfl.sweng.SDP.utils.CheckConnection;
import ch.epfl.sweng.SDP.utils.LayoutUtils.AnimMode;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Class representing the homepage of the app.
 */
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
            Glide.with(this).load(R.drawable.background_animation)
                    .into((ImageView) findViewById(R.id.homeBackgroundAnimation));
        }

        LocalDbHandlerForAccount localDb = new LocalDbHandlerForAccount(this, null, 1);
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
        final ImageView battleLogButton = findViewById(R.id.battleLogButton);
        final ImageView trophiesButton = findViewById(R.id.trophiesButton);
        final TextView trophiesCount = findViewById(R.id.trophiesCount);
        final ImageView starsButton = findViewById(R.id.starsButton);
        final TextView starsCount = findViewById(R.id.starsCount);
        final ImageView leagueImage = findViewById(R.id.leagueImage);

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
        setListener(shopButton, getMainAmplitude(), getMainFrequency());
        setListener(battleLogButton, getMainAmplitude(), getMainFrequency());
        setListener(trophiesButton, getMainAmplitude(), getMainFrequency());
        setListener(starsButton, getMainAmplitude(), getMainFrequency());
        setListener(leagueImage, getMainAmplitude(), LEAGUE_IMAGE_FREQUENCY);
        setListener(usernameButton, getMainAmplitude(), getMainFrequency());
        setListener(practiceButton, getMainAmplitude(), getMainFrequency());
        setListener(mysteryButton, getMainAmplitude(), getMainFrequency());

        setLeague();
    }

    private void updateUserStatusOnFirebase() {
        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(boolean.class);
                        if (connected) {
                            String userId = Account.getInstance(getApplicationContext())
                                    .getUserId();
                            changeOnlineStatus(userId, ONLINE);

                            // On user disconnection, update Firebase
                            changeToOfflineOnDisconnect(userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        throw error.toException();
                    }
                });
    }

    private void addListenerForFriendsRequests() {
        Database.getReference(format("users.%s.friends", Account.getInstance(this).getUserId()))
                .addValueEventListener(listenerFriendsRequest);

    }

    @VisibleForTesting
    public Dialog getFriendRequestWindow() {
        return friendRequestWindow;
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
                            // Update Firebase, delete the account instance and launch MainActivity
                            changeOnlineStatus(
                                    Account.getInstance(getApplicationContext()).getUserId(),
                                    OFFLINE)
                                    .addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Account.deleteAccount();
                                                    toastSignOut.cancel();
                                                    launchActivity(MainActivity.class);
                                                    overridePendingTransition(R.anim.fade_in,
                                                            R.anim.fade_out);
                                                    finish();
                                                }
                                            });
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
                launchOnlineGame((ImageView) view, R.drawable.draw_button, 0);
                break;
            case R.id.leaderboardButton:
                launchActivity(LeaderboardActivity.class);
                break;
            case R.id.shopButton:
                launchActivity(ShopActivity.class);
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
                launchActivity(DrawingOffline.class);
                break;
            case R.id.mysteryButton:
                launchOnlineGame((ImageView) view, R.drawable.home_mystery_button, 1);
                break;
            default:
        }
    }

    private void launchOnlineGame(ImageView view, int resourceId, int gameMode) {
        if (CheckConnection.isOnline(this)) {
            view.setImageResource(resourceId);
            Intent intent = new Intent(this, LoadingScreenActivity.class);
            intent.putExtra("mode", gameMode);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_internet,
                    Toast.LENGTH_LONG).show();
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

    @Override
    public void onResume() {
        super.onResume();

        Account account = Account.getInstance(this);

        // Update values of the text views
        ((TextView) findViewById(R.id.starsCount)).setText(String.valueOf(account.getStars()));
        ((TextView) findViewById(R.id.trophiesCount)).setText(String.valueOf(
                account.getTrophies()));
    }
}
