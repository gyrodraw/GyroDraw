package ch.epfl.sweng.SDP.auth;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.epfl.sweng.SDP.firebase.FbDatabase;
import ch.epfl.sweng.SDP.firebase.OnSuccessValueEventListener;
import ch.epfl.sweng.SDP.home.leagues.League;
import ch.epfl.sweng.SDP.localDatabase.LocalDbForAccount;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;
import ch.epfl.sweng.SDP.shop.ShopItem;

import static ch.epfl.sweng.SDP.firebase.AccountAttributes.AVERAGE_RATING;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.LEAGUE;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.MATCHES_TOTAL;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.MATCHES_WON;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.MAX_TROPHIES;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.STARS;
import static ch.epfl.sweng.SDP.firebase.AccountAttributes.TROPHIES;
import static ch.epfl.sweng.SDP.home.FriendsRequestState.FRIENDS;
import static ch.epfl.sweng.SDP.home.FriendsRequestState.RECEIVED;
import static ch.epfl.sweng.SDP.home.FriendsRequestState.SENT;
import static ch.epfl.sweng.SDP.utils.LayoutUtils.LEAGUES;
import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Singleton class that represents an account.
 */
public class Account {

    private static Account instance = null;

    private String userId;
    private String username;
    private String email;
    private String currentLeague;
    private int trophies;
    private int stars;
    private int matchesWon;
    private int totalMatches;
    private double averageRating;
    private int maxTrophies;
    private List<ShopItem> itemsBought;

    private LocalDbForAccount localDbHandler;

    private Account(Context context, ConstantsWrapper constantsWrapper, String username,
                    String email, String currentLeague,
                    int trophies, int stars, int matchesWon, int totalMatches, double averageRating,
                    int maxTrophies, List<ShopItem> itemsBought) {

        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }

        this.localDbHandler = new LocalDbHandlerForAccount(context, null, 1);
        this.userId = constantsWrapper.getFirebaseUserId();
        this.username = username;
        this.email = email;
        this.currentLeague = currentLeague;
        this.trophies = trophies;
        this.stars = stars;
        this.matchesWon = matchesWon;
        this.totalMatches = totalMatches;
        this.averageRating = averageRating;
        this.maxTrophies = maxTrophies;
        this.itemsBought = new LinkedList<>(itemsBought);
        sortItemsBought();
    }

    private void sortItemsBought() {
        Collections.sort(itemsBought, ShopItem.getComparator());
    }

    /**
     * Creates an account instance. Trophies, stars and statistics are initialized to 0.
     *
     * @param context          the context in which the method is called
     * @param constantsWrapper the {@link ConstantsWrapper} instance necessary for building the
     *                         instance
     * @param username         the string defining the preferred username
     * @param email            the string defining the user email
     * @throws IllegalArgumentException if one of the parameters is null or invalid
     * @throws IllegalStateException    if the account was already instantiated
     */
    public static void createAccount(Context context, ConstantsWrapper constantsWrapper,
                                     String username, String email) {
        createAccount(context, constantsWrapper, username, email, LEAGUES[0].getName(), 0,
                0, 0, 0, 0.0, 0, new ArrayList<ShopItem>());
    }

    /**
     * Creates an account instance given the specified parameters.
     *
     * @param context          the context in which the method is called
     * @param constantsWrapper the {@link ConstantsWrapper} instance necessary for building the
     *                         instance
     * @param username         the string defining the preferred username
     * @param email            the string defining the user's email
     * @param currentLeague    the string defining the user's current league
     * @param trophies         the string defining the user's trophies
     * @param stars            the string defining the user's stars
     * @param matchesWon       the string defining the user's matches won
     * @param totalMatches     the string defining the user's total matches played
     * @param averageRating    the string defining the user's average rating
     * @param maxTrophies      the string defining the user's max trophies achieved
     * @throws IllegalArgumentException if one of the parameters is null or invalid
     * @throws IllegalStateException    if the account was already instantiated
     */
    public static void createAccount(Context context, ConstantsWrapper constantsWrapper,
                                     String username, String email, String currentLeague,
                                     int trophies, int stars, int matchesWon, int totalMatches,
                                     double averageRating, int maxTrophies,
                                     List<ShopItem> itemsBought) {
        checkPrecondition(context != null, "context is null");
        checkPrecondition(constantsWrapper != null, "constantsWrapper is null");
        checkPrecondition(username != null, "username is null");
        checkPrecondition(email != null, "email is null");
        checkPrecondition(currentLeague != null, "currentLeague is null");
        checkPrecondition(trophies >= 0, "trophies is negative");
        checkPrecondition(stars >= 0, "stars is negative");
        checkPrecondition(matchesWon >= 0, "matchesWon is negative");
        checkPrecondition(totalMatches >= 0, "totalMatches is negative");
        checkPrecondition(averageRating >= 0.0, "averageRating is negative");
        checkPrecondition(maxTrophies >= 0, "maxTrophies is negative");
        checkPrecondition(itemsBought != null, "itemsBought are null");

        instance = new Account(context, constantsWrapper, username, email, currentLeague,
                trophies,
                stars, matchesWon, totalMatches, averageRating, maxTrophies, itemsBought);
    }

    /**
     * Gets the account instance.
     *
     * @param context context calling this method
     * @return the account instance
     */
    public static Account getInstance(Context context) {
        if (instance == null) {
            ConstantsWrapper constantsWrapper = new ConstantsWrapper();
            createAccount(context, constantsWrapper,
                    constantsWrapper.getFirebaseUserId(), "");
        }

        return instance;
    }

    public static void deleteAccount() {
        instance = null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentLeague() {
        return currentLeague;
    }

    public void setCurrentLeague(String currentLeague) {
        this.currentLeague = currentLeague;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getMaxTrophies() {
        return maxTrophies;
    }

    public void setMaxTrophies(int maxTrophies) {
        this.maxTrophies = maxTrophies;
    }

    /**
     * Registers this account in Firebase and in the local database.
     */
    void registerAccount() {
        FbDatabase.saveAccount(this);
        localDbHandler.saveAccount(this);
    }

    /**
     * Method that allows one to change trophies.
     *
     * @param change modifier of trophies
     */
    public void changeTrophies(final int change) {
        trophies = Math.max(0, trophies + change);

        FbDatabase.setAccountAttribute(userId, TROPHIES, trophies);

        updateCurrentLeague();

        if (trophies > maxTrophies) {
            FbDatabase.setAccountAttribute(userId, MAX_TROPHIES, trophies);
            maxTrophies = trophies;
        }

        localDbHandler.saveAccount(instance);
    }

    private void updateCurrentLeague() {
        for (League league : LEAGUES) {
            if (league.contains(trophies)) {
                currentLeague = league.getName();
            }
        }

        FbDatabase.setAccountAttribute(userId, LEAGUE, currentLeague);
    }

    /**
     * Adds a recently bought item to the account.
     *
     * @param shopItem {@link ShopItem} that should be added to the account
     */
    public void updateItemsBought(ShopItem shopItem) {
        checkPrecondition(shopItem != null, "Shop item is null");

        FbDatabase.setShopItemValue(userId, shopItem);

        itemsBought.add(shopItem);
        sortItemsBought();

        localDbHandler.saveAccount(instance);
    }

    public List<ShopItem> getItemsBought() {
        return new LinkedList<>(itemsBought);
    }

    /**
     * Method that allows one to add currency (stars), both positive or negative.
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException in case the balance becomes negative
     */
    public void changeStars(final int amount) {
        int newStars = amount + stars;
        checkPrecondition(newStars >= 0, "Negative Balance");

        stars = newStars;

        FbDatabase.setAccountAttribute(userId, STARS, stars);
        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to increase the number of matches won.
     */
    public void increaseMatchesWon() {
        FbDatabase.setAccountAttribute(userId, MATCHES_WON, ++matchesWon);

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to increase the total number of matches.
     */
    public void increaseTotalMatches() {
        FbDatabase.setAccountAttribute(userId, MATCHES_TOTAL, ++totalMatches);

        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to change the average rating per game given a new rating. The rating
     * passed as parameter should be the total rating obtained after a match.
     *
     * @throws IllegalArgumentException in case a rating < 0 or > 20 is given or the user total
     *                                  number of matches is less than 1
     */
    public void changeAverageRating(double rating) {
        checkPrecondition(0 <= rating && rating <= 20, "Wrong rating given");
        checkPrecondition(totalMatches >= 1, "Wrong total matches");

        averageRating = (averageRating * (totalMatches - 1) + rating) / totalMatches;

        FbDatabase.setAccountAttribute(userId, AVERAGE_RATING, averageRating);
        localDbHandler.saveAccount(instance);
    }

    /**
     * Method that allows one to add friends.
     *
     * @param friendId String specifying FirebaseUser.UID of friend
     * @throws IllegalArgumentException in case the given friendId is null
     */
    public void addFriend(final String friendId) {
        checkPrecondition(friendId != null, "Friend's friendId is null");

        FbDatabase.getFriend(userId, friendId, new OnSuccessValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue(int.class)
                            == RECEIVED.ordinal()) {
                        updateFriendship(friendId, FRIENDS.ordinal(), FRIENDS.ordinal());
                    } else {
                        updateFriendship(friendId, SENT.ordinal(), RECEIVED.ordinal());
                    }
                } else {
                    updateFriendship(friendId, SENT.ordinal(), RECEIVED.ordinal());
                }
            }
        });
    }

    /**
     * Updates current users and friends friendship-state.
     *
     * @param friendId    id of friend
     * @param stateUser   state that current user will save
     * @param stateFriend state that friend will save
     */
    private void updateFriendship(String friendId, int stateUser, int stateFriend) {
        assert friendId != null : "friendId is null";
        assert 0 <= stateUser && stateUser <= 2 : "Wrong stateUser given";
        assert 0 <= stateFriend && stateFriend <= 2 : "Wrong stateUser given";

        // Update the user's friends' list
        FbDatabase.setFriendValue(userId, friendId, stateUser);

        // Update the sender's friends' list
        FbDatabase.setFriendValue(friendId, userId, stateFriend);
    }

    /**
     * Method that allows one to remove friends.
     *
     * @param friendId String specifying FirebaseUser.UID of friend
     * @throws IllegalArgumentException in case the given friendId is null
     */
    public void removeFriend(final String friendId) {
        checkPrecondition(friendId != null, "Friend's id is null");

        FbDatabase.removeFriend(userId, friendId);
        FbDatabase.removeFriend(friendId, userId);
    }
}
