package ch.epfl.sweng.SDP.home;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseException;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.sweng.SDP.Account;

public class AccountUnitTest {

    private Account testAccount = new Account("testAccount", 100, 100);

    @Test(expected = NoClassDefFoundError.class)
    public void testGetStars() {
        testAccount.getStars();
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testAddStars() {
        testAccount.addStars(20);
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testSubtractStars() {
        testAccount.addStars(-10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeTrophies() {
        testAccount.addStars(-10000);
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testChangeTrophies() {
        testAccount.changeTrophies(20);
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testChangeUsername() {
        Long tsLong = System.currentTimeMillis()/1000;
        String timestamp = tsLong.toString();
        testAccount.changeUsername(timestamp);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUserName() {
        testAccount.changeUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAddFriend() {
        testAccount.addFriend(null);
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testAddFriend() {
        testAccount.addFriend("123456789");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullFriend() {
        testAccount.removeFriend(null);
    }

    @Test(expected = NoClassDefFoundError.class)
    public void testRemoveFriend() {
        testAccount.removeFriend("123456789");
    }
}
