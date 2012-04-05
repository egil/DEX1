package dk.itu.spcl.dex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PresetListActivity extends Activity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    TextView textview = new TextView(this);
    textview.setText("Preset list goes here");
    setContentView(textview);
  }

}
