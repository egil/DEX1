package dk.itu.spcl.dex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class ThingyActivity extends Activity {

  private Thingy _thingy;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _thingy = Repository.getInstance().getThingy(
        getIntent().getStringExtra("thingy"));
    
    setTitle("Thingy: " +_thingy.getName());

    TextView textview = new TextView(this);
    textview.setText("UI for " + _thingy.getName() + " goes here!");
    setContentView(textview);
  }

}
