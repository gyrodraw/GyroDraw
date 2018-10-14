package ch.epfl.sweng.SDP;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static final int QUALITY = 20;
    private static final int MAX_TRIALS = 3;
    private int trials;

    public FBStorageHandler(){
        trials = 0;
    }

    public void sendBitmapToFireBaseStorage(final Bitmap bitmap, final StorageReference imageRef){
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
                    sendBitmapToFireBaseStorage(bitmap, imageRef);
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
        final long ONE_MEGABYTE = 1024 * 1024;
        final Bitmap[] bitmap = new Bitmap[1];
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                bitmap[0] = null; // Handle any errors
            }
        });
        return bitmap[0];
    }
}
