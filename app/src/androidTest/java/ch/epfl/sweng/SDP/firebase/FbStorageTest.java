package ch.epfl.sweng.SDP.firebase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FbStorageTest {

    private static final String TEST_IMAGE_NAME = "testImage";

    @Test
    public void testSendBitmapToStorage() {
        StorageTask task = FbStorage.sendBitmapToFirebaseStorage(Bitmap.createBitmap(2, 2,
                Bitmap.Config.ARGB_8888), TEST_IMAGE_NAME);
        assertThat(task, is(notNullValue()));
    }

    @Test
    public void testRemoveImage() {
        FbStorage.sendBitmapToFirebaseStorage(Bitmap.createBitmap(2, 2,
                Bitmap.Config.ARGB_8888), TEST_IMAGE_NAME);
        SystemClock.sleep(3000);

        FbStorage.removeImage(TEST_IMAGE_NAME);
        SystemClock.sleep(3000);

        final long EIGHT_KB = 8192;
        Task<byte[]> bitmap = FirebaseStorage.getInstance().getReference().child(TEST_IMAGE_NAME)
                .getBytes(EIGHT_KB);
        SystemClock.sleep(3000);

        assertThat(bitmap.isSuccessful(), is(false));
    }
}
