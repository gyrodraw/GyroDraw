package ch.epfl.sweng.SDP.firebase;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
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
     * @param bitmap the image to upload
     * @param imageRef the name of the image
     * @return the {@link StorageTask} in charge of the upload
     */
    public static StorageTask<TaskSnapshot> sendBitmapToFirebaseStorage(
            final Bitmap bitmap, final StorageReference imageRef) {
        checkPrecondition(bitmap != null, "bitmap is null");
        checkPrecondition(imageRef != null, "imageRef is null");
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

        return uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Upload to Firebase Storage failed.");
            }
        });
    }
}