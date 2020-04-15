package pg.autyzm.przyjazneemocje;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import pg.autyzm.przyjazneemocje.lib.SqliteManager;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

/**
 * Created by Ann on 13.11.2016.
 */
public class CameraActivity extends Activity {
    RelativeLayout overlay;
    SurfaceView cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Bundle extras = getIntent().getExtras();
        String emotion = extras.getString("SpinnerValue_Emotion");*/

        //fileName = getFileName(emotion);
        // Optional: Hide the status bar at the top of the window
   /*     requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the content view and get references to our views
        setContentView(R.layout.square_camera);
        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        overlay = (RelativeLayout) findViewById(R.id.overlay);*/

        takePhoto();

    }

/*    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = cameraPreview.getMeasuredWidth(),
                previewHeight = cameraPreview.getMeasuredHeight();

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
    }*/

    private String fileName;
    private static final int TAKE_PHOTO_CODE = 1;

    public void takePhoto() {
       //final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final Intent intent = new Intent(CameraActivity.this, MainCameraActivity.class);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));
        startActivityForResult(intent, TAKE_PHOTO_CODE);
    }

    private File getTempFile() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        final File path = new File(root + "FriendlyEmotions/Photos" + File.separator);
        if (!path.exists()) {
            path.mkdir();
        }

        return new File(path, fileName + "tmp.jpg");
    }

    private String getFileName(String emotionLang) {
        SqliteManager sqlm = getInstance(this);
        Map<String, String> mapEmo = new ArrayMap<>();
        mapEmo.put(getResources().getString(R.string.emotion_happy_man), "happy_man");
        mapEmo.put(getResources().getString(R.string.emotion_sad_man), "sad_man");
        mapEmo.put(getResources().getString(R.string.emotion_angry_man), "angry_man");
        mapEmo.put(getResources().getString(R.string.emotion_scared_man), "scared_man");
        mapEmo.put(getResources().getString(R.string.emotion_surprised_man), "surprised_man");
        mapEmo.put(getResources().getString(R.string.emotion_bored_man), "bored_man");
        mapEmo.put(getResources().getString(R.string.emotion_happy_woman), "happy_woman");
        mapEmo.put(getResources().getString(R.string.emotion_sad_woman), "sad_woman");
        mapEmo.put(getResources().getString(R.string.emotion_angry_woman), "angry_woman");
        mapEmo.put(getResources().getString(R.string.emotion_scared_woman), "scared_woman");
        mapEmo.put(getResources().getString(R.string.emotion_surprised_woman), "surprised_woman");
        mapEmo.put(getResources().getString(R.string.emotion_bored_woman), "bored_woman");
        mapEmo.put(getResources().getString(R.string.emotion_happy_child), "happy_child");
        mapEmo.put(getResources().getString(R.string.emotion_sad_child), "sad_child");
        mapEmo.put(getResources().getString(R.string.emotion_angry_child), "angry_child");
        mapEmo.put(getResources().getString(R.string.emotion_scared_child), "scared_child");
        mapEmo.put(getResources().getString(R.string.emotion_surprised_child), "surprised_child");
        mapEmo.put(getResources().getString(R.string.emotion_bored_child), "bored_child");
        String emotionAndSex = mapEmo.get(emotionLang);
        Cursor cur = sqlm.givePhotosWithEmotionSource(emotionAndSex, SqliteManager.Source.EXTERNAL);

        int maxNumber = cur.getCount();
        //System.out.println("!!!!!!!!!!!maxnumber " + maxNumber);
        if (maxNumber > 0) {
            cur.moveToLast();
            String lastPhotoName = cur.getString(cur.getColumnIndex("name"));
            String[] nameSeg = lastPhotoName.split("_");
            System.out.println("maxNumber: " + maxNumber + "nameSeg: " + nameSeg);
            //maxNumber = Integer.parseInt(nameSeg[3].replace(".jpg",""));
            maxNumber = Integer.parseInt(nameSeg[nameSeg.length - 1].replace(".jpg",""));
        }
        //todo liczenie zdjęć
        //System.out.println("########nazwapliku: " + emotionAndSex +"_" + ++maxNumber);
        return emotionAndSex +"_e_" + ++maxNumber;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:
                    
                    String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
                    final File path = new File(root + "FriendlyEmotions/Photos" + File.separator);
                    File largeFile = new File(path, fileName + "tmp.jpg");
                    Bitmap largeBitmap = BitmapFactory.decodeFile(largeFile.getAbsolutePath());

                    /*final int maxSize = 400;
                    int outWidth;
                    int outHeight;
                    int inWidth = largeBitmap.getWidth();
                    int inHeight = largeBitmap.getHeight();
                    if(inWidth > inHeight){
                        outWidth = maxSize;
                        outHeight = (inHeight * maxSize) / inWidth;
                    } else {
                        outHeight = maxSize;
                        outWidth = (inWidth * maxSize) / inHeight;
                    }*/

                    Bitmap smallBitmap = Bitmap.createScaledBitmap(largeBitmap, largeBitmap.getWidth() * 1 / 4, largeBitmap.getHeight() * 1 / 4, false);

                    File smallFile = new File(path, fileName + ".jpg");

                    FileOutputStream fOut;
                    try {
                        SqliteManager sqlm = getInstance(this);
                        fOut = new FileOutputStream(smallFile);
                        smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        largeBitmap.recycle();
                        smallBitmap.recycle();
                        largeFile.delete();
                        //DODANIE ZDJĘCIA DO BAZY DANYCH
                        String[] photoSeg = fileName.split("_");
                        int resID = 0; ///DZIWNY PATH
                        sqlm.addPhoto(resID, photoSeg[0]+"_"+photoSeg[1], fileName + ".jpg");
                    } catch (Exception e) {
                    }

                    finish();
                    break;
            }
        }
    }
}
