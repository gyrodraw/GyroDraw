package ch.epfl.sweng.SDP;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class StorageHandlingTestView extends AppCompatActivity {

    LocalDbHandler localDbHandler;
    LocalDbHandler localDbHandler2;
    FbStorageHandler fbStorageHandler;
    private Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    private Canvas canvas = new Canvas(bitmap);
    private Paint paint = new Paint();
    private Path path = new Path();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storagehandlingtest);
        localDbHandler = new LocalDbHandler(StorageHandlingTestView.this, null, 1);
        fbStorageHandler = new FbStorageHandler();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);

        path.lineTo(50, 50);
        canvas.drawPath(path, paint);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    /**
     * Tests case where we want to get from empty database.
     * @param view button
     */
    public void testGetFromEmptyDb(View view) {
        localDbHandler.getLatestBitmapFromDb();
    }

    /**
     * Tests adding and getting from local db.
     * @param view button
     */
    public void testAddAndRetrieveSuccessfully(View view){
        localDbHandler.addBitmapToDb(bitmap, new ByteArrayOutputStream());
        localDbHandler.getLatestBitmapFromDb();
    }

    /**
     * Tests getting element with null reference from storage.
     * @param view button
     */
    public void testGetNullFromStorage(View view) {
        fbStorageHandler.getBitmapFromFireBaseStorageReference(null);
    }

    /**
     * Tests putting and getting from storage.
     * @param view button
     */
    public void testPutAndGetFromStorage(View view) {
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        // Create a reference to "mountains.jpg"
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(""+ts+".jpg");
        fbStorageHandler.sendBitmapToFireBaseStorage(bitmap,imageRef);
        fbStorageHandler.getBitmapFromFireBaseStorageReference(imageRef);
    }

    /**
     * Tests if new table is created when newer version is available.
     * @param view button
     */
    public void testOnUpdate(View view){
        localDbHandler2 = new LocalDbHandler(StorageHandlingTestView.this,null, 2);
    }

}
