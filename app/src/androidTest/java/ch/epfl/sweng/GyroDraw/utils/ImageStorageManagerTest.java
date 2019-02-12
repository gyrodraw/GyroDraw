package ch.epfl.sweng.GyroDraw.utils;

import static ch.epfl.sweng.GyroDraw.game.drawing.DrawingOnlineActivityTest.initializedBitmap;
import static ch.epfl.sweng.GyroDraw.utils.ImageStorageManager.writeFileToStorage;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import ch.epfl.sweng.GyroDraw.localDatabase.LocalDbHandlerForImages;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;

public class ImageStorageManagerTest {

    @Rule
    public GrantPermissionRule writeExternalStoragePermission =
            GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule readExternalStoragePermission =
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void testIsPermissionsGranted() {
        assertThat(ImageStorageManager.hasExternalWritePermissions(
                InstrumentationRegistry.getContext()), is(true));
    }

    @Test
    public void testGetFile() {
        String root = Environment.getExternalStorageDirectory().toString() + "/Gyrodraw/";
        String fileName = root + "Image-testFile.png";

        File file = ImageStorageManager.getFile("testFile");
        writeFileToStorage(initializedBitmap(), file);
        assertThat(file.getPath(), is(equalTo(fileName)));
    }

    @Test
    public void testSaveImage() {
        ImageStorageManager.saveImage(InstrumentationRegistry.getContext(),
                Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888));
        LocalDbHandlerForImages dbHandler = new LocalDbHandlerForImages(
                InstrumentationRegistry.getContext(), null, 1);
        assertThat(dbHandler.getLatestBitmap(), is(notNullValue()));
    }
}
