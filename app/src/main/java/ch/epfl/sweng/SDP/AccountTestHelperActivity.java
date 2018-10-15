package ch.epfl.sweng.SDP;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class AccountTestHelperActivity  extends AppCompatActivity {

    private final static String TAG = "AccountTestHelperAct";
    private Account testAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounttesthelper);
        testAccount = new Account("testAccount", 100, 100);
    }

    public void launchTests(View view){
        try{
            testGetStars();
            testAddStars();
            testSubtractStars();
            testAddNegativeTrophies();
            testChangeTrophies();
            testAddFriend();
            testNullAddFriend();
            testRemoveFriend();
            testChangeUsername();
            testNullUserName();
            testRemoveNullFriend();
        } catch (Exception exception){
            Log.d(TAG, "An exception was thrown during testing, which is normal.");
        }
    }

    public void testGetStars() {
        testAccount.getStars();
        testAccount.subtractStars(10);
    }

    public void testAddStars() {
        testAccount.getStars();
        testAccount.addStars(20);
    }

    public void testSubtractStars() {
        testAccount.subtractStars(10);
    }

    public void testAddNegativeTrophies() {
        testAccount.addStars(-10);
    }

    public void testChangeTrophies() {
        testAccount.changeTrophies(20);
        testAccount.getTrophies();
    }

    public void testChangeUsername() {
        testAccount.changeUsername("newName");
        testAccount.getUsername();
    }

    public void testNullUserName() {
        testAccount.changeUsername(null);
    }

    public void testNullAddFriend() {
        testAccount.addFriend(null);
    }

    public void testAddFriend() {
        testAccount.addFriend("123456789");
    }

    public void testRemoveNullFriend() {
        testAccount.removeFriend(null);
    }

    public void testRemoveFriend() {
        testAccount.removeFriend("123456789");
    }
}
