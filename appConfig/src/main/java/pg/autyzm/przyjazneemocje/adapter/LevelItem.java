package pg.autyzm.przyjazneemocje.adapter;

public class LevelItem {
    private int levelId;
    private String name;
    private boolean isActive;
    private boolean isLearnMode;
    private boolean isTestMode;
    private boolean canEdit;
    private boolean canRemove;

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    private boolean is_default;

   /* public void reduceLevelId() {
        this.levelId--;
    }*/

    public LevelItem(int levelId, String name, boolean isActive, boolean isLearnMode, boolean isTestMode,  boolean is_default) {
        this.levelId = levelId;
        this.name = name;
        this.isActive = isActive;
        this.isLearnMode = isLearnMode;
        this.isTestMode = isTestMode;
        this.canEdit = !is_default;
        this.canRemove = !is_default;
        this.is_default=is_default;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
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
}
