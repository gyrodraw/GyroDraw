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

    public void testAccountFunctions(View view){
        try{
            testGetStars();
        } catch(Exception e) {

        }
        try{
            testAddStars();
        } catch(Exception e) {

        }
        try{
            testSubtractStars();
        } catch (Exception e){

        }
        try{
            testChangeTrophies();
        } catch (Exception e){

        }
        try{
            testAddNegativeTrophies();
        } catch (Exception e){

        }
        try{
            testAddFriend();
        } catch (Exception e){

        }
        try{
            testRemoveFriend();
        } catch (Exception e){

        }
        try{
            testRemoveNullFriend();
        } catch (Exception e){

        }
        try{
            testNullAddFriend();
        } catch (Exception e){

        }
        try{
            testChangeUsername();
        } catch (Exception e){

        }
        try{
            testNullUserName();
        } catch (Exception e){

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
