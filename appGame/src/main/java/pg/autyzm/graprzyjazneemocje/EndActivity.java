package pg.autyzm.graprzyjazneemocje;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pg.autyzm.graprzyjazneemocje.animation.AnimationEndActivity;

/**
 * Created by Ann on 12.11.2016.
 */
public class EndActivity extends Activity {
    public static int getRepeat() {
        return repeat;
    }

    public static void setRepeat(int repeat) {
        repeat = repeat;
    }

    private static int repeat = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        Bundle extras = getIntent().getExtras();
        boolean pass = extras.getBoolean("PASS");

        if (pass) {
            passLevel();
        } else {
            failLevel();
        }

//        txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100);

        int wrongAnswers = extras.getInt("WRONG");
        int rightAnswers = extras.getInt("RIGHT");
        int timeout = extras.getInt("TIMEOUT");
        TextView right = (TextView) findViewById(R.id.rightAnswers);
        right.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        right.setText(getResources().getString(R.string.label_rightAnswers) + " " + rightAnswers);
        TextView wrong = (TextView) findViewById(R.id.wrongAnswers);
        wrong.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        wrong.setText(getResources().getString(R.string.label_wrongAnswers) + " " + wrongAnswers);

        TextView time = (TextView) findViewById(R.id.timeout);
        time.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        time.setText(getResources().getString(R.string.label_timeout) + " " + timeout);

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
                Intent in = new Intent(EndActivity.this,MainActivity.class);
                startActivity(in);
            }


        });
    }

    private void passLevel() {
        Intent i = new Intent(this, AnimationEndActivity.class);
        startActivity(i);

        TextView txt = (TextView) findViewById(R.id.endTextMain);
        txt.setText(getResources().getString(R.string.label_congratulations));



    }

    private void failLevel(){
        ImageView sunImage = (ImageView) findViewById(R.id.sun_image_end);
        sunImage.setVisibility(View.VISIBLE);

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

    }
}
