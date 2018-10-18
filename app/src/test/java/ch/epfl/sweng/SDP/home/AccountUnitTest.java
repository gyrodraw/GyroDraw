package ch.epfl.sweng.SDP.home;

import org.junit.Test;

import ch.epfl.sweng.SDP.Account;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AccountUnitTest {

    @Test
    public void testGetStars(){
        Account account = createFakeAccount(null, 10, 0);
        assertThat(account.getStars(), is(10));
    }

    private static Account createFakeAccount(String username, final int stars, int trophies){
        return new Account(){
            @Override
            public int getStars() {
                return stars;
            }
        };
    }
}
