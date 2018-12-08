package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.epfl.sweng.SDP.Activity;
import ch.epfl.sweng.SDP.R;

/**
 * This class is responsible for saving images to the device storage.
 */
public class ImageStorageManager {

    private ImageStorageManager() {
        // Empty constructor
    }

    /**
     * Saves an image to the device file system.
     * @param image the image to save.
     * @param imageName the filename of the image.
     * @param activity the activity.
     * @param context the context.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveImage(Bitmap image, String imageName, final Activity activity, final Context context) {

        File file = getFile(imageName);

        if (file.exists()) {
            file.delete();
        }

        writeFileToStorage(image,file);

        MediaScannerConnection.scanFile(context, new String[]{file.getPath()},
                new String[]{"image/png"}, null);

        if (activity != null) {
            successfullyDownloadedImageToast(activity);
        }
    }

    /**
     * Write an image to the storage.
     *
     * @param image the image to store.
     * @param file the file path.
     */
    private static void writeFileToStorage(Bitmap image, File file) {
        Log.d("ImageStorageManager",  "Saving image: " + file.getPath());
        // Save image in file directory
        try  (FileOutputStream out = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a file object from a string describing the directory.
     *
     * @param imageName the name of the image.
     * @return a file object to the directory.
     */
    @VisibleForTesting
    public static File getFile(String imageName) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString()+ "/Camera/Gyrodraw/";
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + imageName + ".png";
        return new File(myDir, fname);
    }

    /**
     * Creates a toast to show that image was successfully downloaded.
     *
     * @param activity  to show the toast on
     */
    @VisibleForTesting
    public static void successfullyDownloadedImageToast(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(activity.getApplicationContext(),
                        activity.getString(R.string.successfulImageDownload), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

}
