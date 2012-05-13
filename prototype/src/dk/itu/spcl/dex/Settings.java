package dk.itu.spcl.dex;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

  public static final String THINGY_URL = "http://192.168.43.193/";

  public static String getHomeSsid(Context context) {
    SharedPreferences sp = PreferenceManager
        .getDefaultSharedPreferences(context);
    return sp.getString("ssid", null);
  }
  
  public static String getHomePsk(Context context) {
    SharedPreferences sp = PreferenceManager
        .getDefaultSharedPreferences(context);
    return sp.getString("key", null);
  }

}