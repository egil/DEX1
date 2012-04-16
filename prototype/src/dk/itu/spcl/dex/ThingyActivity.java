package dk.itu.spcl.dex;

import dk.itu.spcl.dex.model.Thingy;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ThingyActivity extends Activity {

  private Thingy _thingy;  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    _thingy = getIntent().getParcelableExtra("thingy");
    
    TextView textview = new TextView(this);
    textview.setText("UI for " + _thingy.getName() + " goes here!");
    setContentView(textview);
  }
  
}
