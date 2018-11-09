package ch.epfl.sweng.SDP.firebase.database;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

public class FakeDatabase extends Database {

    private Map<String, Object> database;
    private DatabaseReference reference;

    private FakeDatabase() {
        database = new HashMap<>();
    }

    public static Database getInstance() {
        return getInstance(new FakeDatabase());
    }

    @Override
    public <V> void setValue(String path, V newValue) {
        database.put(path, newValue);
    }

    @Override
    public <V> void setValueSynchronous(String path, V newValue, OnSuccessListener listener) {
        setValue(path, newValue);
    }

    @Override
    public void containsValue(String path, ValueEventListener listener) {
        listener.onDataChange((DataSnapshot)database.get(path));
    }

    @Override
    public void removeValue(String path) {
        database.remove(path);
    }

    @Override
    public DatabaseReference getReference(String path) {
        return reference;
    }

    public void setReference(DatabaseReference reference){
        this.reference = reference;
    }

    public void cache(Account account, LocalDbHandlerForAccount cache) {
        // Does nothing.
    }
}
