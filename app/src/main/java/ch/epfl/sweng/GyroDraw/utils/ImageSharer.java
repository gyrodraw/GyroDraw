package ch.epfl.sweng.GyroDraw.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import ch.epfl.sweng.GyroDraw.auth.Account;
import ch.epfl.sweng.GyroDraw.firebase.FbStorage;

/**
 * This class is responsible for sharing images to Facebook.
 */
public class ImageSharer {

    private static ImageSharer instance;
    private Context context;

    private ImageSharer(Context context) {
        this.context = context;
    }

    /**
     * Gets this ImageSharer instance. Use this method to initialize the singleton.
     *
     * @param context activity calling this method
     * @return ImageSharer instance
     */
    public static ImageSharer getInstance(Context context) {
        if (instance == null) {
            instance = new ImageSharer(context);
        }
        return instance;
    }

    /**
     * Gets the ImageSharer instance.
     */
    public static ImageSharer getInstance() {
        return instance;
    }

    /**
     * Sets the activity which uses the image sharer. This method should also be used to prevent
     * reference cycle by setting the activity to null when it's not used anymore.
     *
     * @param context the activity
     */
    public void setActivity(Context context) {
        this.context = context;
    }

    /**
     * Shares an image to Facebook by opening a share dialog.
     *
     * @param image the image to share
     */
    public void shareImageToFacebook(Bitmap image) {
        // Check if Facebook app is installed.
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            shareImageToFacebookApp(image);
        } else {
            // Facebook app not installed, use web instead.
            uploadImageToFireBase(image);
        }
    }

    /**
     * Shares an image to Facebook app.
     *
     * @param image the image to share
     * @return true if the {@link ShareDialog} was created, false otherwise
     */
    @VisibleForTesting
    public boolean shareImageToFacebookApp(Bitmap image) {
        SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();

        if (context != null) {
            ShareDialog.show((Activity) context, content);
            return true;
        }
        return false;
    }

    /**
     * Uploads the image to Firebase storage.
     *
     * @param image the image to upload
     */
    private void uploadImageToFireBase(Bitmap image) {
        Account account = Account.getInstance(context);
        String imageName = "DRAWING_" + account.getTotalMatches()
                + "_" + account.getUsername() + ".jpg";

        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageName);
        FbStorage.sendBitmapToFirebaseStorage(image, imageName,
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getUrlAndShare(ref);
                    }
                });
    }

    /**
     * Retrieves the image url of the storage reference.
     *
     * @param ref the storage reference.
     */
    @VisibleForTesting
    public void getUrlAndShare(StorageReference ref) {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                // Share image to facebook after getting url
                shareDrawingToFacebook(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("ERROR", "Error uploading task");
            }
        });
    }

    /**
     * Opens an activity to share the image to Facebook.
     */
    @VisibleForTesting
    public void shareDrawingToFacebook(Uri uri) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder().setContentUrl(uri).build();
        if (context != null) {
            ShareDialog.show((Activity) context, linkContent);
        }
    }

}
