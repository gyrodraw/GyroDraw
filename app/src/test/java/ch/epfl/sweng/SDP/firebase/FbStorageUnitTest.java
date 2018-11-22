package ch.epfl.sweng.SDP.firebase;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FbStorageUnitTest {

    StorageReference mockReference;
    UploadTask mockUploadTask;
    StorageTask mockStorageTask;
    Bitmap mockBitmap;

    @Before
    public void init(){
        mockReference = Mockito.mock(StorageReference.class);
        mockUploadTask = Mockito.mock(UploadTask.class);
        mockBitmap = Mockito.mock(Bitmap.class);
        mockStorageTask = Mockito.mock(StorageTask.class);
    }

    @Test
    public void testSendBitmapToStorage(){
        when(mockReference.putBytes(isA(byte[].class))).thenReturn(mockUploadTask);
        when(mockUploadTask.addOnFailureListener(isA(OnFailureListener.class)))
                .thenReturn(mockStorageTask);
        when(mockBitmap.compress(isA(Bitmap.CompressFormat.class),
                isA(Integer.class), isA(ByteArrayOutputStream.class))).thenReturn(true);
        FbStorage.sendBitmapToFirebaseStorage(mockBitmap, mockReference);
    }
}
