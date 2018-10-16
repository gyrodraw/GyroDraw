package ch.epfl.sweng.SDP;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class AccountTestHelperActivity  extends AppCompatActivity {

    private static final String TAG = "AccountTestHelperAct";
    private Account testAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounttesthelper);
        testAccount = new Account("testAccount", 100, 100);
    }

    public void testGetStars(View view) {
        testAccount.getStars();
        testAccount.subtractStars(10);
    }

    public void testAddStars(View view) {
        testAccount.getStars();
        testAccount.addStars(20);
    }

    public void testSubtractStars(View view) {
        testAccount.subtractStars(10);
    }

    public void testAddNegativeTrophies(View view) {
        testAccount.addStars(-10);
    }

    public void testChangeTrophies(View view) {
        testAccount.changeTrophies(20);
        testAccount.getTrophies();
    }

    public void testChangeUsername(View view) {
        testAccount.changeUsername("newName");
        testAccount.getUsername();
    }

    public void testNullUserName(View view) {
        testAccount.changeUsername(null);
    }

    public void testNullAddFriend(View view) {
        testAccount.addFriend(null);
    }

    public void testAddFriend(View view) {
        testAccount.addFriend("123456789");
    }

    public void testRemoveNullFriend(View view) {
        testAccount.removeFriend(null);
    }

    public void testRemoveFriend(View view) {
        testAccount.removeFriend("123456789");
    }
}
