package ch.epfl.sweng.SDP.firebase.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Singleton wrapper enum over {@link FirebaseDatabase}.
 */
public abstract class Database {

    private static Database instance;

    protected Database() {}

    protected static Database getInstance(Database newInstance) {
        if(instance != null) {
            return instance;
        } else {
            instance = newInstance;
            return instance;
        }
    }

    public abstract <V> void setValue(String path, V newValue);

    public abstract <V> void setValueSynchronous(String path, V newValue, final Runnable onSuccess, final Runnable onFailure);

    public abstract void containsValue(String path, Runnable onTrue, Runnable onFalse);

    public abstract void removeValue(String path);

    public abstract DatabaseReference getReference(String path);

}
