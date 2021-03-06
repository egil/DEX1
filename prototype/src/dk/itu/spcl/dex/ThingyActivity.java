package dk.itu.spcl.dex;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class ThingyActivity extends Activity implements Repository.Listener {

  private Thingy _thingy;
  private ThingyUpdater _thingyUpdater;
  private Repository _repository;
  private ToggleButton _toggleButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _repository = Repository.getInstance();
    _thingyUpdater = ThingyUpdater.getInstance();

    _thingy = Repository.getInstance().getThingy(
        getIntent().getStringExtra("thingy"));

    setTitle("Thingy: " + _thingy.getName());

    setContentView(R.layout.simple_thingy);
    _toggleButton = (ToggleButton) findViewById(R.id.thingyToggleButton);
    _repository.addListener(this);
    _toggleButton.setChecked(_thingy.getStatus());
  }

  public void thingyToggleButtonClicked(View v) {
    ToggleButton button = (ToggleButton) v;
    boolean on = button.isChecked();
    _thingy.setStatus(on).setStatusChangeQueued(true);
  }
  
  @Override 
  protected void onDestroy() {
    _repository.removeListener(this);
    super.onDestroy();
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

  @Override
  public void repositoryStructureChanged() {
    // ignore
  }

  @Override
  public void repositoryStatusChanged() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        _toggleButton.setChecked(_thingy.getStatus());  
      }
    });
  }
}
