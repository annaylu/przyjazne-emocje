package pg.autyzm.graprzyjazneemocje;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RewardAndHintActivity extends Activity {
    private int chosenColor = generateRandomColor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        LinearLayout rewardAndHintLayout = (LinearLayout) findViewById(R.id.activity_reward);
        rewardAndHintLayout.setBackgroundColor(chosenColor);
        int time = 3000;

        Intent intentReward = getIntent();
        Intent intentHint = getIntent();
        String fileName = intentReward.getStringExtra("fileName");
        String emotion = intentReward.getStringExtra("emotion");

        boolean hintMode = intentHint.getBooleanExtra("hintMode", true);

        int praiseBinary = intentReward.getIntExtra("praises", 0);


       // int whichTry = intent.getIntExtra("whichTry",1);
        List<String> praiseList = new ArrayList<String>();
        String[] praiseTab = getPraises(praiseBinary);
        for (String element : praiseTab) {
            if (element != null && element.length() > 0) {
                praiseList.add(element);
            }
        }

        int max = praiseList.size();
        int position = (int) Math.floor(Math.random() * max);

        String commandPraise = "";
        if (praiseList.size() > 0) {
            commandPraise = praiseList.get(position);
        }

        String speakerText = commandPraise +"! "+ emotion;

        if (hintMode) {
            rewardAndHintLayout.setBackgroundColor(getResources().getColor(R.color.background_center));
            fileName = intentHint.getStringExtra("fileName");
            System.out.println("FILENAME " + fileName);
            emotion = intentHint.getStringExtra("emotion");
            speakerText = emotion;
            System.out.println("INNER EMOTION " + emotion);
            time = 2000;
        }
        System.out.println("EMOTION " + emotion);
        System.out.println("SPEAKER TEXT " + speakerText);

        File rewardOrHintFile = new File(fileName);

        Bitmap bitmap = BitmapFactory.decodeFile(rewardOrHintFile.getAbsolutePath());

        TextView rewardOrHintText = (TextView) findViewById(R.id.rewardOrHintText);
        ImageView correctPhoto = (ImageView) findViewById(R.id.correctAnswer);


        rewardOrHintText.setText(emotion);
        Animation zoom = AnimationUtils.loadAnimation(RewardAndHintActivity.this, R.anim.zoom);
        correctPhoto.setImageBitmap(bitmap);
        if (!hintMode) correctPhoto.startAnimation(zoom);
        Speaker.getInstance(RewardAndHintActivity.this).speak(speakerText);
        hintMode = false;
        new CountDownTimer(time, 1) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {
                finish();
            /*    Intent intent = getIntent();
                int whichTry = intent.getIntExtra("whichTry",1);
                Speaker.getInstance(RewardAndHintActivity.this).speak("Próbeczka numer " + whichTry);*/

            }
        }.start();


    }

    public String[] getPraises(int praiseBinary) {
        String[] praises = getResources().getStringArray(R.array.praise_array);
        String[] result = new String[5];
        //String[] praises = dictionary.getAllPraises();
        int praisePositionBinary = 1;
        //int praiseBinary = getLevel().getPraisesBinary();
        int i = 0;
        for (String praise : praises) {
            if ((praisePositionBinary & praiseBinary) == praisePositionBinary) {
                result[i] = praise;
                i++;
            }
            praisePositionBinary++;


        }

        return result;

    }

    @Override
    public void finish() {
        super.finish();


    }

    public int generateRandomColor() {
        // This is the base color which will be mixed with the generated one
        final Random mRandom = new Random(System.currentTimeMillis());

        final int baseColor = Color.WHITE;
        Random rnd = new Random();

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + mRandom.nextInt(256)) / 2;
        final int green = (baseGreen + mRandom.nextInt(256)) / 2;
        final int blue = (baseBlue + mRandom.nextInt(256)) / 2;


        return Color.rgb(red, green, blue);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

}