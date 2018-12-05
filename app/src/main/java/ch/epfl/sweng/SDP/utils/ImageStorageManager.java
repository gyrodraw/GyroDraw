package ch.epfl.sweng.SDP.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class ImageStorageManager {

    /**
     * Saves an image to the device file system.
     * @param image the image to save.
     * @param image_name the filename of the image.
     * @param context the context.
     */
    public static void saveImage(Bitmap image, String image_name, Context context) {

        // Get image dirctory
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ "/Camera/Gyrodraw";
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".png";
        File file = new File(myDir, fname);
        Log.d("ImageStorageManager",  "Saving image: " + root + fname);

        if (file.exists()) file.delete();

        // Save image in file directory
        try {
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

         MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
    }

}
