package ch.epfl.sweng.SDP.firebase;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

/**
 * Helper class to upload and download images to/from Firebase Storage.
 */
public final class FbStorage {

    private static final String TAG = "fbStorage";
    private static final int QUALITY = 20;

    private FbStorage() {
    }

    /**
     * Uploads a given bitmap to Firebase Storage at given StorageReference.
     *
     * @param bitmap   the image to upload
     * @param imageRef the name of the image
     * @return the {@link StorageTask} in charge of the upload
     */
    public static StorageTask<TaskSnapshot> sendBitmapToFirebaseStorage(
            final Bitmap bitmap, final StorageReference imageRef,
            OnSuccessListener<UploadTask.TaskSnapshot> onSucessListener) {
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

        StorageTask<TaskSnapshot> task = uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Upload to Firebase Storage failed.");
            }
        });

        if (onSucessListener != null) {
            task.addOnSuccessListener(onSucessListener);
        }

        return task;
    }
}