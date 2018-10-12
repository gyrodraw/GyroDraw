package ch.epfl.sweng.SDP;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FBStorageHandler {

    private static final String TAG = "fbStorageHandler";
    private StorageReference mStorageRef;
    private static final int QUALITY = 20;
    private static final int MAX_TRIALS = 3;
    private int trials;

    public FBStorageHandler(){
        mStorageRef = FirebaseStorage.getInstance().getReference();
        trials = 0;
    }

    public void sendBitmapToFireBaseStorage(final Bitmap bitmap){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        // Create a reference to "mountains.jpg"
        StorageReference imageRef = mStorageRef.child(""+ts+".jpg");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if(trials > MAX_TRIALS){
                    Log.d(TAG, "Upload to Firebase Storage failed.");
                } else {
                    ++trials;
                    sendBitmapToFireBaseStorage(bitmap);
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public Bitmap getBitmapFromFireBaseStorageReference(StorageReference reference){
        return null;
    }
}
