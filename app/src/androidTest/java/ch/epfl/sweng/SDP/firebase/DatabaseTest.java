package ch.epfl.sweng.SDP.firebase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import android.support.test.InstrumentationRegistry;
import com.google.firebase.FirebaseApp;
import org.junit.Test;

public class DatabaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void getReferenceWithNullStringShouldFail() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = Database.getInstance();
        database.getReference(null);
    }

    @Test
    public void getInstanceShouldAlwaysReturnTheSameInstance() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = Database.getInstance();
        assertThat(Database.getInstance(), is(database));
    }
}
