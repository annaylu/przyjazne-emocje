package pg.autyzm.graprzyjazneemocje;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pg.autyzm.przyjazneemocje.lib.entities.Dictionary;

public class RewardActivity extends Activity {

    public int chosenColor = generateRandomColor();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);






        LinearLayout rewardLayout = (LinearLayout) findViewById(R.id.activity_reward);


            rewardLayout.setBackgroundColor(chosenColor);
        System.out.println("chosen color" + chosenColor);


Intent intentReward = getIntent();
        final String commandType = intentReward.getStringExtra("praise");
//intentReward.getStringExtra("praises");
//String commandPraise =
int praiseBinary = intentReward.getIntExtra("praises",0);
        //String[] praiseTab = new String[10];
        //praiseTab = praiseString.split(";");

        List<String> praiseList = new ArrayList<String>();
String[] praiseTab = getPraises(praiseBinary);
        for(String element : praiseTab) {
            if(element != null && element.length() > 0) {
                praiseList.add(element);
            }
        }

        //praiseTab = praiseList.toArray(new String[praiseList.size()]);
        System.out.print("kukukukukuk praiseList " + praiseList);
        System.out.print("praiseTab " + praiseTab);
        System.out.println("praiseString " + praiseBinary);



        int max = praiseList.size();
        System.out.println("max: " + max);
        int position =  (int) Math.floor(Math.random()*max);

        System.out.println("position: " + position);
        System.out.println("praiseList size: " + praiseList.size());

    String commandPraise = "";
        if (praiseList.size() > 0) {
            commandPraise = praiseList.get(position);
        }
        System.out.println("commandPraise: " + commandPraise + commandType);

String photo = intentReward.getStringExtra("photo");
String fileName = intentReward.getStringExtra("fileName");
        //DZIAŁA DLA DRAWABLE:
       final int photoId = intentReward.getIntExtra("photoId",1);
       int fileId= getResources().getIdentifier("pg.autyzm.graprzyjazneemocje:drawable/" + fileName, null, null);
        System.out.println("fileName:" + fileName);
final String emotion = intentReward.getStringExtra("emotion");

//String photoLocation = intentReward.getStringExtra("photoPath");

        //System.out.println("Reward photoPath" + photoLocation);
       //int photoId = getResources().getIdentifier(photoLocation, null, null);
       // System.out.println("Reward photoId: " + photoId);
        final TextView praise = (TextView) findViewById(R.id.commandPraise);
//String praiseString =

        ////praise.setText( commandPraise+ " " + emotion); <- to ma być potem mówione
        final ImageView correctPhoto = (ImageView) findViewById(R.id.correctAnswer);


//Drawable drawable = (Drawable) ("R.drawable." + photo);



        //ScaleAnimation correctAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //correctPhoto.setAnimation(correctAnimation);

        //correctPhoto.animate().setDuration(3000);
        //correctPhoto.animate().translationX(1000).setDuration(500);
        //correctPhoto.animate().translationY(200).setDuration(2000);
        //correctPhoto.animate().scaleX(2).setDuration(2000);
        //correctPhoto.animate().scaleY(2).setDuration(2000);


        //correctPhoto.setImageResource(photoId);

        //correctPhoto.animate().setDuration(3000);
        //correctPhoto.animate().alpha(0.5f).setDuration(2000);

        //TO NATOMIAST MA BYĆ MÓWIONE    --->    commandPraise




       /* Button przejdzDoAnimacji = (Button) findViewById(R.id.buttonReward);
        przejdzDoAnimacji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RewardActivity.this, MainActivity.class);
                intent.putExtra("color",chosenColor);
                System.out.println("chosen color2" + chosenColor);
                System.out.println("Reward Activity - color" + chosenColor);
                setResult(1,intent);
                finish();
            }
        });*/

        Speaker.getInstance(RewardActivity.this).speak(commandPraise + commandType + emotion);
        Animation zoom = AnimationUtils.loadAnimation(RewardActivity.this, R.anim.zoom);
        correctPhoto.setImageResource(photoId);
        correctPhoto.startAnimation(zoom);

        new CountDownTimer(3000,1) {

            public void onTick(long millisUntilFinished) {

                praise.setText( emotion);
            }

            public void onFinish() {
                Intent intent = new Intent(RewardActivity.this, MainActivity.class);
                intent.putExtra("color",chosenColor);
                System.out.println("chosen color2" + chosenColor);
                System.out.println("Reward Activity - color" + chosenColor);
                setResult(1,intent);
                finish();
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
        for (String praise: praises) {
            if ((praisePositionBinary & praiseBinary) == praisePositionBinary) {
                result[i] = praise;
                i++;
            }
            praisePositionBinary++;


        }

        return  result;

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

/*    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {


    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }*/
}
