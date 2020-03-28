package pg.autyzm.przyjazneemocje.adapter;

public interface ILevelListCallback {

    void editLevel(LevelItem level);

    void removeLevel(LevelItem level);

    void setLevelActive(LevelItem level, boolean isChecked, boolean isLearnMode);
}
