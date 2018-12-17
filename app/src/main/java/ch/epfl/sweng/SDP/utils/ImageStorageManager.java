package ch.epfl.sweng.SDP.utils;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import ch.epfl.sweng.SDP.R;
import ch.epfl.sweng.SDP.auth.Account;
import ch.epfl.sweng.SDP.localDatabase.LocalDbForImages;
import ch.epfl.sweng.SDP.localDatabase.LocalDbHandlerForImages;
import java.io.File;
import java.io.FileOutputStream;

/**
 * This class is responsible for saving images to the device storage.
 */
public final class ImageStorageManager {

    private ImageStorageManager() {
        // Empty constructor
    }

    /**
     * Retrieves the latest image from the local database and saves it in local external storage.
     * @param context activity context
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void saveImage(Context context) {
        LocalDbForImages localDbHandler = new LocalDbHandlerForImages(context, null, 1);
        Account account = Account.getInstance(context);
        String imageName = account.getUsername() + account.getTotalMatches();
        ImageStorageManager.writeImage(localDbHandler.getLatestBitmapFromDb(), imageName, context);
    }

    /**
     * Saves an image to the device file system.
     *
     * @param image     the image to save.
     * @param imageName the filename of the image.
     * @param context  the activity.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void writeImage(Bitmap image, String imageName, final Context context) {
        File file = getFile(imageName);

        if (file.exists()) {
            file.delete();
        }

        writeFileToStorage(image, file);

        MediaScannerConnection.scanFile(context, new String[]{file.getPath()},
                new String[]{"image/png"}, null);

        if (context != null) {
            successfullyDownloadedImageToast(context);
        }
    }

    /**
     * Writes an image to the storage.
     *
     * @param image the image to store.
     * @param file  the file path.
     */
    static void writeFileToStorage(Bitmap image, File file) {
        Log.d("ImageStorageManager", "Saving image: " + file.getPath());

        // Save image in file directory
        try (FileOutputStream out = new FileOutputStream(file)) {
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
    static File getFile(String imageName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Gyrodraw");
        myDir.mkdirs();
        String fileName = "Image-" + imageName + ".png";
        return new File(myDir, fileName);
    }

    /**
     * Creates a toast to show that image was successfully downloaded.
     *
     * @param context to show the toast on
     */
    public static void successfullyDownloadedImageToast(final Context context) {
        Toast toast = Toast.makeText(context,
                context.getString(R.string.successfulImageDownload), Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Asks permissions for writing in external files.
     * @param context context of the application
     */
    public static void askForStoragePermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

    }

    /**
     * Checks if storage permissions are granted.
     * If permissions are revoked it requests permission.
     *
     * @param context activity context
     * @return a boolean indicating if permissions are granted or not.
     */
    public static boolean hasExternalWritePermissions(Context context) {
        return checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
