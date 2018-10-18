package ch.epfl.sweng.SDP.home;

import org.junit.Test;

import ch.epfl.sweng.SDP.Account;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountUnitTest {

    Account account = new Account();

    @Test
    public void testCreateAccount(){
        new Account("testName");
    }

    @Test
    public void testGetStars(){
        assertThat(account.getStars(), is(0));
    }

    @Test
    public void testGetUsername(){
        assertThat(account.getUsername(), is("standardName"));
    }

    @Test
    public void testGetTrophies(){
        assertThat(account.getTrophies(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUsername(){
        account.updateUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeBalanceStars(){
        account.addStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullFriend(){
        account.addFriend(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullFriend(){
        account.removeFriend(null);
    }

}
