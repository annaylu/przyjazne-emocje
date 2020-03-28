package pg.autyzm.przyjazneemocje.chooseImages;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.ArrayMap;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;


/**
 * Created by Joanna on 2016-10-08.
 */

public class ChooseImages extends Activity implements android.widget.CompoundButton.OnCheckedChangeListener {

    private ListView listView;
    private String choosenEmotion;
    private RowBean[] tabPhotos;
    private TextView textView;
    private String emoInLanguage;
    private ArrayList<Integer> listSelectedPhotos;

    public void saveImagesToList(View view) {

        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList("selected_photos", listSelectedPhotos);
        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(RESULT_OK, returnIntent);

        finish();
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_images);

        SqliteManager sqlm = getInstance(this);

        Bundle bundle = getIntent().getExtras();
        emoInLanguage = bundle.getString("SpinnerValue_Emotion");

        Map<String, String> mapEmo = new ArrayMap<>();
        //najpierw robie z sensem wyłącznie dla polskiej wersji językowej
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

        choosenEmotion = mapEmo.get(emoInLanguage);

        textView = (TextView) findViewById(R.id.TextViewChoose);
        String str = getResources().getString(R.string.select);

        Cursor cursor = sqlm.givePhotosWithEmotion(choosenEmotion);

        //tu dodaje
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        System.out.println(root);

        String newFileName = "";

        String[] photosNameList = new File(root + "/FriendlyEmotions/Photos").list();
        if(cursor.getCount() < photosNameList.length)
        {
            for(String fileName : photosNameList)
            {

                String tmp = fileName.replace(".jpg","").replaceAll("[0-9]","");
                if(tmp.equals(choosenEmotion))
                {
                    cursor = sqlm.givePhotosWithEmotion(choosenEmotion);
                    boolean finded = true;
                    while(cursor.moveToNext())
                    {
                        finded = false;
                        if (cursor.getString(6).equals(fileName))//bylo 3
                        {
                            finded = true;
                            break;
                        }
                    }
                    if(finded == false)
                        sqlm.addPhoto(1,choosenEmotion,fileName);
                }
            }
        }

        cursor = sqlm.givePhotosWithEmotion(choosenEmotion);


        //

        int n = cursor.getCount();
        tabPhotos = new RowBean[n];
        while (cursor.moveToNext()) {

            tabPhotos[--n] = (new RowBean(cursor.getString(3), cursor.getInt(1), false, getContentResolver(), cursor.getInt(0)));//bylo 3
        }

        //wybrane wczesniej
        listSelectedPhotos = bundle.getIntegerArrayList("selected_photos");
        for (int selected : listSelectedPhotos) {
            for (RowBean el : tabPhotos) {
                if (el.getId() == selected) {
                    el.setSelected(true);
                }
            }
        }

        textView.setText(emoInLanguage + " " + str + ": " + countSelectedPhotos());

        RowAdapter adapter = new RowAdapter(this, R.layout.item, tabPhotos);
        listView = (ListView) findViewById(R.id.image_list);
        listView.setAdapter(adapter);
    }

    private int countSelectedPhotos() {
        int numberOfPhotos = 0;
        for (RowBean el : tabPhotos) {
            if (el.selected) {
                numberOfPhotos++;
            }
        }
        return numberOfPhotos;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        try {
            int pos = listView.getPositionForView(buttonView);
            if (pos != ListView.INVALID_POSITION) {
                if (isChecked) {
                    tabPhotos[pos].setSelected(true);
                    listSelectedPhotos.add(tabPhotos[pos].getId());
                    System.out.println("To trafia do tablicy z idkami photos " + tabPhotos[pos].getId());
                } else {
                    tabPhotos[pos].setSelected(false);
                     listSelectedPhotos.remove((Object)tabPhotos[pos].getId());
                }
            }

            String str = getResources().getString(R.string.select);
            textView.setText(emoInLanguage + " " + str + ": " + countSelectedPhotos());
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }
}
