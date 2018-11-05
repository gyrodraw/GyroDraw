package ch.epfl.sweng.SDP.firebase.database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RealDatabase extends Database {

    public static Database getInstance() {
        return Database.getInstance(new RealDatabase());
    }

    public <V> void setValue(String path, V newValue) {
        DatabaseReferenceBuilder builder = (new DatabaseReferenceBuilder()).addChildren(path);
        builder.build().setValue(newValue);
    }

    public <V> void setValueSynchronous(String path, V newValue, final Runnable onSuccess, final Runnable onFailure) {
        DatabaseReferenceBuilder builder = (new DatabaseReferenceBuilder()).addChildren(path);
        builder.build().setValue(newValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onSuccess.run();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.run();
            }
        });
    }

    @Override
    public void containsValue(String path, final Runnable onTrue, final Runnable onFalse) {
        DatabaseReferenceBuilder builder = new DatabaseReferenceBuilder();
        builder.addChildren(path);
        builder.build().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if (data.exists()) {
                        onTrue.run();
                    } else {
                        onFalse.run();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
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


}
