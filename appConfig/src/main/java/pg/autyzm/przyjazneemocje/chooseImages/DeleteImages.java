package pg.autyzm.przyjazneemocje.chooseImages;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;

public class DeleteImages extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SqliteManager sqlm = SqliteManager.getInstance(this);
        String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        Cursor cursor = sqlm.givePhotosWithEmotion(ChooseImages.getChoosenEmotion());

        String[] photosNameList = new File(root + "/FriendlyEmotions/Photos").list();
        if (cursor.getCount() < photosNameList.length) {
            for (final String fileName : photosNameList) {
                ImageButton button = (ImageButton) findViewById(R.id.delete_photo);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fileName.isEmpty();
                        System.out.println("sialalalalala");
                    }
                });
            }
        }

    }
}
