package ch.epfl.sweng.SDP.home;

import org.junit.Test;

import ch.epfl.sweng.SDP.firebase.Database;

public class DatabaseUnitTest {

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalArgumenr() {
        new Database.DatabaseReferenceBuilder(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddChildIllegalArgument() {
        Database.DatabaseReferenceBuilder builder = new Database.DatabaseReferenceBuilder();
        builder.addChild(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddChildrenIllegalArgument() {
        Database.DatabaseReferenceBuilder builder = new Database.DatabaseReferenceBuilder();
        builder.addChildren(null);
    }


}
