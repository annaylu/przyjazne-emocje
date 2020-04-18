package pg.autyzm.przyjazneemocje;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

public class PictureActivity extends AppCompatActivity {
Button saveandexit;
Button delete;
Button saveandtakephoto;
TextView emocja;
TextView plec;
private Level level;
String fileName, emotion, sex;
boolean deleted = false;
int sumOfAnother;


    private ImageView imageView;
    private static final String IMAGE_DIRECTORY = "/FriendlyEmotions/Photos/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picture);

        //POWINNO SIE O WIEEEEEEEEEELE ŁADNIEJ ZROBIĆ TO


    emotion = getIntent().getStringExtra("emocja");
   sex = getIntent().getStringExtra("sex");
   sumOfAnother=getIntent().getIntExtra("sumOfAnother",0);
        fileName = getFileName(emotion);
        System.out.println("EMOCJAAAAAAAAAAA " + emotion);
        imageView = findViewById(R.id.img);


        imageView.setImageBitmap(MainCameraActivity.bitmap);
        saveImage(MainCameraActivity.bitmap);

        emocja = findViewById(R.id.photo_emocja);
       plec = findViewById(R.id.photo_plec);
       emocja.setText(emotion);
       plec.setText(sex.replace("a ","").replace("an ","").replace("ty","ta").replace("ny","na"));

       delete = (Button) findViewById(R.id.photo_delete);
       saveandexit = (Button) findViewById(R.id.photo_super);
        saveandtakephoto = (Button) findViewById(R.id.another_photo);


saveandtakephoto.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
   /*     if (!deleted) {
            saveImage(MainCameraActivity.bitmap);
            System.out.println("BLALALALLAL");
        }*/
        Intent intent = new Intent(PictureActivity.this,MainCameraActivity.class);
        intent.putExtra("SpinnerValue_Emotion",emotion);
        intent.putExtra("SpinnerValue_Sex",sex);

        intent.putExtra("sumOfAnother",++sumOfAnother);
        startActivity(intent);
    }
});
       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              imageView.setVisibility(View.GONE);
              delete.setVisibility(View.GONE);
              saveandexit.setText(getResources().getString(R.string.camera_activity_exit));


              deleted = true;
           }
       });



       saveandexit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              /* if (!deleted) saveImage(MainCameraActivity.bitmap);
               deleted = false;*/
               Intent intent = new Intent(PictureActivity.this,MainActivity.class);
               startActivity(intent);
           }
       });


    }


    public String saveImage(Bitmap myBitmap) {
        sumOfAnother = getIntent().getIntExtra("sumOfAnother",0);
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
        int sum = ++maxNumber + sumOfAnother;
        return emotionAndSex +"_e_" + sum;
    }
}
