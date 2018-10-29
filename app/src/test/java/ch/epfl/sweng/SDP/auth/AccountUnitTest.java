package ch.epfl.sweng.SDP.auth;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class AccountUnitTest {

    private ConstantsWrapper mockConstantsWrapper;
    private Context mockContext;
    private Account mockAccount;
    private DatabaseReference mockReference;
    private Query mockQuery;

    @Before
    public void init() {
        mockConstantsWrapper = mock(ConstantsWrapper.class);
        mockContext = mock(Context.class);
        mockAccount = mock(Account.class);
        mockReference = mock(DatabaseReference.class);
        mockQuery = mock(Query.class);

        when(mockConstantsWrapper.getReference(isA(String.class))).thenReturn(mockReference);
        when(mockConstantsWrapper.getFirebaseUserId()).thenReturn("123456789");

        when(mockReference.child(isA(String.class))).thenReturn(mockReference);
        when(mockReference.orderByChild(isA(String.class))).thenReturn(mockQuery);

        when(mockQuery.equalTo(isA(String.class))).thenReturn(mockQuery);

        doNothing().when(mockReference).setValue(isA(Integer.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference).setValue(isA(Boolean.class), isA(DatabaseReference.CompletionListener.class));
        doNothing().when(mockReference).removeValue(isA(DatabaseReference.CompletionListener.class));

        doNothing().when(mockQuery).addListenerForSingleValueEvent(isA(ValueEventListener.class));

        doNothing().when(mockAccount).registerAccount();
        doNothing().when(mockAccount).updateUsername(isA(String.class));

        Account.createAccount(mockContext, mockConstantsWrapper, "123456789");
        Account.getInstance(mockContext).setUserId("123456789");
        Account.enableTesting();
    }

    @Test
    public void testAccountValuesCorrect() {
        assertThat(Account.getInstance(mockContext).getTrophies(), is(0));
        assertThat(Account.getInstance(mockContext).getStars(), is(0));
        assertThat(Account.getInstance(mockContext).getUsername(), is("123456789"));
        assertThat(mockConstantsWrapper.getFirebaseUserId(), is("123456789"));
    }

    @Test
    public void testGetStars() {
        assertThat(Account.getInstance(mockContext).getStars(), is(0));
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
        //assertEquals(account.getTrophies(), 20);
    }

    @Test
    public void testAddStars() {
        Account.getInstance(mockContext).changeStars(20);
        //assertEquals(account.getStars(), 20);
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
        mockAccount.updateUsername("987654321");
        Account.getInstance(mockContext).setUsername("987654321");
        assertThat(Account.getInstance(mockContext).getUsername(), is("987654321"));
    }

    @Test
    public void testRegisterAccount() {
        mockAccount.registerAccount();
    }

    @Test
    public void testCheckIfUsernameTaken() {
        Account.getInstance(mockContext).checkIfAccountNameIsFree("123456789");
        assertThat(Account.getInstance(mockContext).getUsername(), is("123456789"));
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
