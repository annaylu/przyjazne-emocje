package pg.autyzm.przyjazneemocje.configuration;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Locale;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.View.CheckboxImageAdapter;
import pg.autyzm.przyjazneemocje.View.GridCheckboxImageBean;
import pg.autyzm.przyjazneemocje.configuration.LevelConfigurationActivity;
import pg.autyzm.przyjazneemocje.lib.SqliteManager;
import pg.autyzm.przyjazneemocje.lib.entities.Level;

import static pg.autyzm.przyjazneemocje.lib.SqliteManager.getInstance;

public class MaterialForTestActivity extends AppCompatActivity {

    private Level level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_for_test);
        setTitle(R.string.title_choose_img);

        if(getIntent().getExtras() != null && !getIntent().getExtras().isEmpty()){
            level = (Level) getIntent().getExtras().getSerializable("level");
        }

        loadDataFromLevel();
    }

    void loadDataFromLevel(){
        EditText nrEmotions = (EditText) findViewById(R.id.nr_emotions_test);
        nrEmotions.setText(String.valueOf(level.getEmotions().size()));

        final ListView selectedEmotionsList = (ListView) findViewById(R.id.selected_emotions_list_test);

        CustomAdapter adapter = new CustomAdapter();
        selectedEmotionsList.setAdapter(adapter);

        Button saveButton = (Button) findViewById(R.id.button_selected_test);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(" WYBIERZ zdjecia z materiału KLIK2 " + level.getPhotosOrVideosIdList() + "zdjecia z TESTU " + level.getPhotosOrVideosIdListInTest());
                Intent data = new Intent();
                data.putExtra("level", level);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private void updateEmotionsGrid(int emotionNumber) {
        String emotion = getEmotionName(emotionNumber);

        GridCheckboxImageBean[] tabPhotos = getEmotionPhotos(emotion);

        final GridView listView = (GridView) findViewById(R.id.grid_photos_test);
        CheckboxImageAdapter adapter = new CheckboxImageAdapter(this, R.layout.grid_element_checkbox_image, tabPhotos, level, true);
        listView.setAdapter(adapter);
    }

    private String getEmotionName(int emotionNumber) {
        Configuration config = new Configuration(getBaseContext().getResources().getConfiguration());
        config.setLocale(Locale.ENGLISH);
        return getBaseContext().createConfigurationContext(config).getResources().getStringArray(R.array.emotions_array)[emotionNumber];
    }

    private GridCheckboxImageBean[] getEmotionPhotos(String choosenEmotion) {
        SqliteManager sqlm = getInstance(this);
        Cursor cursor = sqlm.givePhotosWithEmotion(choosenEmotion);
        int n = cursor.getCount();
        Cursor cursorVid = sqlm.giveVideosWithEmotion(choosenEmotion);
        n = n + cursorVid.getCount();
        GridCheckboxImageBean tabPhotos[] = new GridCheckboxImageBean[n];

        while (cursorVid.moveToNext()) {

            tabPhotos[--n] = (new GridCheckboxImageBean(cursorVid.getString(3), cursorVid.getInt(1), getContentResolver(), cursorVid.getInt(0)));
        }

        while (cursor.moveToNext()) {

            tabPhotos[--n] = (new GridCheckboxImageBean(cursor.getString(3), cursor.getInt(1), getContentResolver(), cursor.getInt(0)));
        }

        return tabPhotos;
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return level.getEmotions().size();
        }

        @Override
        public Object getItem(int n) {
            return level.getEmotions().get(n);
        }

        @Override
        public long getItemId(int n) {
            return level.getEmotions().get(n);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.row_spinner, parent, false);

            Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner_emotions);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MaterialForTestActivity.this,
                    R.array.emotions_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(level.getEmotions().get(position));
            spinner.setEnabled(false);

            ImageButton button = (ImageButton) convertView.findViewById(R.id.button_edit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateEmotionsGrid(level.getEmotions().get(position));
                }
            });

            AdapterView.OnItemSelectedListener emotionSelectedListener = new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> spinner, View container,
                                           int position, long id) {
                    updateEmotionsGrid(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            };
            spinner.setOnItemSelectedListener(emotionSelectedListener);

            return convertView;
        }
    }
}
