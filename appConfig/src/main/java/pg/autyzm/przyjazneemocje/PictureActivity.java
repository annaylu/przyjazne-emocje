package pg.autyzm.przyjazneemocje;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

public class PictureActivity extends AppCompatActivity {
Button great;
Button delete;
Button another_photo;
TextView emocja;
TextView plec;
private Level level;
String fileName;

    private ImageView imageView;
    private static final String IMAGE_DIRECTORY = "/FriendlyEmotions/Photos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture);

        //POWINNO SIE O WIEEEEEEEEEELE ŁADNIEJ ZROBIĆ TO


    String emotion = getIntent().getStringExtra("emocja");
        fileName = getFileName(emotion);
        System.out.println("EMOCJAAAAAAAAAAA " + emotion);
        imageView = findViewById(R.id.img);


        imageView.setImageBitmap(MainCameraActivity.bitmap);
        saveImage(MainCameraActivity.bitmap);

        emocja = findViewById(R.id.photo_emocja);
       plec = findViewById(R.id.photo_plec);
       emocja.setText("wesoła");
       plec.setText("kobieta");

       delete = (Button) findViewById(R.id.photo_delete);
       great = (Button) findViewById(R.id.photo_super);
another_photo = (Button) findViewById(R.id.another_photo);

another_photo.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(PictureActivity.this,MainCameraActivity.class);
        startActivity(intent);
    }
});
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //OGARNĄĆ
               //ALE JAK BĘDZIE CZAS
               //level.addPhotoToBePermanentlyDeleted(mAI);
           }
       });

       delete.setText("DELETE PHOTO");
       great.setText("SUPER!");

       great.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(PictureActivity.this,MainActivity.class);
               startActivity(intent);
           }
       });




       /* new CountDownTimer(2000, 1) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {

                finish();

            }
        }.start();*/


    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }

        try {
       /*     File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");*/
            File f = new File(wallpaperDirectory, fileName + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
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
            System.out.println("maxNumber: " + maxNumber + "nameSeg: " + nameSeg.toString());
            //maxNumber = Integer.parseInt(nameSeg[3].replace(".jpg",""));
            maxNumber = Integer.parseInt(nameSeg[nameSeg.length - 1].replace(".jpg",""));
        }
        //todo liczenie zdjęć
        //System.out.println("########nazwapliku: " + emotionAndSex +"_" + ++maxNumber);
        return emotionAndSex +"_e_" + ++maxNumber;
    }
}
