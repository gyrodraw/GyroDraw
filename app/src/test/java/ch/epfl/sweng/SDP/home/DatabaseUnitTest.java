package ch.epfl.sweng.SDP.home;

import org.junit.Test;

import ch.epfl.sweng.SDP.firebase.database.Database;
import ch.epfl.sweng.SDP.firebase.database.DatabaseReferenceBuilder;

public class DatabaseUnitTest {

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalArgumenr() {
        new DatabaseReferenceBuilder(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddChildIllegalArgument() {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder();
        builder.addChild(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddChildrenIllegalArgument() {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder();
        builder.addChildren(null);
    }
}
