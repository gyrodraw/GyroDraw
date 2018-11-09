package ch.epfl.sweng.SDP.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;

import ch.epfl.sweng.SDP.firebase.user.FakeCurrentUser;
import ch.epfl.sweng.SDP.firebase.database.FakeDatabase;

@RunWith(JUnit4.class)
public class AccountUnitTest {

    private Context mockContext;

    @Before
    public void init() {
        mockContext = mock(Context.class);

        FakeDatabase database = (FakeDatabase) FakeDatabase.getInstance();
        database.setReference(mock(DatabaseReference.class));

        FakeCurrentUser user = (FakeCurrentUser) FakeCurrentUser.getInstance();

        Account.createAccount(mockContext, "123456789");
        Account.getInstance(mockContext).setUserId("123456789");
        Account.enableTesting();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateAccountWithNullUsername() {
        Account.createAccount(mockContext, null);
    }

    @Test
    public void testSetTrophies() {
        Account.getInstance(mockContext).setTrophies(1);
    }

    @Test
    public void testSetStars() {
        Account.getInstance(mockContext).setStars(1);
    }


    @Test
    public void testSetCurrentLeague() {
        Account.getInstance(mockContext).setCurrentLeague("test");
    }

    /*@Test
    public void testSetUsersRef() {
        DatabaseReference databaseReference = Mockito.mock(DatabaseReference.class);
        Account.getInstance(mockContext).setUsersRef(databaseReference);
    }*/

    @Test
    public void testAccountValuesCorrect() {
        assertThat(Account.getInstance(mockContext).getTrophies(), is(0));
        assertThat(Account.getInstance(mockContext).getStars(), is(0));
        assertThat(Account.getInstance(mockContext).getUsername(), is("123456789"));
    }

    @Test
    public void testCurrentLeague() {
        assertThat(Account.getInstance(mockContext).getCurrentLeague(), is("league1"));
    }

    @Test
    public void testGetStars() {
        assertThat(Account.getInstance(mockContext).getStars(),
                is(0));
    }

    @Test
    public void testGetUserId() {
        assertThat(Account.getInstance(mockContext).getUserId(), is("123456789"));
    }

    @Test
    public void testGetUsername() {
        assertThat(Account.getInstance(mockContext).getUsername(), is("123456789"));
    }

    @Test
    public void testGetTrophies() {
        assertThat(Account.getInstance(mockContext).getTrophies(), is(0));
    }

    @Test
    public void testChangeTrophies() {
        Account.getInstance(mockContext).changeTrophies(20);
        assertEquals(Account.getInstance(mockContext).getTrophies(), 20);
    }

    @Test
    public void testAddStars() {
        Account.getInstance(mockContext).changeStars(20);
        //assertEquals(account.getStars(), 20);
    }

    /*@Test
    public void testDownloadUser() {
        mockAccount.downloadUser();
    }*/

    @Test
    public void testSetValues() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", "fred");
        map.put("id", "fred");

        map.put("stars", (long) 1.0);
        map.put("trophies", (long)1.0);
        map.put("matchesWon", (long)1.0);
        map.put("matchesLost",(long) 1.0);
        map.put("averageRating", (long)1.0);

        Account.getInstance(mockContext).setValues(map);
    }

    @Test
    public void testAddFriend() {
        Account.getInstance(mockContext).addFriend("EPFLien");
    }

    @Test
    public void testRemoveFriend() {
        Account.getInstance(mockContext).removeFriend("EPFLien");
    }

    @Test
    public void testUpdateUsername() {
        Account.getInstance(mockContext).setUsername("987654321");
        assertThat(Account.getInstance(mockContext).getUsername(), is("987654321"));
    }

    @Test
    public void testRegisterAccount() {
        Account.getInstance(mockContext).registerAccount();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUsername() {
        Account.getInstance(mockContext).updateUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeBalanceStars() {
        Account.getInstance(mockContext).changeStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullFriend() {
        Account.getInstance(mockContext).addFriend(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullFriend() {
        Account.getInstance(mockContext).removeFriend(null);
    }
}
