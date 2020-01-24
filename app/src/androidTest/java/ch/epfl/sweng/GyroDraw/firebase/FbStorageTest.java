package ch.epfl.sweng.GyroDraw.firebase;

import android.graphics.Bitmap;
import android.os.SystemClock;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public class FbStorageTest {

    private static final String TEST_IMAGE_NAME = "testImage";

    @Test
    public void testSendBitmapToStorage() {
        StorageTask task = FbStorage.sendBitmapToFirebaseStorage(Bitmap.createBitmap(2, 2,
                Bitmap.Config.ARGB_8888), TEST_IMAGE_NAME, null);
        assertThat(task, is(notNullValue()));
    }

    @Test
    public void testRemoveImage() {
        FbStorage.sendBitmapToFirebaseStorage(Bitmap.createBitmap(2, 2,
                Bitmap.Config.ARGB_8888), TEST_IMAGE_NAME, null);
        SystemClock.sleep(3000);

        FbStorage.removeImage(TEST_IMAGE_NAME);
        SystemClock.sleep(3000);

        final long eight_kb = 8192;
        Task<byte[]> bitmap = FirebaseStorage.getInstance().getReference().child(TEST_IMAGE_NAME)
                .getBytes(eight_kb);
        SystemClock.sleep(3000);

        assertThat(bitmap.isSuccessful(), is(false));
    }
}
