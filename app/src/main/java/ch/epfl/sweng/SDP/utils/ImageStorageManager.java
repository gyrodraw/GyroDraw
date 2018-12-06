package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.epfl.sweng.SDP.Activity;

/**
 * This class is responsible for saving images to the device storage.
 */
public class ImageStorageManager {

    ImageStorageManager() {
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
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(context,
                            "Successfully saved image to /Camera/Gyrodraw", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

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

    private static File getFile(String imageName) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).toString()+ "/Camera/Gyrodraw/";
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + imageName + ".png";
        return new File(myDir, fname);
    }

}
