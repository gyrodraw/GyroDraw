package ch.epfl.sweng.SDP.firebase;

import org.junit.Test;

import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;

public class DatabaseReferenceBuilderUnitTest {

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalArgument() {
        Database.constructBuilder(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddChildIllegalArgument() {
        DatabaseReferenceBuilder builder = Database.constructBuilder();
        builder.addChild(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddChildrenIllegalArgument() {
        DatabaseReferenceBuilder builder = Database.constructBuilder();
        builder.addChildren(null);
    }
}
