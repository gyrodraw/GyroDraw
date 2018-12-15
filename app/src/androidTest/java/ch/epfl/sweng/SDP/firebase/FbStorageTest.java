package ch.epfl.sweng.SDP.firebase;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.storage.StorageTask;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(AndroidJUnit4.class)
public class FbStorageTest {

    @Test
    public void testSendBitmapToStorage() {
        StorageTask task = FbStorage.sendBitmapToFirebaseStorage(Bitmap.createBitmap(2, 2,
                Bitmap.Config.ARGB_8888), "testImage");
        assertThat(task, is(notNullValue()));
    }
}
