package ch.epfl.sweng.SDP.firebase;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
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

    private static final String TAG = "FbStorage";
    private static final int QUALITY = 20;

    private static final StorageReference STORAGE_REF = FirebaseStorage.getInstance()
            .getReference();

    private FbStorage() {
    }

    /**
     * Uploads a given bitmap to Firebase Storage with the given name.
     *
     * @param bitmap    the image to upload
     * @param imageName the name of the image
     * @return the {@link StorageTask} in charge of the upload
     */
    public static StorageTask<TaskSnapshot> sendBitmapToFirebaseStorage(
            final Bitmap bitmap, final String imageName,
            OnSuccessListener<UploadTask.TaskSnapshot> successListener) {
        checkPrecondition(bitmap != null, "bitmap is null");
        checkPrecondition(imageName != null, "imageName is null");

        StorageReference imageRef = STORAGE_REF.child(imageName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, byteArrayOutputStream);

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

        if (successListener != null) {
            task.addOnSuccessListener(successListener);
        }

        return task;
    }

    /**
     * Removes from Firebase Storage the image corresponding to the given name. The image name has
     * to contain the extension (imageName.jpg, for example).
     *
     * @param imageName the name of the image
     */
    public static void removeImage(String imageName) {
        STORAGE_REF.child(imageName).delete();
    }
}
