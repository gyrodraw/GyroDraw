package ch.epfl.sweng.SDP.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Helper class to upload and download images from Firebase Storage.
 */
public class FbStorage {

    private static final String TAG = "fbStorage";
    private static final int QUALITY = 20;

    /**
     * Hides the public constructor.
     */
    private FbStorage(){}

    /**
     * Uploads a given bitmap to Firebase Storage at given StorageReference.
     * @param bitmap the image to upload
     * @param imageRef the name of the image
     */
    public static void sendBitmapToFireBaseStorage(
            final Bitmap bitmap, final StorageReference imageRef){
        if(bitmap != null && imageRef != null) {
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,
                    QUALITY, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(data);
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "Upload to Firebase Storage failed.");
                }
            });
        }
    }

    /**
     * Retrieve image from Firebase Storage.
     * @param reference the name of the image
     * @return the image bitmap
     */
    public static Bitmap getBitmapFromFireBaseStorageReference(
            StorageReference reference, OnSuccessListener onSuccessListener,
            OnFailureListener onFailureListener){
        if(reference == null){
            return null;
        } else {
            final long oneMegabyte = 1024 * 1024;
            final Bitmap[] bitmap = new Bitmap[1];
            reference.getBytes(oneMegabyte)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
}
