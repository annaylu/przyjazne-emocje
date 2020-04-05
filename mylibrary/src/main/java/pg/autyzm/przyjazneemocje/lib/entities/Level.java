package pg.autyzm.przyjazneemocje.lib.entities;

import android.database.Cursor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pg.autyzm.przyjazneemocje.lib.R;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

/**
 * Created by Ann on 25.10.2016.
 */
public class Level implements Serializable {

    private int id;

    private String photosOrVideosFlag = "photos";
    private int timeLimit;
    private int photosOrVideosShowedForOneQuestion;
    private int sublevelsPerEachEmotion;
    private int amountOfAllowedTriesForEachEmotion;
    private boolean isForTests;

    //ZlecenieIT - Test mode
    private boolean isLearnMode;
    private boolean isTestMode;
    private boolean isMaterialForTest;
    private int numberOfTriesInTest;
    private int timeLimitInTest;

    public boolean isLevelActive() {
        return isLevelActive;
    }

    public void setLevelActive(boolean levelActive) {
        isLevelActive = levelActive;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    private boolean isLevelActive;
    private boolean is_default;

    private static Level levelContext;
    private String amountOfEmotions;

    public Level(Cursor curLevel, Cursor cur2, Cursor cur3) {

        setPhotosOrVideosIdList(new ArrayList<Integer>());
        setEmotions(new ArrayList<Integer>());
        setPhotosOrVideosIdListInTest(new ArrayList<Integer>());
        setEmotionsInTest(new ArrayList<Integer>());

        while (curLevel.moveToNext()) {
            //setAmountOfEmotions(cur.getString(cur.getColumnIndex("nr_emotions")));
            setId(curLevel.getInt(curLevel.getColumnIndex("id")));
            int active = curLevel.getInt(curLevel.getColumnIndex("is_level_active"));
            setLevelActive((active != 0));
            setPhotosOrVideosFlag(curLevel.getString(curLevel.getColumnIndex("photos_or_videos")));

            setTimeLimit(curLevel.getInt(curLevel.getColumnIndex("time_limit")));
            setPhotosOrVideosShowedForOneQuestion(curLevel.getInt(curLevel.getColumnIndex("photos_or_videos_per_level")));

            //
            setPraisesBinary(curLevel.getInt(curLevel.getColumnIndex("praisesBinary")));
            setShouldQuestionBeReadAloud(curLevel.getInt(curLevel.getColumnIndex("shouldQuestionBeReadAloud")) != 0);
            setIs_default(curLevel.getInt(curLevel.getColumnIndex("is_default"))==1);


            int isLevelForTests = curLevel.getInt(curLevel.getColumnIndex("is_for_tests"));
            setForTests(isLevelForTests != 0);

            setAmountOfAllowedTriesForEachEmotion(curLevel.getInt(curLevel.getColumnIndex("correctness")));
            setSublevelsPerEachEmotion(curLevel.getInt(curLevel.getColumnIndex("sublevels_per_each_emotion")));


            setName(curLevel.getString(curLevel.getColumnIndex("name")));
            setQuestionType(Question.valueOf(curLevel.getString(curLevel.getColumnIndex("question_type"))));
            setHintTypesAsNumber(curLevel.getInt(curLevel.getColumnIndex("hint_types_as_number")));
            setCommandTypesAsNumber(curLevel.getInt(curLevel.getColumnIndex("command_types_as_number")));

            //ZlecenieIT
            int isLearnMode = curLevel.getInt(curLevel.getColumnIndex("is_learn_mode"));
            int isTestMode = curLevel.getInt(curLevel.getColumnIndex("is_test_mode"));
            int isMaterialForTest = curLevel.getInt(curLevel.getColumnIndex("material_for_test"));

            setLearnMode((isLearnMode != 0));
            setTestMode((isTestMode != 0));
            setMaterialForTest((isMaterialForTest != 0));

            setNumberOfTriesInTest(curLevel.getInt(curLevel.getColumnIndex("number_of_tries_in_test")));
            setTimeLimitInTest(curLevel.getInt(curLevel.getColumnIndex("time_limit_in_test")));
        }

        if (cur2 != null) {
            while (cur2.moveToNext()) {
                if(cur2.getInt(cur2.getColumnIndex("material_for_test")) == 0){
                    getPhotosOrVideosIdList().add(cur2.getInt(cur2.getColumnIndex("photoid")));
                } else {
                    getPhotosOrVideosIdListInTest().add(cur2.getInt(cur2.getColumnIndex("photoid")));
                }
            }
        }

        if (cur3 != null) {
            while (cur3.moveToNext()) {
                if(cur3.getInt(cur2.getColumnIndex("material_for_test")) == 0){
                    getEmotions().add(cur3.getInt(cur3.getColumnIndex("emotionid")) - 1);
                } else {
                    getEmotionsInTest().add(cur3.getInt(cur3.getColumnIndex("emotionid")) - 1);
                }
            }
        }

    }
    private String name;

    public static Level getLevelContext() {
        return levelContext;
    }

   /* public String getAmountOfEmotions() {
        return amountOfEmotions;
    }*/
    public int getAmountOfEmotions() {
        return this.emotions.size();
    }

    private int hintTypesAsNumber = 0;

    public int getCommandTypesAsNumber() {
        return commandTypesAsNumber;
    }

    public void setCommandTypesAsNumber(int commandTypesAsNumber) {
        this.commandTypesAsNumber = commandTypesAsNumber;
    }

    private int commandTypesAsNumber = 0;



    private List<Integer> photosOrVideosIdList;
    private List<Integer> photosOrVideosIdListInTest;
    private List<Integer> emotions = new ArrayList<>();
    private List<Integer> emotionsInTest = new ArrayList<>();

    public List<Integer> getPhotoToBeDeletedFromDatabaseId() {
        return photoToBeDeletedFromDatabaseId;
    }

    private List<Integer> photoToBeDeletedFromDatabaseId = new ArrayList<>();

    public List<String> getPhotoNameToBeDeletedFromDirectory() {
        return photoNameToBeDeletedFromDirectory;
    }

    private List<String> photoNameToBeDeletedFromDirectory = new ArrayList<>();


    public void addPhotoToBePermanentlyDeleted(int photoId, String photoName) {
        photoToBeDeletedFromDatabaseId.add(photoId);
        photoNameToBeDeletedFromDirectory.add(photoName);
    }


    public int getPraisesBinary() {
        return praisesBinary;
    }

    public void setPraisesBinary(int praisesBinary) {
        this.praisesBinary = praisesBinary;
    }

    public int getCommandsBinary() {
        return commandsBinary;
    }

    public void setCommandsBinary(int commandsBinary) {
        this.commandsBinary = commandsBinary;
    }

    private int commandsBinary = 0;

    private int praisesBinary = 0;

    public void setPraiseBinaryTypesAsNumber() {
        this.praisesBinary = 0;
    }

    public void addPraiseBinaryTypesAsNumber(int type) {
        this.praisesBinary = praisesBinary | type;
    }
    private int secondsToHint;
    private boolean shouldQuestionBeReadAloud;

    private Question questionType;
    private List<Hint> hintTypes = new ArrayList<>();

    public List<Command> getCommandTypes() {
        return commandTypes;
    }

    public void setCommandTypes(List<Command> commandTypes) {
        this.commandTypes = commandTypes;
    }

    private List<Command> commandTypes = new ArrayList<>();

    public Question getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Question questionType) {
        this.questionType = questionType;
    }

    public List<Hint> getHintTypes() {
        return hintTypes;
    }

    public void setHintTypes(List<Hint> hintTypes) {
        this.hintTypes = hintTypes;
    }


    public int allSelected(int positions) {
        int result=0;
        for (int i = 0; i < positions; i++) {
            result += Math.floor(Math.pow(2,i));
        }
        return result;
    }

/*    public int allCommands() {
        return 1+ 2 + 2*2 + 2*2*2 + 2*2*2*2;
        //bo 5 pochwał
    }

    public int allHints() {
        return 1+ 2 + 2*2 + 2*2*2 + 2*2*2*2;
        //bo 5 pochwał
    }*/



    public int getHintTypesAsNumber() {
        return hintTypesAsNumber;
    }

    public void setHintTypesAsNumber(int hintTypesAsNumber) {
        this.hintTypesAsNumber = hintTypesAsNumber;
    }

    public int getNumberOfTriesInTest() {
        return numberOfTriesInTest;
    }

    public void setNumberOfTriesInTest(int numberOfTriesInTest) {
        this.numberOfTriesInTest = numberOfTriesInTest;
    }

    public int getTimeLimitInTest() {
        return timeLimitInTest;
    }

    public void setTimeLimitInTest(int timeLimitInTest) {
        this.timeLimitInTest = timeLimitInTest;
    }

    public boolean isMaterialForTest() {
        return isMaterialForTest;
    }

    public void setMaterialForTest(boolean isMaterialForTest) {
        this.isMaterialForTest = isMaterialForTest;
    }

    public enum Question {
        EMOTION_NAME, SHOW_EMOTION_NAME, SHOW_WHERE_IS_EMOTION_NAME
    }

    public enum Hint {
        FRAME_CORRECT, ENLARGE_CORRECT, MOVE_CORRECT, GREY_OUT_INCORRECT
    }
    public enum Command {
        SHOW, SELECT, FIND, TOUCH, POINT
    }

    public void addHintType(Hint hint){
        hintTypes.add(hint);
    }

    public void addCommandType(Command command){
        commandTypes.add(command);
    }

    public void setAmountOfEmotions(String amountOfEmotions) {
        this.getEmotions().size();
    }


    public Level(){

        setPhotosOrVideosIdList(new ArrayList<Integer>());
        setEmotions(new ArrayList<Integer>());
        setPhotosOrVideosIdListInTest(new ArrayList<Integer>());
        setEmotionsInTest(new ArrayList<Integer>());
        setLearnMode(true);
        setId(0);

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotosOrVideosFlag() {
        return photosOrVideosFlag;
    }

    public void setPhotosOrVideosFlag(String photosOrVideosFlag) {
        this.photosOrVideosFlag = photosOrVideosFlag;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getPhotosOrVideosShowedForOneQuestion() {
        return photosOrVideosShowedForOneQuestion;
    }

    public void setPhotosOrVideosShowedForOneQuestion(int photosOrVideosShowedForOneQuestion) {
        this.photosOrVideosShowedForOneQuestion = photosOrVideosShowedForOneQuestion;
    }

    public boolean isLearnMode() {
        return isLearnMode;
    }

    public void setLearnMode(boolean learnMode) {
        isLearnMode = learnMode;
    }

    public boolean isTestMode() {
        return isTestMode;
    }

    public void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    public int getSublevelsPerEachEmotion() {
        return sublevelsPerEachEmotion;
    }

    public void setSublevelsPerEachEmotion(int sublevelsPerEachEmotion) {
        this.sublevelsPerEachEmotion = sublevelsPerEachEmotion;
    }

    public int getAmountOfAllowedTriesForEachEmotion() {
        return amountOfAllowedTriesForEachEmotion;
    }

    public void setAmountOfAllowedTriesForEachEmotion(int amountOfAllowedTriesForEachEmotion) {
        this.amountOfAllowedTriesForEachEmotion = amountOfAllowedTriesForEachEmotion;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return generateDefaultName();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getPhotosOrVideosIdList() {
        return photosOrVideosIdList;
    }

    public void setPhotosOrVideosIdList(List<Integer> photosOrVideosIdList) {
        this.photosOrVideosIdList = photosOrVideosIdList;
    }
public int lastEmotionNumber() {
        return emotions.get(emotions.size()-1);
}
    public List<Integer> getEmotions() {
        return emotions;
    }



    public void setEmotions(List<Integer> emotions) {
        this.emotions = emotions;
    }

    public List<Integer> getPhotosOrVideosIdListInTest() {
        return photosOrVideosIdListInTest;
    }

    //getEmotionNameInLocalLanguage

    public void setPhotosOrVideosIdListInTest(List<Integer> photosOrVideosIdList) {
        this.photosOrVideosIdListInTest = photosOrVideosIdList;
    }

    public List<Integer> getEmotionsInTest() {
        return emotionsInTest;
    }

    public void setEmotionsInTest(List<Integer> emotions) {
        this.emotionsInTest = emotions;
    }

public int newEmotionId(){
 //todo - do poprawy, ładniejsza forma - zamiats 6 liczba emocji ze stringów
        for ( int i =0; i< 6; i++) {
            if (!emotions.contains(i)) {
                return i;
            }
        }
        return -1;
}
    public void addEmotion(int newEmotionId) {




        if (isEmotionNew(newEmotionId)) {
            this.emotions.add(newEmotionId);
        }


    }

    public boolean isEmotionNew(int emotionId) {
        return !(emotions.contains(emotionId));
    }

    public void deleteEmotion(int i) {
        this.emotions.remove(emotions.get(i));
    }

    public Map getInfo(){
        Map out = new HashMap();
        for(Field field : this.getClass().getDeclaredFields()){
            try {
                out.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    public int getSecondsToHint() {
        return secondsToHint;
    }

    public void setSecondsToHint(int secondsToHint) {
        this.secondsToHint = secondsToHint;
    }

    public boolean isShouldQuestionBeReadAloud() {
        return shouldQuestionBeReadAloud;
    }

    public void setShouldQuestionBeReadAloud(boolean shouldQuestionBeReadAloud) {
        this.shouldQuestionBeReadAloud = shouldQuestionBeReadAloud;
    }

    public void addPhoto(Integer photoId){
        photosOrVideosIdList.add(photoId);
    }

    public void addPhotoForTest(Integer photoId){
        photosOrVideosIdListInTest.add(photoId);
    }

    public void removePhoto(Integer photoId){
        photosOrVideosIdList.remove(photoId);
    }

    public void removePhotoForTest(Integer photoId){
        photosOrVideosIdListInTest.remove(photoId);
    }


    public int getAllSublevelsInLevelAmount(){
        return emotions.size() * sublevelsPerEachEmotion;
    }

    public boolean isForTests() {
        return isForTests;
    }

    public void setForTests(boolean forTests) {
        isForTests = forTests;
    }

    public void addHintTypeAsNumber(int newType){
        setHintTypesAsNumber(getHintTypesAsNumber() + newType);
    }

    public void addCommandTypeAsNumber(int newType){
        setCommandTypesAsNumber(getCommandTypesAsNumber() + newType);
    }

    public void removeHintTypeAsNumber(int newType){
        setHintTypesAsNumber(getHintTypesAsNumber() - newType);
    }

    public void incrementEmotionIdsForGame(){

        List<Integer> newEmotions = new ArrayList<>();

        for(Integer emotionId : emotions){
            newEmotions.add(emotionId + 1);
        }

        emotions = newEmotions;

    }

    private String generateDefaultName() {
        String name = "";
        for (int emotion : emotions) {
            name += emotion + " ";
        }
        return name;
    }

    public static Level defaultLevel(){
        Level level = new Level();

        level.addEmotion(0);
        level.addEmotion(1);

        return level;
    }

   /* public void selectAllPictures(String emotion){
        SqliteManager sqlm = getInstance();

        //Cursor cursor = sqlm.givePhotosWithEmotion(currentEmotionName);
        Cursor cursor = sqlm.givePhotosWithEmotion(getEmotionName(newEmotionId));
        System.out.println("metoda selectAllPictures " + getEmotionName(newEmotionId));
        while (cursor.moveToNext()) {
            getLevel().addPhoto(cursor.getInt(cursor.getColumnIndex("id")));

            //aniadzisiaj System.out.println("aneczka cursor.getInt(0) " + cursor.getInt(0));
        }


    }*/



}
