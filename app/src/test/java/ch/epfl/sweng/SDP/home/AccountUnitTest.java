package ch.epfl.sweng.SDP.home;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.EventListener;

import static java.lang.Math.toIntExact;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AccountUnitTest {

    ConstantsWrapper mockConstantsWrapper;
    DatabaseReference mockReference;
    Query mockQuery;
    Account account;

    @Before
    public void init() {
        mockConstantsWrapper = Mockito.mock(ConstantsWrapper.class);
        mockReference = Mockito.mock(DatabaseReference.class);
        mockQuery = Mockito.mock(Query.class);
        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn("123456789");
        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.orderByChild(isA(String.class))).thenReturn(mockQuery);
        when(mockQuery.equalTo(isA(String.class))).thenReturn(mockQuery);
        doNothing().when(mockReference).setValue(isA(Integer.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference).setValue(isA(Boolean.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference).setValue(isA(String.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference).removeValue(isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockQuery).addListenerForSingleValueEvent(isA(ValueEventListener.class));

        account = new Account(mockConstantsWrapper, "123456789");
        account.setUserId("123456789");
    }

    @Test
    public void testAccountValuesCorrect() {
        assertEquals(account.getTrophies(), 0);
        assertEquals(account.getStars(), 0);
        assertEquals(account.getMatchesLost(), 0);
        assertEquals(account.getMatchesWon(), 0);
        assertEquals(account.getAverageRating(), 0,0);
        assertEquals(account.getUsername(), "123456789");
        assertEquals(mockConstantsWrapper.getFirebaseUserId(), "123456789");
    }

    @Test
    public void testGetStars() {
        assertThat(account.getStars(), is(0));
    }

    @Test
    public void testGetUsername() {
        assertThat(account.getUsername(), is("123456789"));
    }

    @Test
    public void testGetTrophies() {
        assertThat(account.getTrophies(), is(0));
    }

    public void testGetMatchesWon() {
        assertThat(account.getMatchesWon(), is(0));
    }

    public void testGetMatchesLost() {
        assertThat(account.getMatchesLost(), is(0));
    }

    public void testGetAverageRating() {
        assertThat(account.getAverageRating(), is(0.0));
    }

    @Test
    public void testChangeTrophies() {
        account.changeTrophies(20);
        assertEquals(account.getTrophies(), 20);
    }

    @Test
    public void testAddStars() {
        account.addStars(20);
        assertEquals(account.getStars(), 20);
    }

    @Test
    public void testAddFriend() {
        account.addFriend("EPFLien");
    }

    @Test
    public void testRemoveFriend() {
        account.removeFriend("EPFLien");
    }

    @Test
    public void testUpdateUsername() {
        account.updateUsername("987654321");
        assertEquals(account.getUsername(), "987654321");
    }

    @Test
    public void testRegisterAccount() {
        account.registerAccount();
    }

    @Test
    public void testDownloadUser() {
        account.downloadUser();
    }

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

        account.setValues(map);
    }

    @Test
    public void testEventListener() {
        EventListener listener = new EventListener();
        DataSnapshot snap = Mockito.mock(DataSnapshot.class);
        HashMap<String, Object> map = new HashMap<>();

        map.put("username", "fred");
        map.put("id", "fred");

        map.put("stars", (long) 1.0);
        map.put("trophies", (long)1.0);
        map.put("matchesWon", (long)1.0);
        map.put("matchesLost",(long) 1.0);
        map.put("averageRating", (long)1.0);

    }

    @Test
    public void testCheckIfUsernameTaken() {
        account.checkIfAccountNameIsFree("123456789");
        assertEquals(account.getUsername(), "123456789");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUsername() {
        account.updateUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNegativeBalanceStars() {
        account.addStars(-1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullFriend() {
        account.addFriend(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullFriend() {
        account.removeFriend(null);
    }

}
