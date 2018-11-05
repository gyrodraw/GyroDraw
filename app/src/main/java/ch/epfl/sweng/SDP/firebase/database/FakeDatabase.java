package ch.epfl.sweng.SDP.firebase.database;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class FakeDatabase extends Database {

    private Map<String, Object> database;
    private DatabaseReference reference;

    private FakeDatabase() {
        database = new HashMap<>();
    }

    public static Database getInstance() {
        return Database.getInstance(new FakeDatabase());
    }

    @Override
    public <V> void setValue(String path, V newValue) {
        database.put(path, newValue);
    }

    @Override
    public <V> void setValueSynchronous(String path, V newValue, Runnable onSuccess, Runnable onFailure) {
        setValue(path, newValue);
    }

    @Override
    public void containsValue(String path, Runnable onTrue, Runnable onFalse) {
        if(database.containsKey(path)) {
            onTrue.run();
        } else {
            onFalse.run();
        }
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
}
