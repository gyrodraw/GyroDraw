package ch.epfl.sweng.SDP.firebase.database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForAccount;

public class RealDatabase extends Database {

    public static Database getInstance() {
        return Database.getInstance(new RealDatabase());
    }

    @Override
    public <V> void setValue(String path, V newValue) {
        DatabaseReferenceBuilder builder = (new DatabaseReferenceBuilder()).addChildren(path);
        builder.build().setValue(newValue);
    }

    @Override
    public <V> void setValueSynchronous(String path, V newValue, OnSuccessListener listener) {
        DatabaseReferenceBuilder builder = (new DatabaseReferenceBuilder()).addChildren(path);
        builder.build().setValue(newValue).addOnSuccessListener(listener);
    }

    @Override
    public void containsValue(String path, ValueEventListener listener) {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder();
        builder.addChildren(path);
        builder.build().addListenerForSingleValueEvent(listener);
    }

    @Override
    public void removeValue(String path) {
        DatabaseReferenceBuilder builder = (new DatabaseReferenceBuilder()).addChildren(path);
        builder.build().removeValue();
    }

    @Override
    public DatabaseReference getReference(String path) {
        return (new DatabaseReferenceBuilder()).addChildren(path).build();
    }

    @Override
    public void cache(Account account, LocalDbHandlerForAccount cache) {
        cache.saveAccount(account);
    }
}
