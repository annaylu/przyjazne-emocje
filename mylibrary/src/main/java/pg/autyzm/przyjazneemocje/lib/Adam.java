package pg.autyzm.przyjazneemocje.lib;

import android.app.Application;
import android.content.Context;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;


import java.util.Locale;
import java.util.Locale;

import pg.autyzm.przyjazneemocje.lib.R;

public class Adam  extends Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }

    //Context ctx = null;
    public Adam() {
//        ctx=  getApplicationContext();
        return;
    }

    public void adam_print() {
        System.out.println("@@@@@@@@@ ADAM 1   @@@@@@@@@ ");
Integer test_id;
String test_str;
  //test =  R.string.app_name;
        test_id =  R.string.adam_key;
        System.out.println("@@@@@@@@@ ADAM 2   @@@@@@@@@ "+ test_id);


        Resources res = this.getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;

        //Configuration config = new Configuration(getBaseContext().getResources().getConfiguration());
        //config.setLocale(Locale.ENGLISH);
        //return getBaseContext().createConfigurationContext(config).getResources().getStringArray(R.array.emotions_array)[emotionNumber];
        test_str = res.getString(test_id);
        System.out.println("@@@@@@@@@ ADAM 3   @@@@@@@@@ "+ test_str);

        //String englishName = getLocaleStringResource(new Locale("en"), test_id, ctx);
    }


}
