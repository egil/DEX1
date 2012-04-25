package dk.itu.spcl.dex;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class HomeActivity extends TabActivity {
  
  private ThingyUpdater _thingyUpdater;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _thingyUpdater = ThingyUpdater.getInstance();
    setContentView(R.layout.home);
    addTabs();
  }

  @Override
  protected void onPause() {
    _thingyUpdater.cancelUpdatesFor(this);
    super.onPause();
  }

  @Override
  protected void onResume() {
    _thingyUpdater.requestUpdatesFor(this);
    super.onResume();
  }

  private void addTabs() {
    Resources res = getResources(); 
    TabHost tabHost = getTabHost(); 

    addThingyTab(res, tabHost);
    addPresetTab(res, tabHost);
  }

  private void addPresetTab(Resources res, TabHost tabHost) {
    TabHost.TabSpec spec;
    Intent intent;
    intent = new Intent(this, PresetListActivity.class);
    spec = tabHost.newTabSpec("presetlist")
        .setIndicator("Presets")//, res.getDrawable(R.drawable.ic_tab_default))
        .setContent(intent);
    tabHost.addTab(spec);
  }

  private void addThingyTab(Resources res, TabHost tabHost) {
    Intent intent = new Intent(this, ThingyListActivity.class);
    TabHost.TabSpec spec = tabHost.newTabSpec("thingylist")
        .setIndicator("Thingies")//, res.getDrawable(R.drawable.ic_tab_default))
        .setContent(intent);
    tabHost.addTab(spec);
  }

}