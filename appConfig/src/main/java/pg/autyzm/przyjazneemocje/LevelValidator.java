package pg.autyzm.przyjazneemocje;

import android.widget.Toast;

import pg.autyzm.przyjazneemocje.configuration.LevelConfigurationActivity;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;
/**
 * Created by user on 26.08.2017.
 */

public class LevelValidator {

    Level validatedLevel;
    SqliteManager sqliteManager = SqliteManager.getAppContext();

    public LevelValidator(Level l) {
        validatedLevel = l;
    }




    boolean validateLevel(){

        // sprawdzenie dlugosci nazwy poziomu
        if(validatedLevel.getName().length() == 0 || validatedLevel.getName().length() > 50) {
            return false;
        }
        // prosta walidacja, czy w ogle zaznaczono jakies emocje (minimum dwa)/zdjecia
        if (validatedLevel.getEmotions().size() < 2 ) {
            //Toast.makeText("Wybierz conajmniej jedną emocję",Toast.LENGTH_LONG);
            return false;
        }
        if(everyEmotionHasAtLestOnePhoto()){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean everyEmotionHasAtLestOnePhoto(){


        /*for(int emotion : sqliteManager.givePhotosWithEmotion(emotion)){
            if (validatedLevel.getPhotosOrVideosIdList().size() < 1)
                return false;
        }
*/
        return true;

    }
}
