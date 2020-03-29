package pg.autyzm.graprzyjazneemocje;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    CountDownTimer timer;
    public Speaker speaker;

    public boolean isFirstWrong() {
        return firstWrong;
    }

    public void setFirstWrong(boolean firstWrong) {
        this.firstWrong = firstWrong;
    }

    private boolean firstWrong;

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
        System.out.println(cur0.getCount());
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
        setAttempt(0);
        setFirstWrong(false);
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

        java.util.Collections.shuffle(photosToUseInSublevel);

        // z tego co rozumiem w photosList powinny byc name wszystkich zdjec, jakie maja sie pojawic w lvl (czyli - 3 pozycje)

        if (level.isLearnMode()) {
            StartTimer(level);
        } else if (level.isTestMode()) {
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

    public void generateView(List<String> photosList) {


        System.out.println("goodAnswer przed cięciami: " + goodAnswer);

        String rightEmotion = goodAnswer.replace(".jpg", "").replaceAll("[0-9.]", "").replaceAll("_r_", "");

        if (!videos) {
            TextView txt = (TextView) findViewById(R.id.rightEmotion);

            System.out.println("rightEmotion: " + rightEmotion);
            System.out.println("goodAnswer po cięciach: " + goodAnswer);
            System.out.println();


            String rightEmotionLang = getResources().getString(getResources().getIdentifier("emotion_" + rightEmotion, "string", getPackageName()));
            System.out.println("rightEmotionLang: " + rightEmotionLang);

            System.out.println("levelQuestionType " + level.getQuestionType());
            System.out.println("ANIAAA EMOTION_NAME " + Level.Question.EMOTION_NAME);
if (level.getQuestionType() != Level.Question.EMOTION_NAME) {
    final int commandTypes = level.getCommandTypesAsNumber();
    System.out.println("commanDtypes: " + commandTypes);
    System.out.println("question type: " + level.getQuestionType());
    ArrayList<String> commandsSelected = new ArrayList<>();
    //checkbox: 8
    if (commandTypes == 8 || commandTypes == 9 || commandTypes == 10 || commandTypes == 11 || commandTypes == 12 || commandTypes == 13 || commandTypes == 14 || commandTypes == 15 || commandTypes == 24 || commandTypes == 25 || commandTypes == 26 || commandTypes == 28 || commandTypes == 31) {
        if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.touch_where));
        else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.touch));

    }
    //checkbox:1
    if (commandTypes == 1 || commandTypes == 3 || commandTypes == 5 || commandTypes == 7 || commandTypes == 9 || commandTypes == 11 || commandTypes == 13 || commandTypes == 15 || commandTypes == 19 || commandTypes == 25 || commandTypes == 31 || commandTypes == 29 || commandTypes == 31) {
        if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.show_where));
        else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.show));
    }

    //checkbox: 4
    if (commandTypes == 4 || commandTypes == 5 || commandTypes == 6 || commandTypes == 7 || commandTypes == 12 || commandTypes == 13 || commandTypes == 14 || commandTypes == 15 || commandTypes == 20 || commandTypes == 21 || commandTypes == 22 || commandTypes == 23 || commandTypes == 28 || commandTypes == 29 || commandTypes == 31) {
        if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.point_where));
        else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.point));
    }

    //checkbox:2
    if (commandTypes == 2 || commandTypes == 3 || commandTypes == 6 || commandTypes == 7 || commandTypes == 10 || commandTypes == 11 || commandTypes == 14 || commandTypes == 15 || commandTypes == 18 || commandTypes == 19 || commandTypes == 22 || commandTypes == 23 || commandTypes == 26 || commandTypes == 31) {
        if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.select_where));
        else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.select));

    }
    //checkbox: 16
    if (commandTypes == 16 || commandTypes == 17 || commandTypes == 18 || commandTypes == 19 || commandTypes == 20 || commandTypes == 21 || commandTypes == 22 || commandTypes == 23 || commandTypes == 25 || commandTypes == 26 || commandTypes == 28 || commandTypes == 29 || commandTypes == 30 || commandTypes == 31) {
        if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.find_where));
        else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME))
            commandsSelected.add(getResources().getString(R.string.find));

    }

    int size = commandsSelected.size();
    String[] commandsToChoose = new String[size];
    for (int i = 0; i < size; i++) {
        commandsToChoose[i] = commandsSelected.get(i);
    }


    System.out.println("anusia  size: " + size);
    System.out.println("commandsToChoose" + commandsToChoose.toString());
    System.out.println("commandsSelected: " + commandsSelected.toString());
    System.out.println("commands to choose length" + commandsToChoose.length);

    commandText = commandsToChoose[(int) Math.floor(Math.random() * (size))] + " " + rightEmotionLang;

}
            else
                commandText = rightEmotionLang;

            txt.setText(commandText);
        }

    LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.imageGallery);

        linearLayout1.removeAllViews();
    int listSize = photosList.size();

    int height;
    int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 - (150 / listSize), getResources().getDisplayMetrics());

        if(!videos)

    {
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 790 / listSize, getResources().getDisplayMetrics());
    } else

    {
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450 / listSize, getResources().getDisplayMetrics());
    }

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(height, height);
        lp.setMargins(45/listSize,10,45/listSize,margin);
    lp.gravity =Gravity.CENTER;
        for(
    String photoName :photosList)

    {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        File fileOut = new File(root + "FriendlyEmotions/Photos" + File.separator + photoName);
        try {
            ImageView image = new ImageView(MainActivity.this);
            image.setLayoutParams(lp);

            if (photoName.contains(rightEmotion)) {
                image.setId(1);
            } else {
                image.setId(0);
            }

            image.setOnClickListener(this);
            Bitmap captureBmp = Media.getBitmap(getContentResolver(), Uri.fromFile(fileOut));
            image.setImageBitmap(captureBmp);
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
        if (v.getId() == 1) {
            sublevelsLeft--;
            rightAnswers++;
            rightAnswersSublevel++;
            nextLevelOrEnd();
        } else {
            TextView numberOfTries = (TextView) findViewById(R.id.numberOfTries);
            wrongAnswers++;
            wrongAnswersSublevel++;
            if (endTimer) {
                timeout++;
                timeoutSubLevel++;
            }
            if (wrongAnswersSublevel >= level.getNumberOfTriesInTest()) {
                sublevelsLeft--;
                numberOfTries.setVisibility(View.INVISIBLE);
                nextLevelOrEnd();
            } else if (!endTimer) {
                numberOfTries.setVisibility(View.VISIBLE);
                numberOfTries.setText(getString(R.string.tries) + wrongAnswersSublevel + "/" + level.getNumberOfTriesInTest());
                Toast.makeText(getApplicationContext(), "Odpowiedź nieprawidłowa\nSpróbuj ponownie.", Toast.LENGTH_LONG).show();
                timer.start();
            } else {
                numberOfTries.setVisibility(View.VISIBLE);
                numberOfTries.setText(getString(R.string.tries) + wrongAnswersSublevel + "/" + level.getNumberOfTriesInTest());
                Toast.makeText(getApplicationContext(), "-Upłynął czas odpowiedzi.\n-Odpowiedź nieprawidłowa.\n-Spróbuj ponownie.", Toast.LENGTH_LONG).show();
                timer.start();
            }
        }
    }

    public void onClickLearnMode(View v) {

        if (v.getId() == 1) {

                sublevelsLeft--;
            if (getAttempt() == 0) {
                rightAnswers++;
                setAttempt(1);
            }
            rightAnswersSublevel++;

            timer.cancel();

            boolean correctness = true;

            if (sublevelsLeft == 0) {
                correctness = checkCorrectness();
            }

            if (correctness && level.isLearnMode()) {
                startRewardActivity();

            } else {
                startEndActivity(false);
            }

        } else { //jesli nie wybrano wlasciwej
            //wczesniej było z IFem i również liczyło po jednej nieprawidłowej, teraz będzie liczyć każdą nieprawidłową
                setAttempt(1);
                setFirstWrong(true);

            wrongAnswers++;
            wrongAnswersSublevel++;

        }
    }

    void nextLevelOrEnd() {
        timer.cancel();
        if (!findNextActiveLevel()) {
            startEndActivity(true);
        } else {
            generateView(photosToUseInSublevel);
            System.out.println("Wygenerowano view");
        }
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

    private void startRewardActivity() {
        if (speaker == null) {
            speaker = Speaker.getInstance(MainActivity.this);
        }

        Intent intentReward = new Intent(MainActivity.this, RewardActivity.class);
        if (level.getQuestionType().equals(Level.Question.SHOW_WHERE_IS_EMOTION_NAME))
            commandText = getResources().getString(R.string.reward_complex);
        else if (level.getQuestionType().equals(Level.Question.SHOW_EMOTION_NAME))
            commandText = ", ";
        else if (level.getQuestionType().equals(Level.Question.EMOTION_NAME))
            commandText = ", ";

        intentReward.putExtra("praise", commandText);

        String rightEmotion = goodAnswer.replace(".jpg", "").replaceAll("[0-9.]", "").replaceAll("_r_", "");
        String emotion = getResources().getString(getResources().getIdentifier("emotion_" + rightEmotion, "string", getPackageName()));
        intentReward.putExtra("emotion", emotion);
        String photoName = getGoodAnswer().replace(".jpg", "");
        System.out.println("photoName: " + photoName);


        ///ADDING A PHOTO TO AN INTENT - VERSION WITHOUT DRAWABLES

        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        final File path = new File(root + "FriendlyEmotions/Photos" + File.separator);


        String fileName = (path + photoName + ".jpg");

        intentReward.putExtra("fileName", fileName);


        //DZIAŁA DLA DRAWABLES:
        int id = getResources().getIdentifier("pg.autyzm.graprzyjazneemocje:drawable/" + photoName, null, null);
        intentReward.putExtra("photoId", id);
        System.out.println("photoId:" + id);


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
        System.out.println("MainActivity - color" + currentStrokeColor);
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

    private void StartTimerForTest(final Level l) {
        //timer! seconds * 1000
        final TextView timeToAnswer = (TextView) findViewById(R.id.timeToAnswer);
        timeToAnswer.setText(getString(R.string.timeToAnswer) + " " + l.getTimeLimitInTest() + "s / " + l.getTimeLimitInTest() + "s");
        timer = new CountDownTimer(l.getTimeLimitInTest() * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeToAnswer.setText(getString(R.string.timeToAnswer) + " " + (millisUntilFinished / 1000) + "s / " + l.getTimeLimitInTest() + "s");
            }

            public void onFinish() {
                View badAnswer = new View(getApplicationContext());
                badAnswer.setId(0);
                onClickTestMode(badAnswer, true);
            }
        }.start();
    }

    private void StartTimer(final Level l) {
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

                    final int childcount = imagesLinear.getChildCount();
                    for (int i = 0; i < childcount; i++) {
                        ImageView image = (ImageView) imagesLinear.getChildAt(i);
                        //eeee zmienilam 1 na 0
                        if (l.isLearnMode()) {
                            if (image.getId() != 1) {
                                if (hintTypes == 8 || hintTypes == 9 || hintTypes == 10 || hintTypes == 11 || hintTypes == 12 || hintTypes == 13 || hintTypes == 15) {
                                    image.setColorFilter(filter);
                                }
                            } else {

                                if (hintTypes == 1 || hintTypes == 3 || hintTypes == 5 || hintTypes == 9 || hintTypes == 11 || hintTypes == 13 || hintTypes == 15) {
                                    image.setPadding(20, 20, 20, 20);
                                    image.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                }
                                if (hintTypes == 4 || hintTypes == 5 || hintTypes == 6 || hintTypes == 7 || hintTypes == 12 || hintTypes == 13 || hintTypes == 15) {
                                    Animation shake = AnimationUtils.loadAnimation(currentContext, R.anim.shake);
                                    image.startAnimation(shake);
                                }
                                if (hintTypes == 2 || hintTypes == 3 || hintTypes == 6 || hintTypes == 7 || hintTypes == 11 || hintTypes == 14 || hintTypes == 15) {
                                    Animation zooming = AnimationUtils.loadAnimation(currentContext, R.anim.zoom);
                                    zooming.scaleCurrentDuration(1.05f);
                                    image.startAnimation(zooming);
                                    LinearLayout imageGallery = (LinearLayout) findViewById(R.id.imageGallery);


                                    //imageGallery.generateLayoutParams(5,, getResources().getDisplayMetrics()),5,)

                                }
                            }

                        }
                    }
                    timeout++;
                    timeoutSubLevel++;
                }
            }.start();

        }
    }
}
