package ch.epfl.sweng.SDP.firebase;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
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
    private FbStorage() {
    }

    /**
     * Uploads a given bitmap to Firebase Storage at given StorageReference.
     *
     * @param bitmap   the image to upload
     * @param imageRef the name of the image
     */
    public static void sendBitmapToFireBaseStorage(
            final Bitmap bitmap, final StorageReference imageRef) {
        if (bitmap != null && imageRef != null) {
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
}