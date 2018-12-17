package ch.epfl.sweng.SDP.firebase;

import static ch.epfl.sweng.SDP.utils.Preconditions.checkPrecondition;

import android.graphics.Bitmap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
     * @param successListener optional {@link OnSuccessListener} to add to the task
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

        if (successListener != null) {
            uploadTask.addOnSuccessListener(successListener);
        }

        return uploadTask;
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
