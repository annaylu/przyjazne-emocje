package pg.autyzm.przyjazneemocje.lib;

/**
 * Created by Ann on 26.09.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import pg.autyzm.przyjazneemocje.lib.entities.Level;

public class SqliteManager extends SQLiteOpenHelper {

    private static SqliteManager appContext;
    private static SqliteManager sInstance;

    private static final String DATABASE_NAME = "przyjazneemocje";


    private SQLiteDatabase db;

    private SqliteManager(final Context context) {
        super(new DatabaseContext(context), DATABASE_NAME, null, 18);
        db = getWritableDatabase();
    }

    public static SqliteManager getAppContext() {
        return appContext;
    }

    public static synchronized SqliteManager getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SqliteManager(context.getApplicationContext());
        }
        return sInstance;

    }

    public void onCreate(SQLiteDatabase db) {

        this.db = db;


        createTablesInDatabase();
        addEmotionsToDatabase();
        addDefaultLevels();

        addLang(1, "pl", 1);
        addLang(2, "en", 0);

        Level level_easy_icons = new Level();
        level_easy_icons.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_easy_icons.setName("IKONY - 2 emocje:: ICONS - 2 emotions");
        level_easy_icons.setTimeLimit(10);
        ///przed tem   -   level_easy_icons.setPraises("dobrze");
       //próbuję   ---
        level_easy_icons.addPraise("dobrze");
        level_easy_icons.addPraise("super");
        level_easy_icons.setAmountOfAllowedTriesForEachEmotion(3);
        level_easy_icons.setSublevelsPerEachEmotion(2);
        level_easy_icons.setPhotosOrVideosShowedForOneQuestion(3);
        level_easy_icons.setForTests(true);
        level_easy_icons.setShouldQuestionBeReadAloud(false);
        level_easy_icons.setQuestionType(Level.Question.EMOTION_NAME);


        level_easy_icons.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        });
        level_easy_icons.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(9);
                add(10);
                add(11);
                add(16);
                add(14);
                add(15);
            }
        });

        //ZlecenieIT
        level_easy_icons.setLearnMode(true);
        level_easy_icons.setTestMode(false);
        level_easy_icons.setNumberOfTriesInTest(3);
        level_easy_icons.setMaterialForTest(true);
        level_easy_icons.setTimeLimitInTest(10);

        saveLevelToDatabase(level_easy_icons);


        Level level_easy_photos = new Level();
        level_easy_photos.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_easy_photos.setName("ZDJĘCIA - 2 emocje::Photos - 2 emotions");
        level_easy_photos.setTimeLimit(10);
        level_easy_photos.setPraises("dobrze");
        level_easy_photos.setAmountOfAllowedTriesForEachEmotion(3);
        level_easy_photos.setSublevelsPerEachEmotion(2);
        level_easy_photos.setPhotosOrVideosShowedForOneQuestion(3);
        level_easy_photos.setForTests(true);
        level_easy_photos.setShouldQuestionBeReadAloud(true);
        level_easy_photos.setQuestionType(Level.Question.EMOTION_NAME);

        level_easy_photos.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        });
        level_easy_photos.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(7);
                add(6);
                add(8);
                add(12);
                add(13);
            }
        });

        //ZlecenieIT
        level_easy_photos.setLearnMode(false);
        level_easy_photos.setTestMode(false);
        level_easy_photos.setNumberOfTriesInTest(3);
        level_easy_photos.setTimeLimitInTest(10);
        level_easy_photos.setMaterialForTest(true);

        saveLevelToDatabase(level_easy_photos);

        Level level_medium = new Level();
        level_medium.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_medium.setName("ZDJĘCIA - 4 emocje::Photos - 4 emotions");
        level_medium.setTimeLimit(10);
        level_medium.setPraises("dobrze");
        level_medium.setPraises("wspaniale");
        level_medium.setPraises("świetnie");
        level_medium.setAmountOfAllowedTriesForEachEmotion(3);
        level_medium.setPhotosOrVideosShowedForOneQuestion(3);
        level_medium.setSublevelsPerEachEmotion(2);
        level_medium.setForTests(true);
        level_medium.setShouldQuestionBeReadAloud(true);
        level_medium.setQuestionType(Level.Question.SHOW_EMOTION_NAME);
        level_medium.setAmountOfEmotions(Integer.toString(level_medium.getEmotions().size()));

        level_medium.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(4);
                add(3);
            }
        });
        level_medium.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(7);
                add(6);
                add(8);
                add(12);
                add(13);
                add(1);
                add(2);
                add(3);
                add(17);
                add(18);
            }
        });

        //ZlecenieIT
        level_medium.setLearnMode(false);
        level_medium.setTestMode(false);
        level_medium.setNumberOfTriesInTest(3);
        level_medium.setTimeLimitInTest(10);
        level_medium.setMaterialForTest(true);

        saveLevelToDatabase(level_medium);


        Level level_difficult = new Level();
        level_difficult.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_difficult.setName("ZDJĘCIA - 6 emocji::Photos - 6 emotions");
       /* if (getCurrentLang() == "en")
            level_difficult.setName("PHOTOS - 6 emotions");
        else if (getCurrentLang() == "pl")
            level_difficult.setName("PHOTOS - 6 emotions pl");
        else if (getCurrentLang() == "1")
            level_difficult.setName("PHOTOS - 6 emotions 1");
        else if (getCurrentLang() == "0")
            level_difficult.setName("PHOTOS - 6 emotions 0");
        else if (getCurrentLang().equals("2"))
            level_difficult.setName("PHOTOS - 6 emotions 2");
        else if (getCurrentLang().equals("0"))
            level_difficult.setName("PHOTOS - 6 emotions 2");
        else if (getCurrentLang().equals("1"))
            level_difficult.setName("PHOTOS - 6 emotions 2");
        else if (getCurrentLang().equals("en"))
            level_difficult.setName("PHOTOS - 6 emotions 2");
        else
            level_difficult.setName("ZDJĘCIA - 6 emocji");*/

        level_difficult.setTimeLimit(10);
        level_difficult.setPraises("dobrze");
        level_difficult.setPraises("good"); //chyba nie działa, trzebaby wszystko pousuwać i sprbować jeszcze raz
        level_difficult.setAmountOfAllowedTriesForEachEmotion(3);
        level_difficult.setSublevelsPerEachEmotion(2);
        level_difficult.setPhotosOrVideosShowedForOneQuestion(3);
        level_difficult.setForTests(true);
        level_difficult.setShouldQuestionBeReadAloud(true);
        level_difficult.setQuestionType(Level.Question.SHOW_WHERE_IS_EMOTION_NAME);
        //level_difficult.setAmountOfEmotions("6");
        level_difficult.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(2);
                add(3);
                add(4);
                add(5);
            }
        });
        level_difficult.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(7);
                add(6);
                add(8);
                add(12);
                add(13);
                add(1);
                add(2);
                add(3);
                add(17);
                add(18);
                add(4);
                add(5);
                add(19);
            }
        });

        //trudne
        level_difficult.setLearnMode(false);
        level_difficult.setTestMode(false);
        level_difficult.setNumberOfTriesInTest(3);
        level_difficult.setTimeLimitInTest(10);
        level_difficult.setMaterialForTest(true);

        saveLevelToDatabase(level_difficult);

    }


    public void onOpen(SQLiteDatabase db){

        this.db = db;

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.db = db;

        deleteTablesFromDatabase();
        createTablesInDatabase();
        addEmotionsToDatabase();
    }

    public void addEmotion(String emotion)
    {
        ContentValues values = new ContentValues();
        values.put("emotion",emotion);
        System.out.println(db.insertOrThrow("emotions", null, values) + " addEmotion");
    }

    public void addPhoto(int path, String emotion, String fileName)
    {
        ContentValues values = new ContentValues();
        values.put("path",path);
        values.put("emotion",emotion);
        values.put("name",fileName);
        db.insertOrThrow("photos", null, values);
    }

    public void addVideo(int path, String emotion, String fileName)
    {
        ContentValues values = new ContentValues();
        values.put("path",path);
        values.put("emotion",emotion);
        values.put("name",fileName);
        db.insertOrThrow("videos", null, values);
    }

    public void addLang(int id, String lang, Integer selected) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("language", lang);
        values.put("selected", selected);
        db.insertOrThrow("language", null, values);
    }


    public void saveLevelToDatabase(Level level)
    {
        ContentValues values = new ContentValues();
        values.put("photos_or_videos", level.getPhotosOrVideosFlag());
        values.put("name", level.getName());
        values.put("photos_or_videos_per_level", level.getPhotosOrVideosShowedForOneQuestion());
        values.put("time_limit", level.getTimeLimit());
        values.put("correctness", level.getAmountOfAllowedTriesForEachEmotion());
        values.put("sublevels_per_each_emotion", level.getSublevelsPerEachEmotion());
        values.put("is_for_tests", level.isForTests());
        values.put("question_type", level.getQuestionType().toString());
        values.put("hint_types_as_number", level.getHintTypesAsNumber());
        values.put("praises", level.getPraises());
        values.put("shouldQuestionBeReadAloud", level.isShouldQuestionBeReadAloud());

        //ZlecenieIT
        values.put("is_learn_mode", level.isLearnMode());
        values.put("is_test_mode", level.isTestMode());
        values.put("material_for_test", level.isMaterialForTest());
        values.put("number_of_tries_in_test", level.getNumberOfTriesInTest());
        values.put("time_limit_in_test", level.getTimeLimitInTest());

        if(level.getId() != 0) {
            db.update("levels", values, "id=" + level.getId(), null);

            //usunac wszystkie rekordy polaczone z tym poziomem
            delete("levels_photos", "levelid", new String[]{String.valueOf(level.getId())});
            delete("levels_emotions", "levelid", new String[]{String.valueOf(level.getId())});
        } else {
            long longId = db.insertOrThrow("levels", null, values);
            level.setId((int) longId);
        }

        updatePhotosAndEmotions(level.getId(), level.getPhotosOrVideosIdList(), level.getEmotions(), values, false);

        if(level.isMaterialForTest()){
            level.setPhotosOrVideosIdListInTest(level.getPhotosOrVideosIdList());
            level.setEmotionsInTest(level.getEmotions());
        }
        updatePhotosAndEmotions(level.getId(), level.getPhotosOrVideosIdListInTest(), level.getEmotionsInTest(), values, true);
    }

    public void updatePhotosAndEmotions(int levelId, List<Integer> photosOrVideosList, List<Integer> emotions, ContentValues values, boolean forTestMode){
        // Dodaj rekordy wiele do wielu ze zdjeciami/video dla trybu test
        for(Integer photoOrVideo : photosOrVideosList){
            values = new ContentValues();
            values.put("levelid", levelId);
            values.put("material_for_test", forTestMode);
            values.put("photoid",photoOrVideo);
            db.insertOrThrow("levels_photos", null, values);
        }

        for(Integer emotion : emotions){
            values = new ContentValues();
            values.put("levelid", levelId);
            values.put("material_for_test", forTestMode);
            values.put("emotionid",emotion + 1);
            db.insertOrThrow("levels_emotions", null, values);
        }
    }

    public void delete(String tableName, String columnName, String[] value)
    {
        db.delete(tableName, columnName + "=?",value);
    }

    public void delete(String tableName, String columnName, String value)
    {
        String[] args = {"" + value};
        db.delete(tableName, columnName + "=?",args);
    }

    public void cleanTable(String tableName)
    {
        db.execSQL("delete from "+ tableName);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='" + tableName +"'");
    }

    public Cursor givePhotosWithEmotion(String emotion)
    {
        String[] columns = {"id", "path", "emotion", "name"};
        Cursor cursor = db.query("photos", columns,"emotion like " + "'%" + emotion + "%'", null, null, null, null);
        return cursor;
    }

    public Cursor giveVideosWithEmotion(String emotion)
    {
        String[] columns = {"id", "path", "emotion", "name"};
        Cursor cursor = db.query("videos", columns,"emotion like " + "'%" + emotion + "%'", null, null, null, null);
        return cursor;
    }

    public Cursor givePhotoWithPath(String path)
    {
        String[] columns = {"id", "path", "emotion", "name"};
        Cursor cursor = db.query("photos", columns,"path like " + "'%" + path + "%'", null, null, null, null);
        return cursor;
    }

    public Cursor givePhotoWithId(int id)
    {
        String[] columns = {"id", "path", "emotion", "name"};
        Cursor cursor = db.query("photos", columns,"id like " + "'%" + id + "%'", null, null, null, null);
        return cursor;
    }

    public Cursor givePhotosInLevel(int levelId)
    {
        String[] columns = {"id", "levelid", "material_for_test", "photoid"};
        Cursor cursor = db.query("levels_photos", columns,"levelid like " + "'%" + levelId + "%'", null, null, null, null);
        return cursor;
    }

    public Cursor giveAllVideos() //TODO: Change to "giveVideosInLevel(int levelId)
    {
        String[] columns = {"id", "emotion", "name"};
        Cursor cursor = db.query("videos", columns, null, null, null, null, null);
        return cursor;
    }


    public Cursor giveEmotionsInLevel(int levelId)
    {
        String[] columns = {"id", "levelid", "material_for_test", "emotionid"};
        Cursor cursor = db.query("levels_emotions", columns,"levelid like " + "'%" + levelId + "%'", null, null, null, null);
        return cursor;
    }


    public Cursor giveEmotionId(String name){

        String[] columns = {"id", "emotion"};
        Cursor cursor = db.query("emotions", columns,"emotion like " + "'%" + name + "%'", null, null, null, null);
        return cursor;

    }


    public Cursor giveEmotionName(int id){

        String[] columns = {"id", "emotion"};
        Cursor cursor = db.query("emotions", columns,"id like " + "'%" + id + "%'", null, null, null, null);
        return cursor;

    }

    public Cursor giveAllEmotions()
    {
        Cursor cursor =  db.rawQuery("select * from emotions", null);
        return cursor;
    }

    public Cursor giveAllLevels()
    {
        Cursor cursor =  db.rawQuery("select * from levels", null);
        return cursor;
    }

    public Cursor giveLevel(int id)
    {
        Cursor cursor =  db.rawQuery("select * from levels where id='" + id + "'" , null);
        return cursor;
    }


    public String giveNameOfEmotionFromPhoto(String nameOfPhoto)
    {
        String[] columns = {"id", "path", "emotion", "name"};
        Cursor cursor = db.query("photos", columns,null, null, null, null, null);
        while(cursor.moveToNext()) {
            String name = cursor.getString(3);
            if(name.equals(nameOfPhoto))
                return cursor.getString(2);
        }
        return "Fail";
    }


    private void createTablesInDatabase(){

        System.out.println("Tworze tablee");
        db.execSQL("create table photos(" + "id integer primary key autoincrement," + "path int," + "emotion text," + "name text);" + "");
        db.execSQL("create table emotions(" + "id integer primary key autoincrement," + "emotion text);" + "");
        db.execSQL("create table levels(" + "id integer primary key autoincrement, photos_or_videos text, photos_or_videos_per_level int, " +
                "time_limit int, is_learn_mode int, is_test_mode int, number_of_tries_in_test int, time_limit_in_test int, material_for_test int, name text, correctness int, sublevels_per_each_emotion int, is_for_tests int, question_type text, hint_types_as_number int, praises text, shouldQuestionBeReadAloud boolean);" + "");
        db.execSQL("create table levels_photos(" + "id integer primary key autoincrement,"  + "levelid integer references levels(id)," + "material_for_test integer," + "photoid integer references photos(id));" + "");
        db.execSQL("create table levels_emotions(" + "id integer primary key autoincrement," + "levelid integer references levels(id),"  + "material_for_test integer," + "emotionid integer references emotions(id));" + "");
        db.execSQL("create table videos(" + "id integer primary key autoincrement," + "path int," + "emotion text," + "name text);" + "");
        db.execSQL("create table language(" + "id integer primary key autoincrement," + "language text not null unique," + "selected integer default 0);" + "");
    }

    private void deleteTablesFromDatabase(){

        db.execSQL("drop table levels_emotions");
        db.execSQL("drop table levels_photos");
        db.execSQL("drop table levels");
        db.execSQL("drop table emotions");
        db.execSQL("drop table photos");
        db.execSQL("drop table videos");
        db.execSQL("drop table language");
    }

    private void addEmotionsToDatabase(){
        addEmotion("happywoman");
        addEmotion("sadwoman");
        addEmotion("surprisedwoman");
        addEmotion("angrywoman");
        addEmotion("scaredwoman");
        addEmotion("boredwoman");
        addEmotion("happyman");
        addEmotion("sadman");
        addEmotion("surprisedman");
        addEmotion("angryman");
        addEmotion("scaredman");
        addEmotion("boredman");
        addEmotion("happychild");
        addEmotion("sadchild");
        addEmotion("surprisedchild");
        addEmotion("angrychild");
        addEmotion("scaredchild");
        addEmotion("boredchild");
      /*  addEmotion("happy");
        addEmotion("sad");
        addEmotion("surprised");
        addEmotion("angry");
        addEmotion("scared");
        addEmotion("bored");*/
    }

    public int getPhotoIdByName(String name){

        String[] columns = {"id", "path", "emotion", "name"};
        Cursor cursor = db.query("photos", columns,"name like " + "'%" + name + "%'", null, null, null, null);
        cursor.moveToNext();


        return cursor.getInt(0);
    }

    public String getCurrentLang() {
        String[] columns = {"id", "language", "selected"};
        String where = "selected == 1";
        Cursor cursor = db.query("language", columns, where, null, null, null, null, null);
        String result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(1);
        }
        return result;
    }


    public void updateCurrentLang(String lang) {
        ContentValues values = new ContentValues();
        values.put("selected", 1);
        String whereTrue = "language=?";
        db.update("language", values, whereTrue, new String[]{lang});
        values.put("selected", 0);
        String whereFalse = "language!=?";
        db.update("language", values, whereFalse, new String[]{lang});
    }


    private void addDefaultLevels() {
        addLang(1, "pl", 1);
        addLang(2, "en", 0);

        Level level_easy_icons = new Level();
        level_easy_icons.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_easy_icons.setName("IKONY - 2 emocje::Icons - 2 emotions");
        level_easy_icons.setLevelActive(true);
        level_easy_icons.setTimeLimit(10);
        level_easy_icons.setPraises("dobrze");
        level_easy_icons.setAmountOfAllowedTriesForEachEmotion(3);
        level_easy_icons.setSublevelsPerEachEmotion(2);
        level_easy_icons.setPhotosOrVideosShowedForOneQuestion(3);
        level_easy_icons.setForTests(true);
        level_easy_icons.setShouldQuestionBeReadAloud(false);
        level_easy_icons.setQuestionType(Level.Question.EMOTION_NAME);


        level_easy_icons.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        });
        level_easy_icons.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {


                add(9);
                add(10);
                add(11);
                add(16);
                add(14);
                add(15);


            }
        });

        saveLevelToDatabase(level_easy_icons);


        Level level_easy_photos = new Level();
        level_easy_photos.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_easy_photos.setName("ZDJĘCIA - 2 emocje::Photos - 2 emotions");
        level_easy_photos.setLevelActive(false);
        level_easy_photos.setTimeLimit(10);
        level_easy_photos.setPraises("dobrze");
        level_easy_photos.setAmountOfAllowedTriesForEachEmotion(3);
        level_easy_photos.setSublevelsPerEachEmotion(2);
        level_easy_photos.setPhotosOrVideosShowedForOneQuestion(3);
        level_easy_photos.setForTests(true);
        level_easy_photos.setShouldQuestionBeReadAloud(true);
        level_easy_photos.setQuestionType(Level.Question.EMOTION_NAME);

        level_easy_photos.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
            }
        });
        level_easy_photos.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(7);
                add(6);
                add(8);
                add(12);
                add(13);
            }
        });

        saveLevelToDatabase(level_easy_photos);

        Level level_medium = new Level();
        level_medium.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_medium.setName("ZDJĘCIA - 4 emocje::Photos - 4 emotions");
        level_medium.setLevelActive(false);
        level_medium.setTimeLimit(10);
        level_medium.setPraises("dobrze");
        level_medium.setPraises("wspaniale");
        level_medium.setPraises("świetnie");
        level_medium.setAmountOfAllowedTriesForEachEmotion(3);
        level_medium.setPhotosOrVideosShowedForOneQuestion(3);
        level_medium.setSublevelsPerEachEmotion(2);
        level_medium.setForTests(true);
        level_medium.setShouldQuestionBeReadAloud(true);
        level_medium.setQuestionType(Level.Question.SHOW_EMOTION_NAME);
        level_medium.setAmountOfEmotions(Integer.toString(level_medium.getEmotions().size()));

        level_medium.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(4);
                add(3);
            }
        });
        level_medium.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(7);
                add(6);
                add(8);
                add(12);
                add(13);
                add(1);
                add(2);
                add(3);
                add(17);
                add(18);
            }
        });

        saveLevelToDatabase(level_medium);


        Level level_difficult = new Level();
        level_difficult.setPhotosOrVideosIdList(new ArrayList<Integer>());
        level_difficult.setName("ZDJĘCIA - 6 emocji::Photos - 6 emotions");
        level_difficult.setLevelActive(false);
        level_difficult.setTimeLimit(10);
        level_difficult.setPraises("dobrze");//addPraises
        level_difficult.setPraises("good"); //chyba nie działa, trzebaby wszystko pousuwać i sprbować jeszcze raz
        level_difficult.setAmountOfAllowedTriesForEachEmotion(3);
        level_difficult.setSublevelsPerEachEmotion(2);
        level_difficult.setPhotosOrVideosShowedForOneQuestion(3);
        level_difficult.setForTests(true);
        level_difficult.setShouldQuestionBeReadAloud(true);
        level_difficult.setQuestionType(Level.Question.SHOW_WHERE_IS_EMOTION_NAME);
        //level_difficult.setAmountOfEmotions("6");
        level_difficult.setEmotions(new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(2);
                add(3);
                add(4);
                add(5);
            }
        });
        level_difficult.setPhotosOrVideosIdList(new ArrayList<Integer>() {
            {
                add(7);
                add(6);
                add(8);
                add(12);
                add(13);
                add(1);
                add(2);
                add(3);
                add(17);
                add(18);
                add(4);
                add(5);
                add(19);
            }
        });

        saveLevelToDatabase(level_difficult);
    }

}
