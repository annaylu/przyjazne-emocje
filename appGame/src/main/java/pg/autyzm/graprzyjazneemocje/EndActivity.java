package pg.autyzm.graprzyjazneemocje;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pg.autyzm.graprzyjazneemocje.animation.AnimationEndActivity;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

/**
 * Created by Ann on 12.11.2016.
 */
public class EndActivity extends Activity {
    public static int getRepeat() {
        return repeat;
    }

    Level level = new Level();

    public static void setRepeat(int repeat) {
        repeat = repeat;
    }

    private static int repeat = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_end);

        System.out.println("LEARN MODE " + level.isLearnMode());
        System.out.println("TEST MODE " + level.isTestMode());


        Bundle extras = getIntent().getExtras();
        boolean pass = extras.getBoolean("PASS");
/*if (!level.isLearnMode()) {
    if (pass) {
        passLevel();
    } else {
        failLevel();
    }
}*/

//        txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100);

        TextView txt = (TextView) findViewById(R.id.endTextMain);
        txt.setText(getResources().getString(R.string.label_congratulations));

        int wrongAnswers = extras.getInt("WRONG");
        int rightAnswers = extras.getInt("RIGHT");
        int timeout = extras.getInt("TIMEOUT");
        TextView right = (TextView) findViewById(R.id.rightAnswers);
        right.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        right.setText(getResources().getString(R.string.label_rightAnswers) + " " );
        TextView rightNumber = (TextView) findViewById(R.id.rightAnswersNumber);
        rightNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        rightNumber.setText(" " + rightAnswers);
        TextView wrong = (TextView) findViewById(R.id.wrongAnswers);
        wrong.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        wrong.setText(getResources().getString(R.string.label_wrongAnswers) + " " );
        TextView wrongNumber = (TextView) findViewById(R.id.wrongAnswersNumber);
        wrongNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        wrongNumber.setText(" " + wrongAnswers);



        TextView time = (TextView) findViewById(R.id.timeout);
        time.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        time.setText(getResources().getString(R.string.label_timeout) + " ");
        TextView timeNumber = (TextView) findViewById(R.id.timeOutsNumber);
        timeNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        timeNumber.setText(" " + timeout);

        TextView statistics = (TextView) findViewById(R.id.statistics);
        statistics.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        statistics.setText(getResources().getString(R.string.procencik) + " ");
        TextView statisticsNumber = (TextView) findViewById(R.id.statisticsNumber);
        statisticsNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
       // int result = rightAnswers / wrongAnswers * 100;
       // String.format()
        if (wrongAnswers != 0) {
            double result = ( (rightAnswers * 100)/ (double) (rightAnswers +wrongAnswers));
            //System.out.println(result);
            statisticsNumber.setText(" " + String.format("%.1f ",result) + "%");
            //System.out.println(String.format("%f ",result) + "%");
            //todo czemu zaokrągla??? co tu sie dzieje
            //System.out.println("STAAAAAAAATS" + rightAnswers / wrongAnswers * 100);
        }
        else
            statisticsNumber.setText("100%");


        repeat = 0;

        Button prevButton = (Button) findViewById((R.id.prevButton));
        prevButton.setBackgroundResource(R.drawable.prev_button_text);
        prevButton.setText(getResources().getString(R.string.prevButton));
        prevButton.setVisibility(View.VISIBLE);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatLevel(view);

            }
        });

        Button mainMenuButton = (Button) findViewById((R.id.mainMenuButton));
        mainMenuButton.setBackgroundResource(R.drawable.prev_button_text);
        mainMenuButton.setText(getResources().getString(R.string.mainMenuButton));
        mainMenuButton.setVisibility(View.VISIBLE);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pg.autyzm.przyjazneemocje");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(EndActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
                    //Intent in = new Intent(EndActivity.this,pg.autyzm.przyjazneemocje.MainActivity.class);


                }
                /*Intent in = new Intent(EndActivity.this,MainActivity.class);
                startActivity(in);*/
            }


        });
    }

    private void passLevel() {
        MediaPlayer ring= MediaPlayer.create(EndActivity.this,R.raw.fanfare3);
        ring.start();

        if (!level.isTestMode()) {
            Intent i = new Intent(this, AnimationEndActivity.class);
            startActivity(i);
        }





    }

    private void failLevel(){
        if (!level.isTestMode()) {
            //NIE DZIAŁA!!!!!!!!!!!!
            ImageView sunImage = (ImageView) findViewById(R.id.sun_image_end);
            sunImage.setVisibility(View.VISIBLE);
            MediaPlayer ring= MediaPlayer.create(EndActivity.this,R.raw.fanfare1);
            ring.start();
        }

        TextView txt = (TextView) findViewById(R.id.endTextMain);
        txt.setText(getResources().getString(R.string.failed_level));
    }


    private void repeatLevel(View view) {
        finish();
        Intent intent = new Intent(EndActivity.this,MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pg.autyzm.przyjazneemocje");
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(EndActivity.this, "There is no package available in android", Toast.LENGTH_LONG).show();
            //Intent in = new Intent(EndActivity.this,pg.autyzm.przyjazneemocje.MainActivity.class);


        }
        Intent in = new Intent(EndActivity.this,MainActivity.class);
        startActivity(in);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);

    }


}
