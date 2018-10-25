package ch.epfl.sweng.SDP.home;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;

import ch.epfl.sweng.SDP.Account;
import ch.epfl.sweng.SDP.ConstantsWrapper;
import ch.epfl.sweng.SDP.firebase.FbStorage;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
        FbStorage.sendBitmapToFireBaseStorage(mockBitmap, mockReference);
    }

    @Test
    public void testGetBitmapFromStorage(){
        Task<byte[]> mockTask = Mockito.mock(Task.class);
        when(mockReference.getBytes(isA(Long.class))).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(isA(OnSuccessListener.class)))
                .thenReturn(mockTask);
        when(mockTask.addOnFailureListener(isA(OnFailureListener.class)))
                .thenReturn(mockStorageTask);

        assertTrue(FbStorage.getBitmapFromFireBaseStorageReference(mockReference) == null);
    }

    @Test
    public void testGetBitmapFromNullStorage(){
        assertTrue(FbStorage.getBitmapFromFireBaseStorageReference(null)==null);
    }
}
