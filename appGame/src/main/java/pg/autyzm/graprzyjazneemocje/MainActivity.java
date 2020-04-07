package pg.autyzm.graprzyjazneemocje;

import android.app.Activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.teamlog.SmartSyncOptOutType;
import com.j256.ormlite.stmt.query.In;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pg.autyzm.graprzyjazneemocje.animation.AnimationActivity;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

//import static pg.autyzm.przyjazneemocje.lib.SqliteManager.Source.BOTH;
import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;


public class MainActivity extends Activity implements View.OnClickListener {

    int sublevelsLeft;
    List<Integer> sublevelsList;
int whichTry = 1;
    List<String> photosWithEmotionSelected;
    List<String> photosWithRestOfEmotions;
    List<String> photosToUseInSublevel;
    String goodAnswer;
    Cursor cur0;
    Cursor videoCursor;
    SqliteManager sqlm;
    int wrongAnswers;
    int rightAnswers;
    int wrongAnswersSublevel;
    int rightAnswersSublevel;
    int timeout;
    int timeoutSubLevel;
    String commandText;
    boolean animationEnds = true;
    Level level;
    ImageView image_selected=null;
    CountDownTimer timer;
    public Speaker speaker;
    int height;
    LinearLayout linearLayout1;
    private int listSize;
    LinearLayout.LayoutParams lp;
    String speakerText;
    int proba;



    public SubLevelMode getSubLevelMode() {
        return subLevelMode;
    }

    public void setSubLevelMode(SubLevelMode subLevelMode) {
        this.subLevelMode = subLevelMode;
    }

    private SubLevelMode subLevelMode;

    public int getAttempt() {
        return attempt;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    private int attempt;
    boolean videos = false; //TODO: Get from database
    static int repeat = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        sqlm = getInstance(this);

        sqlm.getReadableDatabase();

        // Birgiel
        cur0 = sqlm.giveAllLevels();

        videoCursor = sqlm.giveAllVideos(); //TODO: Change to "giveVideosInLevel(int levelId)
        videoCursor.moveToFirst();

        if (!videos) {
            setContentView(R.layout.activity_main);
        } else {
            Intent i = new Intent(this, VideoWelcomeActivity.class);
            startActivity(i);
            setContentView(R.layout.activity_videos);
        }

        findNextActiveLevel();
        generateView(photosToUseInSublevel);

        if (!videos) {
            if (!level.isShouldQuestionBeReadAloud()) {
                findViewById(R.id.matchEmotionsSpeakerButton).setVisibility(View.GONE);
            } else {
                speaker = Speaker.getInstance(MainActivity.this);
                final ImageButton speakerButton = (ImageButton) findViewById(R.id.matchEmotionsSpeakerButton);
                speakerButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        speaker.speak(commandText);
                        //MediaPlayer ring= MediaPlayer.create(MainActivity.this,R.raw.showwhereis);
                        //ring.start();
                    }
                });
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public boolean findNextActiveLevel() {

        if (sublevelsLeft != 0) {
            wrongAnswersSublevel = 0;
            generateSublevel(sublevelsList.get(sublevelsLeft - 1));
            return true;
        }

        // zaraz zostanie zaladowany nowy poziom (skonczyly sie podpoziomy. trzeba ustalic, czy dziecko odpowiedzialo wystarczajaco dobrze, by przejsc dalej
        //System.out.println(cur0.getCount());
        while (cur0.moveToNext()) {
            int levelId = cur0.getInt(cur0.getColumnIndex("id"));

            Cursor cur2 = sqlm.giveLevel(levelId);
            Cursor cur3 = sqlm.givePhotosInLevel(levelId);
            Cursor cur4 = sqlm.giveEmotionsInLevel(levelId);

            level = new Level(cur2, cur3, cur4);
            if (level.isLearnMode() || level.isTestMode()) {
                return loadLevel(level);
            } else {
                continue;
            }
        }
        return false;
    }

    boolean loadLevel(Level level) {
        wrongAnswersSublevel = 0;
        rightAnswersSublevel = 0;
        timeoutSubLevel = 0;

        int photosPerLvL = 0;

        if (level.getPhotosOrVideosFlag().equals("videos")) {
            videos = true;
        } else {
            videos = false;
        }

        photosPerLvL = level.getPhotosOrVideosShowedForOneQuestion();
        level.incrementEmotionIdsForGame();

        // tworzymy tablice do permutowania
        if (!videos)
            sublevelsLeft = level.getEmotions().size() * level.getSublevelsPerEachEmotion();
        else
            sublevelsLeft = videoCursor.getCount();

        sublevelsList = new ArrayList<Integer>();

        for (int i = 0; i < level.getEmotions().size(); i++) {
            for (int j = 0; j < level.getSublevelsPerEachEmotion(); j++) {
                sublevelsList.add(level.getEmotions().get(i));
            }
        }

        java.util.Collections.shuffle(sublevelsList);
        generateSublevel(sublevelsList.get(sublevelsLeft - 1));

        // wylosuj emocje z wybranych emocji, odczytaj jej imie (bo mamy liste id)
        //int emotionIndexInList = selectEmotionToChoose(level);

        return true;
    }

    void generateSublevel(int emotionIndexInList) {
        attempt = 0;
        whichTry = 1;
        subLevelMode = SubLevelMode.NO_WRONG_ANSWER;
        //System.out.println("Ustawiamy tu NO WRONG ANSWER");
        Cursor emotionCur = sqlm.giveEmotionName(emotionIndexInList);

        emotionCur.moveToFirst();
        String selectedEmotionName = emotionCur.getString(emotionCur.getColumnIndex("emotion"));
        // po kolei czytaj nazwy emocji wybranych zdjec, jesli ich emocja = wybranej emocji, idzie do listy a, jesli nie, lista b

        photosWithEmotionSelected = new ArrayList<String>();
        photosWithRestOfEmotions = new ArrayList<String>();
        photosToUseInSublevel = new ArrayList<String>();

        if (level.isTestMode()) {
            selectedPhotosForGame(level.getPhotosOrVideosIdListInTest(), selectedEmotionName);
        } else {
            selectedPhotosForGame(level.getPhotosOrVideosIdList(), selectedEmotionName);
        }

        // z listy a wybieramy jedno zdjecie, ktore bedzie prawidlowa odpowiedzia
        goodAnswer = selectPhotoWithSelectedEmotion();

        // z listy b wybieramy zdjecia nieprawidlowe
        selectPhotoWithNotSelectedEmotions(level.getPhotosOrVideosShowedForOneQuestion());

        // laczymy dobra odpowiedz z reszta wybranych zdjec i przekazujemy to dalej
        photosToUseInSublevel.add(goodAnswer);

        //java.util.Collection<String> kakk =
        java.util.Collections.shuffle(photosToUseInSublevel);

        // z tego co rozumiem w photosList powinny byc name wszystkich zdjec, jakie maja sie pojawic w lvl (czyli - 3 pozycje)


        if (level.isLearnMode()) {
            startTimer(level);
        } else if (level.isTestMode()) {

            Intent intent = new Intent(MainActivity.this,Blank.class);
            startActivity(intent);
            speakerText = "Próba numer jeden";
            Speaker.getInstance(MainActivity.this).speak(speakerText);
            StartTimerForTest(level);
        }


           /* final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LinearLayout imagesLinear = (LinearLayout)findViewById(R.id.imageGallery);

                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation((float)0.1);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

                    final int childcount = imagesLinear.getChildCount();
                    for (int i = 0; i < childcount; i++)
                    {
                        ImageView image = (ImageView) imagesLinear.getChildAt(i);
                        if(image.getId() != 1)
                        {
                            image.setColorFilter(filter);
                        }

                    }
                    timeout ++;
                }
            }, level.timeLimit * 1000);*/


        // /birgiel

    }

    void selectedPhotosForGame(List<Integer> photos, String selectedEmotionName) {
        for (int e : photos) {
            //System.out.println("Id zdjecia: " + e);
            Cursor curEmotion = sqlm.givePhotoWithId(e);



            if (curEmotion.getCount() != 0){
                curEmotion.moveToFirst();
                String photoEmotionName = curEmotion.getString(curEmotion.getColumnIndex("emotion"));
                String photoName = curEmotion.getString(curEmotion.getColumnIndex("name"));

                if (photoEmotionName.equals(selectedEmotionName)) {
                    photosWithEmotionSelected.add(photoName);
                } else {
                    photosWithRestOfEmotions.add(photoName);
                }
            }
        }
    }

    public void generateView(List<String> photosList) {

       // System.out.println("goodAnswer przed cięciami: " + goodAnswer);

        String rightEmotion = goodAnswer.replace(".jpg", "").replaceAll("[0-9.]", "").replaceAll("_r_", "").replaceAll("_e_","");
        //Intent commandIntent = new Intent(MainActivity.this,Command.class);

        if (!videos) {
            TextView txt = (TextView) findViewById(R.id.rightEmotion);
/*
            System.out.println("rightEmotion: " + rightEmotion);
            System.out.println("goodAnswer po cięciach: " + goodAnswer);
            System.out.println();*/


            String rightEmotionLang = getResources().getString(getResources().getIdentifier("emotion_" + rightEmotion, "string", getPackageName()));
          /*  System.out.println("rightEmotionLang: " + rightEmotionLang);

            System.out.println("levelQuestionType " + level.getQuestionType());
            System.out.println("ANIAAA EMOTION_NAME " + Level.Question.EMOTION_NAME);*/
            if (level.getQuestionType() != Level.Question.EMOTION_NAME && level.getQuestionType() != Level.Question.SHOW_EMOTION_NAME) {
                if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME)) {
                    final int commandTypes = level.getCommandTypesAsNumber();
                /*System.out.println("commanDtypes: " + commandTypes);
                System.out.println("question type: " + level.getQuestionType());*/
                    ArrayList<String> commandsSelected = new ArrayList<>();
                    //checkbox: 8


                    if ((commandTypes & CommandTypeValue(CommandType.TOUCH)) == CommandTypeValue(CommandType.TOUCH)) {

                        commandsSelected.add(getResources().getString(R.string.touch_where));

                    }
                    //checkbox:1
                    if ((commandTypes & CommandTypeValue(CommandType.SHOW)) == CommandTypeValue(CommandType.SHOW)) {
                        commandsSelected.add(getResources().getString(R.string.show_where));
                    }

                    //checkbox: 4
                    if ((commandTypes & CommandTypeValue(CommandType.SHOW)) == CommandTypeValue(CommandType.POINT)) {
                        commandsSelected.add(getResources().getString(R.string.point_where));

                    }

                    //checkbox:2
                    if ((commandTypes & CommandTypeValue(CommandType.SELECT)) == CommandTypeValue(CommandType.SELECT)) {
                        commandsSelected.add(getResources().getString(R.string.select_where));


                    }
                    //checkbox: 16
                    if ((commandTypes & CommandTypeValue(CommandType.FIND)) == CommandTypeValue(CommandType.FIND)) {
                        commandsSelected.add(getResources().getString(R.string.find_where));
                    }


                    int size = commandsSelected.size();
                    String[] commandsToChoose = new String[size];
                    for (int i = 0; i < size; i++) {
                        commandsToChoose[i] = commandsSelected.get(i);
                    }



          /*      System.out.println("anusia  size: " + size);
                System.out.println("commandsToChoose" + commandsToChoose.toString());
                System.out.println("commandsSelected: " + commandsSelected.toString());
                System.out.println("commands to choose length" + commandsToChoose.length);*/

                    commandText = commandsToChoose[(int) Math.floor(Math.random() * (size))] + " " + rightEmotionLang;

                } else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME)) {
                    commandText = getResources().getString(R.string.show) + " " + rightEmotionLang;
                } else if (level.getQuestionType().equals(Level.Question.EMOTION_NAME)) {
                    commandText = rightEmotionLang;
                }
            /*commandIntent.putExtra("emotion",rightEmotionLang);
            commandIntent.putExtra("command",commandText);
            startActivity(commandIntent);*/
                txt.setText(commandText);
                System.out.println("!!!!!!!!!!!!!! " + commandText);
            }

        }

        linearLayout1 = (LinearLayout) findViewById(R.id.imageGallery);
        linearLayout1.setGravity(Gravity.CENTER);
        linearLayout1.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        linearLayout1.removeAllViews();
        listSize = photosList.size();



        for(
                String photoName :photosList)

        {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            File fileOut = new File(root + "FriendlyEmotions/Photos" + File.separator + photoName);
            try {
                final ImageView image = new ImageView(MainActivity.this);

                setLayoutMargins(image,45/listSize,45/listSize,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));

                if (photoName.contains(rightEmotion)) {
                    image.setId(1);
                } else {
                    image.setId(0);
                }

                image.setOnClickListener(this);
                final Bitmap captureBmp = Media.getBitmap(getContentResolver(), Uri.fromFile(fileOut));
/*if (level.isTestMode()) {*/
    image.setImageBitmap(captureBmp);
/*} else {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            image.setImageBitmap(captureBmp);

        }
    }, 2000);
}*/
                linearLayout1.addView(image);
            } catch (IOException e) {
                System.out.println("IO Exception " + photoName);
            }
        }

    }


    public void onClick(View v) {
        if (level.isTestMode()) {
            onClickTestMode(v, false);
        } else {
           onClickLearnMode(v);

        }
    }

    public void onClickTestMode(View v, boolean endTimer) {
        int x;
        if (v.getId() == 1) {
            sublevelsLeft--;

            rightAnswersSublevel++;

            if (getAttempt()==0)
                rightAnswers++;









            nextLevelOrEnd();
        } else {
           /* speakerText = "Próba numer " + wrongAnswersSublevel;
            Speaker.getInstance(MainActivity.this).speak(speakerText);*/
            Intent intent = new Intent(MainActivity.this,Blank.class);
                    startActivity(intent);


            TextView numberOfTries = (TextView) findViewById(R.id.numberOfTries);
            wrongAnswers++;
            wrongAnswersSublevel++;
            setAttempt(1);
            if (endTimer) {
                timeout++;
                timeoutSubLevel++;
                x = wrongAnswersSublevel + 1;
                if (x<=3) {
                    speakerText = "Próba numer " + x;
                    Speaker.getInstance(MainActivity.this).speak(speakerText);
                }
            }
            if (wrongAnswersSublevel >= level.getNumberOfTriesInTest()) {
                sublevelsLeft--;
                numberOfTries.setVisibility(View.INVISIBLE);
                nextLevelOrEnd();
            } else if (!endTimer) {
              /*  speakerText = "Próba numer " + wrongAnswersSublevel;
                Speaker.getInstance(MainActivity.this).speak(speakerText);*/
                //numberOfTries.setVisibility(View.VISIBLE);
                //numberOfTries.setText(getString(R.string.tries) + wrongAnswersSublevel + "/" + level.getNumberOfTriesInTest());
                //Toast.makeText(getApplicationContext(), "Odpowiedź nieprawidłowa\nSpróbuj ponownie.", Toast.LENGTH_LONG).show();
                x = wrongAnswersSublevel + 1;
                if (x<=3) {
                    speakerText = "Próba numer " + x;
                    Speaker.getInstance(MainActivity.this).speak(speakerText);
                }
                timer.start();
            } else {

                //numberOfTries.setVisibility(View.VISIBLE);
                //numberOfTries.setText(getString(R.string.tries) + wrongAnswersSublevel + "/" + level.getNumberOfTriesInTest());
               // Toast.makeText(getApplicationContext(), "-Upłynął czas odpowiedzi.\n-Odpowiedź nieprawidłowa.\n-Spróbuj ponownie.", Toast.LENGTH_LONG).show();
                timer.start();
                x = wrongAnswersSublevel + 1;
                if (x<=3) {
                    speakerText = "Próba numer " + x;
                    Speaker.getInstance(MainActivity.this).speak(speakerText);
                }
                timer.start();
            }
        }

    }

/*    public void hideImages(ImageView image) {
        image.setVisibility(View.INVISIBLE);
        try{Thread.sleep(2000);}
        catch (InterruptedException ex) {

        }
        image.setVisibility(View.VISIBLE);
    }*/

    public void onClickLearnMode(View v) {
        System.out.println("## ONCLICKLEARNMODE Aktualny sublevel mode:  B " + subLevelMode);
        Intent intent = new Intent(MainActivity.this,RewardAndHintActivity.class);
        if (v.getId() == 1) {
            //wybór prawidłowego zdjęcia
            //sublevelsLeft--;
            whichTry = 1;
            selected_image_unzoom();
            if (getAttempt() == 0) {
                rightAnswers++;
                attempt = 1;
                if (subLevelMode == SubLevelMode.AFTER_WRONG_ANSWER_1_CORRECT) {
                    selected_image_unzoom();
                    setLayoutMargins(image_selected,45/listSize,45/listSize,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
                    attempt = 2;
                }


            }
            ///TODO tata gra
            rightAnswersSublevel++;

            timer.cancel();
            clear_efects_on_all_images();




            boolean correctness = true;

            if ((subLevelMode == SubLevelMode.NO_WRONG_ANSWER) || (subLevelMode == SubLevelMode.AFTER_WRONG_ANSWER_2_CORRECT) ) {

                //udzialona od razu odpowiedź prawidłowa
                System.out.println("Udizelona prawidłowa, chcemy przzekść do następnego; Sublevel.mode= " + subLevelMode);

                //usunelamteraz startTimer(level);
                sublevelsLeft--;
                startRewardActivity();



            }
            else if (subLevelMode == SubLevelMode.AFTER_WRONG_ANSWER) {
                subLevelMode = SubLevelMode.AFTER_WRONG_ANSWER_1_CORRECT;
                // zostajemy na tym samym subLevelu

                startHintActivity();
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        selected_image_unzoom();

                        clear_efects_on_all_images();
                        whichTry = 1;

                    }
                }, 300);
                timer.cancel();
                startTimer(level);


            }
            else if (subLevelMode == SubLevelMode.AFTER_WRONG_ANSWER_1_CORRECT) {
                subLevelMode = SubLevelMode.AFTER_WRONG_ANSWER_2_CORRECT;

                //zostajemy na tym samym sublevelu, mieszamy kolejność zdjęć
               /*
               whichTry++;
               speakerText = "Próba numer " + whichTry;
                if (whichTry >3)
                    speakerText = "Kolejna próba";
                Speaker.getInstance(MainActivity.this).speak(speakerText);
                */

                startRewardActivity();
                Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {



                        timer.cancel();
                        clear_efects_on_all_images();



                    }
                }, 350);
                reorder_image();




       /*         Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                    }
                }, 300);*/








                startTimer(level);


                intent.putExtra("whichTry",whichTry);
               /* whichTry++;
                speakerText = "Próba numer " + whichTry;
                if (whichTry >3)
                    speakerText = "Kolejna próba";

                Speaker.getInstance(MainActivity.this).speak(speakerText);*/


            }

            System.out.println("Aktualny sublevel mode:  B " + subLevelMode);
            if (sublevelsLeft == 0) {
                correctness = checkCorrectness();
            }

            if (correctness && level.isLearnMode()) {
                //startRewardActivity();

            } else {
                startEndActivity(false);
            }

        } else { //jesli nie wybrano wlasciwej
            //wczesniej było z IFem i również liczyło po jednej nieprawidłowej, teraz będzie liczyć każdą nieprawidłową
            attempt = 1;
            subLevelMode= SubLevelMode.AFTER_WRONG_ANSWER;
        /*    whichTry++;
            if (whichTry >3)
                speakerText = "Kolejna próba";
            speakerText = "Próba numer " + whichTry;
            Speaker.getInstance(MainActivity.this).speak(speakerText);*/
            System.out.println("zla odpowiedz, sublevel mode: " + subLevelMode);

            if (whichTry !=3) {
             /*   timer.cancel();
                selected_image_unzoom();

                startTimer(level);*/
            }

//setLayoutMargins(image_selected,45/listSize,45/listSize,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
            wrongAnswers++;
            wrongAnswersSublevel++;
            if (whichTry != 3)
                timer.onFinish();
            whichTry = 3;
            //ANIANOWEWIDOKI dodałam ifa:
        /*if (!isFirstWrong())
            sublevelsLeft--;
*/
        }
    }

    void nextLevelOrEnd() {
        timer.cancel();
        if (!findNextActiveLevel()) {
            startEndActivity(true);
        } else {
            if (level.isTestMode()) {
          /* Intent intent = new Intent(MainActivity.this,Blank.class);
            startActivity(intent);*/
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        generateView(photosToUseInSublevel);
                    }
                }, 500);
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        generateView(photosToUseInSublevel);
                    }
                }, 300);
            }
        }
/*
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                }
            }, 600);
*/



            //hideImages();
            System.out.println("Wygenerowano view");
    }

    boolean checkCorrectness() {
        if (wrongAnswersSublevel > level.getAmountOfAllowedTriesForEachEmotion()) {
            return false;
        }
        return true;
    }


    /*
        int selectEmotionToChoose(Level level){

            Random rand = new Random();

            int emotionIndexInList = rand.nextInt(level.emotions.size());

            return emotionIndexInList;
        }
    */
    String selectPhotoWithSelectedEmotion() {
        Random rand = new Random();

        int photoWithSelectedEmotionIndex = rand.nextInt(photosWithEmotionSelected.size());

        String name = photosWithEmotionSelected.get(photoWithSelectedEmotionIndex);

        return name;

        //BYŁO NAJZWYKLEJSZE:
    }

    void selectPhotoWithNotSelectedEmotions(int howMany) {

        Random rand = new Random();

        for (int i = 0; i < howMany - 1; i++) {
            if (photosWithRestOfEmotions.size() > 0) {
                int photoWithSelectedEmotionIndex = rand.nextInt(photosWithRestOfEmotions.size());
                String name = photosWithRestOfEmotions.get(photoWithSelectedEmotionIndex);

                photosToUseInSublevel.add(name);
                photosWithRestOfEmotions.remove(name);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                //int praises = level.getPraisesBinary();
                //System.out.println("praises: " + praises);
                startAnimationActivity();
                break;
            case 2:
                animationEnds = true;

                if (!findNextActiveLevel()) {

                    startEndActivity(true);
                } else {
                    generateView(photosToUseInSublevel);
                    System.out.println("Wygenerowano view");
                }


                break;
            case 3:


                java.util.Collections.shuffle(sublevelsList);
                if (!videos)
                    sublevelsLeft = level.getEmotions().size() * level.getSublevelsPerEachEmotion();
                else
                    sublevelsLeft = videoCursor.getCount();

                wrongAnswersSublevel = 0;
                rightAnswersSublevel = 0;
                timeoutSubLevel = 0;

                generateSublevel(sublevelsList.get(sublevelsLeft - 1));

                break;
        }
    }

    private void startHintActivity() {


        if (speaker == null) {
            speaker = Speaker.getInstance(MainActivity.this);
        }

        timer.cancel();
        selected_image_unzoom();
        String rightEmotion = goodAnswer.replace(".jpg", "").replaceAll("[0-9.]", "").replaceAll("_r_", "").replaceAll("_e_","");
        String emotion = getResources().getString(getResources().getIdentifier("emotion_" + rightEmotion, "string", getPackageName()));
        Intent intentHint = new Intent(MainActivity.this, RewardAndHintActivity.class);
        intentHint.putExtra("hintMode",true);
        intentHint.putExtra("emotion",emotion);
        System.out.println("INTENT EMOTION " + emotion);
        String photoName = "/" + getGoodAnswer().replace(".jpg", "");
        // System.out.println("photoName: " + photoName);


        ///ADDING A PHOTO TO AN INTENT - VERSION WITHOUT DRAWABLES

        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        final File path = new File(root + "FriendlyEmotions/Photos"+File.separator);
        String fileName = (path.toString() + photoName + ".jpg");

        intentHint.putExtra("fileName", fileName);
        startActivity(intentHint);
        clear_efects_on_all_images();
        selected_image_unzoom();


    }

    private void startRewardActivity() {
        if (speaker == null) {
            speaker = Speaker.getInstance(MainActivity.this);
        }

        Intent intentReward = new Intent(MainActivity.this, RewardAndHintActivity.class);

        String rightEmotion = goodAnswer.replace(".jpg", "").replaceAll("[0-9.]", "").replaceAll("_r_", "").replaceAll("_e_","");
        String emotion = getResources().getString(getResources().getIdentifier("emotion_" + rightEmotion, "string", getPackageName()));
        intentReward.putExtra("emotion", emotion);
        intentReward.putExtra("hintMode",false);
        String photoName = "/" + getGoodAnswer().replace(".jpg", "");
       // System.out.println("photoName: " + photoName);


        ///ADDING A PHOTO TO AN INTENT - VERSION WITHOUT DRAWABLES

        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        final File path = new File(root + "FriendlyEmotions/Photos"+File.separator);
        String fileName = (path.toString() + photoName + ".jpg");

        intentReward.putExtra("fileName", fileName);


        //DZIAŁA DLA DRAWABLES:
        int id = getResources().getIdentifier("pg.autyzm.graprzyjazneemocje:drawable/" + photoName, null, null);
        intentReward.putExtra("photoId", id);
        //System.out.println("photoId:" + id);


        ///WYPOWIADANE POCHWAŁY
        int praises = level.getPraisesBinary();
        System.out.println("praises: " + praises);
        intentReward.putExtra("praises", praises);


       /* String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        final File path = new File(root + "FriendlyEmotions/Photos" +File.separator);
        String photoLocation = path + "/" + getGoodAnswer();
       intentReward.putExtra("photoPath",photoLocation);


        System.out.println("photoName:" + photoName);
        System.out.println("photoPath: " + photoLocation);*/
        startActivityForResult(intentReward, 1);

    }

    public String getGoodAnswer() {
        return goodAnswer;
    }

    private void startAnimationActivity() {
        if (speaker == null) {
            speaker = Speaker.getInstance(MainActivity.this);
        }
        Intent intent = getIntent();
        int currentStrokeColor = intent.getIntExtra("color", 0);
        Intent i = new Intent(MainActivity.this, AnimationActivity.class);
        //i.putExtra("praises", level.getPraises());
        i.putExtra("color", currentStrokeColor);
        //System.out.println("MainActivity - color" + currentStrokeColor);
        startActivityForResult(i, 2);
    }


    private void startEndActivity(boolean pass) {
        Intent in = new Intent(this, EndActivity.class);
        in.putExtra("PASS", pass);
        in.putExtra("WRONG", wrongAnswers);
        in.putExtra("RIGHT", rightAnswers);
        in.putExtra("TIMEOUT", timeout);
        startActivityForResult(in, 2);
    }

    public void StartTimerForTest(final Level l) {
        //timer! seconds * 1000


        final TextView timeToAnswer = (TextView) findViewById(R.id.timeToAnswer);
        //timeToAnswer.setText(getString(R.string.timeToAnswer) + " " + l.getTimeLimitInTest() + "s / " + l.getTimeLimitInTest() + "s");
        timer = new CountDownTimer(l.getTimeLimitInTest() * 1000, 1000) {


            public void onTick(long millisUntilFinished) {
                //timeToAnswer.setText(getString(R.string.timeToAnswer) + " " + (millisUntilFinished / 1000) + "s / " + l.getTimeLimitInTest() + "s");

            }

            public void onFinish() {


                View badAnswer = new View(getApplicationContext());
                badAnswer.setId(0);
                onClickTestMode(badAnswer, true);
                speakerText = "Próba numer jeden";
                Speaker.getInstance(MainActivity.this).speak(speakerText);
                whichTry = 3;

            }
        }.start();
    }
    //********************
    public void image_grey_out(ImageView image , boolean set)
    {
        ColorMatrix matrix = new ColorMatrix();
        if (set)
            matrix.setSaturation((float) 0.1);
        else
            matrix.setSaturation((float) 1);


        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        image.setColorFilter(filter);
        //System.out.println("ustawienie szarości zdjęcia set: " + set);
    }

    public void image_frame(ImageView image , boolean set) {

        int border;
        if (set)  border = 15 ;
        else  {
            //setLayoutMargins(image,45/listSize,45/listSize,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
            border = 0;
        }

        image.setPadding(border,border,border,border);
        image.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        System.out.println("imageframe imageid:" + image.getId() +" set: " + set);
      /* try {Thread.sleep(2000);}
       catch (InterruptedException ex) {};*/



    }

    public void image_zoom(ImageView image)
    {
        Animation zooming;
        zooming = AnimationUtils.loadAnimation(this, R.anim.zoom);
        zooming.scaleCurrentDuration(1.05f);
        image.hasOverlappingRendering();
        setLayoutMargins(image,45/listSize + 10,45/listSize + 10,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
        image.startAnimation(zooming);

        image_selected = image;
        System.out.println("ANIAANIAANIA Robie zoom " + image.getId() + " data: " + new Date());



    }
    public void selected_image_unzoom()
    {
        Animation unzooming;

        System.out.println("WCHODZIMY W UNZOOM ANIAANIAANIA Robie zoom "+ new Date());
        if (image_selected != null) {
            setLayoutMargins(image_selected,45/listSize,45/listSize,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
            unzooming = AnimationUtils.loadAnimation(this, R.anim.unzoom);
            unzooming.scaleCurrentDuration(0);
            image_selected.startAnimation(unzooming);
            System.out.println("ANIAANIAANIA Robie UNZOOOOOOM " + image_selected.getId());


        }


    }


    //************

    public void clear_efects_on_all_images() {


        LinearLayout imagesLinear = (LinearLayout) findViewById(R.id.imageGallery);

        selected_image_unzoom();

        final int childcount = imagesLinear.getChildCount();
        for (int i = 0; i < childcount; i++) {
            ImageView image = (ImageView) imagesLinear.getChildAt(i);
            //setLayoutMargins(image,45/listSize,45/listSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
            //eeee zmienilam 1 na 0
            image_grey_out(image,false);
            image_frame(image, false);
        }




    }

    public void reorder_image()
    {

        long choose = Math.round(Math.random());

        if (choose == 0) {
            swapRight();
            System.out.println("swapping RIGHT");
        }
        if (choose == 1) {
            swapLeft();
            System.out.println("swapping LEFT");
        }

    }

    private void swapRight() {
        int size = photosToUseInSublevel.size();
        for (int i = 0; i < size-1; i++) {

            Collections.swap(photosToUseInSublevel,i,i+1);
            //System.out.println("!!!!!!!!!!PHOTOS SUBLEVEL: " + photosToUseInSublevel);
        }
        generateView(photosToUseInSublevel);
    }

    private void swapLeft() {
        int size = photosToUseInSublevel.size();
        for (int i = 1; i < size-1; i++) {

            Collections.swap(photosToUseInSublevel,i,i-1);
            //System.out.println("!!!!!!!!!!PHOTOS SUBLEVEL: " + photosToUseInSublevel);
        }
        generateView(photosToUseInSublevel);
    }



    private void startTimer(final Level l) {
        //timer! seconds * 1000
        if (l.getTimeLimit() != 1) {
            final int hintTypes = l.getHintTypesAsNumber();
            final Context currentContext = this;
            timer = new CountDownTimer(level.isTestMode() ? l.getTimeLimitInTest() * 1000 : l.getTimeLimit() * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {

                    LinearLayout imagesLinear = (LinearLayout) findViewById(R.id.imageGallery);

                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation((float) 0.1);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
if (whichTry != 3) {
    final int childcount = imagesLinear.getChildCount();
    for (int i = 0; i < childcount; i++) {
        ImageView image = (ImageView) imagesLinear.getChildAt(i);
        //eeee zmienilam 1 na 0
        if (l.isLearnMode()) {

            //FRAME,ENLARGE,MOVE,GREY_OUT
            if (image.getId() != 1) {
                if ((hintTypes & HintTypeValue(HintType.GREY_OUT)) == HintTypeValue(HintType.GREY_OUT)) {
                    image_grey_out(image, true);
                }
            } else {

                if ((hintTypes & HintTypeValue(HintType.FRAME)) == HintTypeValue(HintType.FRAME)) {
                    image_frame(image, true);
                }
                if ((hintTypes & HintTypeValue(HintType.MOVE)) == HintTypeValue(HintType.MOVE)) {
                    Animation shake = AnimationUtils.loadAnimation(currentContext, R.anim.shake);
                    image.startAnimation(shake);
                }
                if ((hintTypes & HintTypeValue(HintType.ENLARGE)) == HintTypeValue(HintType.ENLARGE)) {

                    //setLayoutMargins(image,15,15,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics()));
                                   /* if (photosToUseInSublevel.size() == 1) {
                                        lp = new LinearLayout.LayoutParams(420, 420);

                                        lp.setMargins(30,50,30,30);
                                    }*/

                    image_zoom(image);


                    // image.hasOverlappingRendering();


                }
            }

        }
    }
    timeout++;
    timeoutSubLevel++;
    whichTry = 3;
}
                }
            }.start();


        }
    }

    public enum HintType {
        FRAME,ENLARGE,MOVE,GREY_OUT
    }

    public enum CommandType {
        SHOW, SELECT, POINT, TOUCH, FIND
    }

    public int CommandTypeValue(CommandType command) {
        if (command == CommandType.SHOW)
            return 1;
        if (command == CommandType.SELECT)
            return 2;
        if (command == CommandType.POINT)
            return 2*2;
        if (command == CommandType.TOUCH)
            return 2*2*2;
        if (command == CommandType.FIND)
            return 2*2*2*2;
        return 1;
    }
    public int HintTypeValue(HintType hint) {
        if (hint == HintType.FRAME)
            return 1;
        if (hint == HintType.ENLARGE)
            return 2;
        if (hint == HintType.MOVE)
            return 2*2;
        if (hint == HintType.GREY_OUT)
            return 2*2*2;
        return 2*2*2;
    }

    public enum SubLevelMode {
        NO_WRONG_ANSWER,AFTER_WRONG_ANSWER,AFTER_WRONG_ANSWER_1_CORRECT,AFTER_WRONG_ANSWER_2_CORRECT
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    public void setLayoutMargins(ImageView image, int leftMargin, int rightMargin, int height)
    {
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 - (150 / listSize), getResources().getDisplayMetrics());

            lp = new LinearLayout.LayoutParams(height, height);
            lp.setMargins(leftMargin,10,rightMargin,margin);

       /* if (photosList.size() == 1) {
            lp = new LinearLayout.LayoutParams(410, 410);
            lp.setMargins(45,30,45,30);
        }*/
       lp.gravity =Gravity.CENTER;
            image.setLayoutParams(lp);
    }
}