package ch.epfl.sweng.GyroDraw.firebase;

import android.support.test.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FbDatabaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void getReferenceWithNullStringShouldFail() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        FbDatabase.getReference(null);
    }

    @Test
    public void getReferenceWithSingleKeyReturnsValidReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        DatabaseReference ref = FbDatabase.getReference("test");
        assertThat(ref.getKey(), is("test"));
    }

    @Test
    public void getReferenceWithMultipleKeysReturnsValidReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        DatabaseReference ref = FbDatabase.getReference("test.tests");
        assertThat(ref.getKey(), is("tests"));
        assertThat(ref.getParent().getKey(), is("test"));
    }
}
