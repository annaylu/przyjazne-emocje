package pg.autyzm.graprzyjazneemocje;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

import pg.autyzm.przyjazneemocje.lib.SqliteManager;

public class SplashActivity extends Activity {

    public static final String CURRENT_LANG = "KEY_CURRENT_LANG";

    public SqliteManager sqlm;
    protected Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //  setContentView(R.layout.activity_splash);

        sqlm = SqliteManager.getInstance(this);
        setLocale(sqlm.getCurrentLang());
    }


    public void setLocale(final String localeName) {
        myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        sqlm.updateCurrentLang(localeName);

        final Intent refresh = new Intent(this, MainActivity.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.putExtra(CURRENT_LANG, localeName);
                startActivity(refresh);
            }
        }, 1000);

    }
}
