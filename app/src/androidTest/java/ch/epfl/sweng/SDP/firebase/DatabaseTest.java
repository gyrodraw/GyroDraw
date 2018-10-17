package ch.epfl.sweng.SDP.firebase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.support.test.InstrumentationRegistry;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import org.junit.Test;

public class DatabaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void getReferenceWithNullStringShouldFail() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = Database.getInstance();
        database.getReference(null);
    }

    @Test
    public void getReferenceWithSingleKeyReturnsValidReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = Database.getInstance();
        DatabaseReference ref = database.getReference("test");
        assertThat(ref.getKey(), is("test"));
    }

    @Test
    public void getReferenceWithMultipleKeysReturnsValidReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = Database.getInstance();
        DatabaseReference ref = database.getReference("test.tests");
        assertThat(ref.getKey(), is("tests"));
        assertThat(ref.getParent().getKey(), is("test"));
    }

    @Test
    public void getInstanceShouldAlwaysReturnTheSameInstance() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = Database.getInstance();
        assertThat(Database.getInstance(), is(database));
    }


}
