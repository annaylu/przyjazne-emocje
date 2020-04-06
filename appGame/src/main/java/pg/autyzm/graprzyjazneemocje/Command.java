package pg.autyzm.graprzyjazneemocje;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Command extends Activity {

    Speaker speaker;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.background_center));

        TextView command = (TextView) findViewById(R.id.rightEmotion);

        Intent commandIntent = getIntent();
        String emotion = commandIntent.getStringExtra("emotion");

        command.setText(emotion);

        final String commandText = commandIntent.getStringExtra("command");
        speaker = Speaker.getInstance(Command.this);
        final ImageButton speakerButton = (ImageButton) findViewById(R.id.matchEmotionsSpeakerButton);
        speakerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                speaker.speak(commandText);
            }
        });

        new CountDownTimer(1500, 1) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {
                finish();
            }
        }.start();
    }
    }

