package ch.epfl.sweng.SDP.firebase;

import android.support.test.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import ch.epfl.sweng.SDP.firebase.database.Database;
import ch.epfl.sweng.SDP.firebase.database.DatabaseReferenceBuilder;
import ch.epfl.sweng.SDP.firebase.database.RealDatabase;

public class DatabaseTest {

    @Test(expected = IllegalArgumentException.class)
    public void getReferenceWithNullStringShouldFail() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = RealDatabase.getInstance();
        database.getReference(null);
    }

    @Test
    public void getReferenceWithSingleKeyReturnsValidReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = RealDatabase.getInstance();
        DatabaseReference ref = database.getReference("test");
        assertThat(ref.getKey(), is("test"));
    }

    @Test
    public void getReferenceWithMultipleKeysReturnsValidReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = RealDatabase.getInstance();
        DatabaseReference ref = database.getReference("test.tests");
        assertThat(ref.getKey(), is("tests"));
        assertThat(ref.getParent().getKey(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDatabaseReferenceBuilderWithNullReference() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        new DatabaseReferenceBuilder(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullChild() {
        DatabaseReferenceBuilder databaseReferenceBuilder = init();
        databaseReferenceBuilder.addChild(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullChildren() {
        DatabaseReferenceBuilder databaseReferenceBuilder = init();
        databaseReferenceBuilder.addChildren(null);
    }

    private DatabaseReferenceBuilder init() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());
        Database database = RealDatabase.getInstance();
        DatabaseReference ref = database.getReference("test.tests");
        return new DatabaseReferenceBuilder(ref);
    }
}
