package ch.epfl.sweng.SDP.shop;

/**
 * Helper class facilitating getting and synchronizing data from the database.
 */
public class IntegerWrapper {

    private int wrappedInt;

    public IntegerWrapper(int intToWrap) {
        this.wrappedInt = intToWrap;
    }

    public void setInt(int newInt) {
        wrappedInt = newInt;
    }

    public int getInt() {
        return wrappedInt;
    }
}
