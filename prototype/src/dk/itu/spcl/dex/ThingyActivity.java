package dk.itu.spcl.dex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class ThingyActivity extends Activity {

  private Thingy _thingy;
  private ThingyUpdater _thingyUpdater;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    _thingyUpdater = ThingyUpdater.getInstance();

    _thingy = Repository.getInstance().getThingy(
        getIntent().getStringExtra("thingy"));
    
    setTitle("Thingy: " +_thingy.getName());

    TextView textview = new TextView(this);
    textview.setText("UI for " + _thingy.getName() + " goes here!");
    setContentView(textview);
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

}
