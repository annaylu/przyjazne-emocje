package pg.autyzm.przyjazneemocje.configuration;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.configuration.LevelConfigurationActivity;
import pg.autyzm.przyjazneemocje.lib.Adam;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

/**
 * Created by user on 26.08.2017.
 */

public class LevelValidator extends AppCompatActivity {

    Level validatedLevel;
    Context currentContext;
    SqliteManager sqliteManager = SqliteManager.getAppContext();

    public LevelValidator(Level l, Object obj) {
        validatedLevel = l;
        currentContext = (Context) obj;
    }


    public boolean validateLevel() {

        test_adam();

        // sprawdzenie dlugosci nazwy poziomu
        if (validatedLevel.getName().length() == 0 || validatedLevel.getName().length() > 50) {
            Toast.makeText(currentContext, "blalalal", Toast.LENGTH_LONG).show();
            return false;
        }
        // prosta walidacja, czy w ogle zaznaczono jakies emocje (minimum dwa)/zdjecia
        if (validatedLevel.getEmotions().size() < 2) {
            //Toast.makeText("Wybierz conajmniej jedną emocję",Toast.LENGTH_LONG);
            return false;
        }
        if (everyEmotionHasAtLestOnePhoto()) {
            return true;
        } else {
            Toast.makeText(currentContext, "Select one photo for emotion ", Toast.LENGTH_LONG);
            return false;
        }
    }

    public boolean everyEmotionHasAtLestOnePhoto() {
        String emotionNameWithoutSex;
        LevelConfigurationActivity lca = new LevelConfigurationActivity();
        for (int emotion : validatedLevel.getEmotions()) {
            List<Integer> id_zd = new ArrayList<>();
            //emotionNameWithoutSex = lca.getEmotionNameinBaseLanguage(emotion);
            emotionNameWithoutSex = getEmotionNameinBaseLanguage2(emotion);
            //emotionNameWithoutSex = getEmotionNameinBaseLanguage3(emotion);
            for (int mode = 0; mode < 2; mode++) {

if (mode == 0)
                id_zd = selectedPhotosForGameCheck(validatedLevel.getPhotosOrVideosIdList(), emotionNameWithoutSex);
else if (mode == 1)
    id_zd = selectedPhotosForGameCheck(validatedLevel.getPhotosOrVideosIdListInTest(), emotionNameWithoutSex);



                System.out.println("emotionName " + emotionNameWithoutSex + " size " + id_zd.size());


                if (id_zd.size() < 1) {
                    if (mode == 0) Toast.makeText(currentContext, "Select at least one photo in MATERIAL for emotion " + emotionNameWithoutSex, Toast.LENGTH_LONG).show();
                    else Toast.makeText(currentContext, "Select at least one photo in TEST for emotion "  + emotionNameWithoutSex, Toast.LENGTH_LONG).show();
                    System.out.println("DRUKUJEMYY!!!");
                    return false;
                }
                ///TODO SPRAWDZENIE TRYBU TESTOWEGO - jeśli chceckbox odznaczony
                //
            }
        }

        /*for(int emotion : sqliteManager.givePhotosWithEmotion(emotion)){
            if (validatedLevel.getPhotosOrVideosIdList().size() < 1)
                return false;
        }
*/
        return true;

    }


    List<Integer> selectedPhotosForGameCheck(List<Integer> photos, String emotionBaseNameWithoutSex) {
        SqliteManager sqlm = SqliteManager.getInstance(this);
        boolean differentSexes = validatedLevel.isOptionDifferentSexes();
        List<Integer> photosForEmotionList = new ArrayList<>();
        System.out.println("@@@@@@@@@ 1 selectedPhotosForGameCHECK emotion: " + emotionBaseNameWithoutSex + " idPhoto: " + photos);
        for (int e : photos) {
            //System.out.println("Id zdjecia: " + e);
            Cursor curEmotion = sqlm.givePhotoWithId(e);
            // Cursor curSelectedEmotion = sqlm.givePhotosWithEmotion(emotionBaseNameWithoutSex);
            //System.out.println("CURSELECTEDEMOTION SIZE " + curEmotion.getCount());


            if (curEmotion.getCount() != 0) { //obejście  z tata
                curEmotion.moveToFirst();
                String photoEmotionName = curEmotion.getString(curEmotion.getColumnIndex("emotion"));
                String photoName = curEmotion.getString(curEmotion.getColumnIndex("name"));

                if (photoEmotionName.contains(emotionBaseNameWithoutSex)) {
                    photosForEmotionList.add(e);
                    //System.out.println("@@@@@@@@@ ŚCIEŻKA IF selectedPhotosForGame idzdjecia: " + e + " photoName " + photoName + " PHPTPEmotionName " + photoEmotionName);
                }

            }
        }
        System.out.println("@@@@@@@@@ 2 selectedPhotosForGameCHECK emotion: " + emotionBaseNameWithoutSex + " idPhoto: " + photosForEmotionList);
        return photosForEmotionList;
    }

    public String getEmotionNameinBaseLanguage2(int emotionNumber) {

        switch(emotionNumber) {
         /*   case 1: return "happy";
            case 2: return "sad";
            case 3: return  "surprised";
            case 4: return "angry";
            case 5: return "scared";
            case 6: return "bored";*/

            case 0: return "happy";
            case 1: return "sad";
            case 2: return  "surprised";
            case 3: return "angry";
            case 4: return "scared";
            case 5: return "bored";

        }
        return "zly numer emocji";
}
    public String getEmotionNameinBaseLanguage3(int emotionNumber) {
        Context o1;
        Resources o2;
        Configuration o3;
        o1 = getApplicationContext();
        o2 = o1.getResources();
        o3 = o2.getConfiguration();
        Configuration config = new Configuration(getBaseContext().getResources().getConfiguration());
        config.setLocale(Locale.ENGLISH);
        return getBaseContext().createConfigurationContext(config).getResources().getStringArray(R.array.emotions_array)[emotionNumber];
    }

    public void test_adam()
    {

        Adam sqlm = new Adam();

    sqlm.adam_print();

    }
}