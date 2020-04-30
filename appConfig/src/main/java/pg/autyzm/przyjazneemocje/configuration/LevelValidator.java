package pg.autyzm.przyjazneemocje.configuration;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pg.autyzm.przyjazneemocje.DialogHandler;
import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.lib.Ania;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

/**
 * Created by user on 26.08.2017.
 */

public class LevelValidator extends AppCompatActivity {


    Level validatedLevel;
    int womanPhotos, manPhotos;
    boolean value;
    int mode;

    Context currentContext;
    SqliteManager sqliteManager = SqliteManager.getAppContext();
    List<Integer> photoIds = new ArrayList<>();
    SqliteManager sqlm = SqliteManager.getInstance(this);

    public LevelValidator(Level l, Object obj) {
        validatedLevel = l;
        currentContext = (Context) obj;
    }


    public boolean validateLevel() {

        //test_adam();

        // sprawdzenie dlugosci nazwy poziomu
        if (validatedLevel.getName().length() == 0) {
            Toast.makeText(currentContext, "Name of the level should contain at least one character", Toast.LENGTH_LONG).show();
            return false;
        }
        if (validatedLevel.getName().length() > 55) {
            Toast.makeText(currentContext, "Name of the level should be shorter - max 55 characters", Toast.LENGTH_LONG).show();
            return false;
        }
        if (validatedLevel.getPhotosOrVideosIdList().isEmpty()) {
            Toast.makeText(currentContext, R.string.empty_photos_learn_mode, Toast.LENGTH_LONG).show();
            return false;
        }

       /* if (!numberOfPhotosSelected(validatedLevel.getPhotosOrVideosShowedForOneQuestion())) {
            return false;
        }*/
        if (!everyEmotionHasAtLestOnePhoto()) {
            //System.out.println("blalaal");
            return false;
        }
        if (!photosOfBothSexesChosen()) {
            //Toast.makeText(currentContext, "Zdjęcia w zakładce MATERIAŁ powinny przedstawiać osoby obydwu płci", Toast.LENGTH_LONG).show();
            //System.out.println("silalaal");
            return false;
        }
        if (validatedLevel.getCommandTypesAsNumber() == 0) {
            Toast.makeText(currentContext, R.string.commandWarning, Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;

    }

    public boolean photosOfBothSexesChosen() {
        SqliteManager sqlm = SqliteManager.getInstance(this);


        boolean differentSexes = validatedLevel.isOptionDifferentSexes();
for (mode = 0; mode < 2; mode++) {

    if (mode == 0) photoIds = validatedLevel.getPhotosOrVideosIdList();
    if (mode == 1) photoIds = validatedLevel.getPhotosOrVideosIdListInTest();

    for (int photoId : photoIds) {
        Cursor cursorPhotoId = sqlm.givePhotoWithId(photoId);
        cursorPhotoId.moveToFirst();
        String photoName = cursorPhotoId.getString(cursorPhotoId.getColumnIndex("name"));

        if (photoName.contains("_woman")) {
            womanPhotos++;
        }
        if (photoName.contains("_man")) {
            manPhotos++;
        }

    }

    if ((differentSexes) && (womanPhotos == 0 || manPhotos == 0)) {
        System.out.println("man photos " + manPhotos + " woman photos " + womanPhotos);
        if (mode == 0)
            Toast.makeText(currentContext, "Zdjęcia w zakładce MATERIAŁ powinny przedstawiać osoby obydwu płci", Toast.LENGTH_LONG).show();
        if (mode == 1)
            Toast.makeText(currentContext, "Zdjęcia w zakładce TEST powinny przedstawiać osoby obydwu płci", Toast.LENGTH_LONG).show();
        return false;

        //COŚ NIE DZIAŁA DLA TESTU !!!!
    }
}
            return true;

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
            //int mode = 0;

            if (mode == 0)
                id_zd = selectedPhotosForGameCheck(validatedLevel.getPhotosOrVideosIdList(), emotionNameWithoutSex);
            else if ((mode == 1 )&& (!validatedLevel.isMaterialForTest()))
                id_zd = selectedPhotosForGameCheck(validatedLevel.getPhotosOrVideosIdListInTest(), emotionNameWithoutSex);


            System.out.println("emotionName " + emotionNameWithoutSex + " size " + id_zd.size());


            if (id_zd.size() < 1) {
                if (mode == 0)
                    Toast.makeText(currentContext, "Select at least one photo in MATERIAL for emotion " + emotionNameWithoutSex, Toast.LENGTH_LONG).show();
                else if  ((mode == 1 )&& (!validatedLevel.isMaterialForTest()))
                    System.out.println("ZDJECIA W TRYBIE TESTOWYM everyEmotion " + id_zd.toString() + " getPhotosOrVideoIdListInTest " + validatedLevel.getPhotosOrVideosIdListInTest().toString());
                    Toast.makeText(currentContext, "Select at least one photo in TEST for emotion " + emotionNameWithoutSex, Toast.LENGTH_LONG).show();
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



    public boolean numberOfPhotosSelected(int numberOfPhotosDisplayed, boolean differentSexes) {
        String emotionNameWithoutSex;
        int womanPhotos = 0, manPhotos = 0;
        int countedWoman;
        int countedMan;
        int mode = 0;
        System.out.println("numberofphotos differentsexes " + differentSexes);
        if (!differentSexes) {
            List<Integer> id_zd = new ArrayList<>();
            for (int emotion : validatedLevel.getEmotions()) {
                countedMan = 0;
                countedWoman = 0;

                //emotionNameWithoutSex = lca.getEmotionNameinBaseLanguage(emotion);
                emotionNameWithoutSex = getEmotionNameinBaseLanguage2(emotion);
                //emotionNameWithoutSex = getEmotionNameinBaseLanguage3(emotion);
                //for (int mode = 0; mode < 2; mode++) {


                if (mode == 0) {
                    id_zd = selectedPhotosForGameCheck(validatedLevel.getPhotosOrVideosIdList(), emotionNameWithoutSex);
                } else if (mode == 1) {
                    id_zd = selectedPhotosForGameCheck(validatedLevel.getPhotosOrVideosIdListInTest(), emotionNameWithoutSex);
                }

                for (int i : id_zd) {
                    Cursor curEmotion = sqlm.givePhotoWithId(i);

                    if (curEmotion.getCount() != 0) { //obejście  z tata
                        curEmotion.moveToFirst();
                        String photoName = curEmotion.getString(curEmotion.getColumnIndex("name"));

                        if (countedWoman == 0 && photoName.contains("woman")) {
                            womanPhotos++;
                            countedWoman = 1;
                        }
                        if (countedMan == 0 && photoName.contains("_man")) {
                            manPhotos++;
                            countedMan = 1;
                        }
                    }
curEmotion.close();
                }
                System.out.println("emotionName " + emotionNameWithoutSex + " size " + id_zd.size());

            }
          /* if (womanPhotos < numberOfPhotosDisplayed && manPhotos < numberOfPhotosDisplayed) {
                Toast.makeText(currentContext, "Wybrana liczba wyświetlanych zdjęć to " + numberOfPhotosDisplayed + ", wybierz conajmniej po jednym zdjęciu kobiet i conajmniej tyle zdjęć mężczyzn", Toast.LENGTH_LONG).show();
                return false;*/
            System.out.println("!differentSexes lv " + differentSexes);

    System.out.println("LEVEL VALIDATOR " + differentSexes);
    if (womanPhotos < numberOfPhotosDisplayed) {
        Toast.makeText(currentContext, "Wybrana liczba wyświetlanych zdjęć to " + numberOfPhotosDisplayed + ", wybierz conajmniej jedno zdjęcie kobiety dla " + numberOfPhotosDisplayed + " różnych emocji lub zmniejsz liczbę zdjęć wyświetlanych na ekranie", Toast.LENGTH_LONG).show();
        return false;
    } else if (manPhotos < numberOfPhotosDisplayed) {
        Toast.makeText(currentContext, "Wybrana liczba wyświetlanych zdjęć to " + numberOfPhotosDisplayed + ", wybierz conajmniej jedno zdjęcie mężczyzny dla " + numberOfPhotosDisplayed + " różnych emocji lub zmniejsz liczbę zdjęć wyświetlanych na ekranie. Are you sure you want to save?", Toast.LENGTH_LONG).show();
        return false;

}
            ///TODO SPRAWDZENIE TRFBU TESTOWEGO - jeśli chceckbox odznaczony
            //
            //}

            /*AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);
            builder.setMessage("You have set number of pictures displayed for " + numberOfPhotosDisplayed + ", but there are not enough men pictures selected. Are you sure you want to save?").setPositiveButton("SAVE", dialogClickListener)
                    .setNegativeButton("Don't save, I want to select more pictures", dialog).show();*/

     /*       AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing, but close the dialog
                    dialog.dismiss();
                    mHandler.sendMessage(mHandler.obtainMessage(DIALOG_CLICKED, true));
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                    mHandler.sendMessage(mHandler.obtainMessage(DIALOG_CLICKED, false));
                }
            });

            AlertDialog alert = builder.create();
            alert.show();*/

          /*  if (Confirm(currentContext))
                return true;
            else
                return false;
*/


        }

return true;
    }


    List<Integer> selectedPhotosForGameCheck(List<Integer> photos, String emotionBaseNameWithoutSex) {


        List<Integer> photosForEmotionList = new ArrayList<>();
        System.out.println("@@@@@@@@@ 1 selectedPhotosForGameCHECK emotion level : " + validatedLevel + " id " + validatedLevel.getId() +" " + emotionBaseNameWithoutSex + " idPhoto: " + photos);
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
curEmotion.close();
            }
        }

        System.out.println("@@@@@@@@@ 2 selectedPhotosForGameCHECK emotion: " + emotionBaseNameWithoutSex + " idPhoto: " + photosForEmotionList + " \nmaterial " + validatedLevel.getPhotosOrVideosIdList() + " test " + validatedLevel.getPhotosOrVideosIdListInTest());
        return photosForEmotionList;
    }

    public String getEmotionNameinBaseLanguage2(int emotionNumber) {

        switch (emotionNumber) {
         /*   case 1: return "happy";
            case 2: return "sad";
            case 3: return  "surprised";
            case 4: return "angry";
            case 5: return "scared";
            case 6: return "bored";*/

            case 0:
                return "happy";
            case 1:
                return "sad";
            case 2:
                return "surprised";
            case 3:
                return "angry";
            case 4:
                return "scared";
            case 5:
                return "bored";

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

    public void test_adam() {

        Ania sqlm = new Ania();

        sqlm.adam_print();

    }




    protected static final int DIALOG_CLICKED = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String txt;
            switch (msg.what) {
                case DIALOG_CLICKED:
                    Boolean bool = Boolean.parseBoolean(msg.obj.toString());
                    // When user press on "OK",
                    // your braking point goes here
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };



    public void doclick1() {
        DialogHandler appdialog = new DialogHandler();
        appdialog.Confirm(this, "Insufficient photos chosen", "A re you sure you want to save?",
                "No, I want to choose more photos.", "Yes, save.", aproc1(), bproc1());
    }
    public Runnable aproc1(){
        return new Runnable() {
            public void run() {
                //save(LevelConfigurationActivity.this);
                finish();
                Log.d("Test", "This from A proc");
            }
        };
    }
    public Runnable bproc1(){
        return new Runnable() {
            public void run() {
                Log.d("Test", "This from B proc");
            }
        };
    }


}