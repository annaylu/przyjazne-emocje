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
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import pg.autyzm.przyjazneemocje.lib.SqliteManager;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

/**
 * Created by Ann on 13.11.2016.
 */
public class CameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String emotion = extras.getString("SpinnerValue_Emotion");
        fileName = getFileName(emotion);
        takePhoto();

    }

    private String fileName;
    private static final int TAKE_PHOTO_CODE = 1;

    public void takePhoto() {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));
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

    private String getFileName(String emotionLang)
    {
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
        String emotion = mapEmo.get(emotionLang);
        Cursor cur = sqlm.givePhotosWithEmotion(emotion);

        int maxNumber = 1;
        while(cur.moveToNext())
        {
            String name = cur.getString(3);
            name = name.replace(".jpg","").replaceAll("[^0-9]","");
            if(maxNumber < Integer.parseInt(name))
                maxNumber = Integer.parseInt(name);

        }
        return emotion + ++maxNumber;
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

                    Bitmap smallBitmap = Bitmap.createScaledBitmap(largeBitmap,largeBitmap.getWidth() * 1/4,largeBitmap.getHeight()*1/4,false);

                    File smallFile = new File(path, fileName + ".jpg");
                    FileOutputStream fOut;
                    try {
                        fOut = new FileOutputStream(smallFile);
                        smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        largeBitmap.recycle();
                        smallBitmap.recycle();
                        largeFile.delete();
                    } catch (Exception e) {}

                    finish();
                    break;
            }
        }
    }
}
