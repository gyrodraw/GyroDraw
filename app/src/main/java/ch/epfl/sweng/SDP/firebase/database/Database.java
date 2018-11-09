package ch.epfl.sweng.SDP.firebase.database;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

/**
 * Singleton wrapper enum over {@link FirebaseDatabase}.
 */
public abstract class Database {

    public static Database instance;

    protected static Database getInstance(Database newInstance) {
        if(instance != null) {
            return instance;
        } else {
            instance = newInstance;
            return instance;
        }
    }

    public abstract <V> void setValue(String path, V newValue);

    public abstract <V> void setValueSynchronous(String path, V newValue, OnSuccessListener listener);

    public abstract void containsValue(String path, ValueEventListener listener);

    public abstract void removeValue(String path);

    public abstract DatabaseReference getReference(String path);

    public abstract void cache(Account account, LocalDbHandlerForAccount cache);

}
