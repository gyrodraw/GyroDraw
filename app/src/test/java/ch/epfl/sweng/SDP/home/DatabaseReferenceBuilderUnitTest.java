package ch.epfl.sweng.SDP.home;

import ch.epfl.sweng.SDP.firebase.Database.DatabaseReferenceBuilder;
import org.junit.Test;

public class DatabaseReferenceBuilderUnitTest {

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalArgument() {
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
