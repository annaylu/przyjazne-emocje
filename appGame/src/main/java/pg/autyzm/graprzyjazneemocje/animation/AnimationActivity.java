package pg.autyzm.graprzyjazneemocje.animation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import java.util.Random;

import pg.autyzm.graprzyjazneemocje.R;
import pg.autyzm.graprzyjazneemocje.api.managers.AnimationBuilder;


/**
 * Created by joagi on 13.01.2018.
 */

public class AnimationActivity extends Activity implements Animation.AnimationListener {

    protected Animation anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        anim.setAnimationListener(this);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        Intent intent = getIntent();
        int currentStrokeColor = intent.getIntExtra("color",0);

        //System.out.println("AnimationActivity - color:" + currentStrokeColor);
        //System.out.println("AnimationActivity - colorRANDOM:" + color);

        RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.activity_menu);
        myLayout.setBackgroundColor(color);
    }

    protected void createView() {

        anim = new AnimationBuilder(this).prepareAndReturnRandomAward();

    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}
