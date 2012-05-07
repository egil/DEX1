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
  private ThingyStatusWriter _thingyWriter;
  private ToggleButton _toggleButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _repository = Repository.getInstance();
    _thingyUpdater = ThingyUpdater.getInstance();
    _thingyWriter = new ThingyStatusWriter();

    _thingy = Repository.getInstance().getThingy(
        getIntent().getStringExtra("thingy"));

    setTitle("Thingy: " + _thingy.getName());

    _repository.addListener(this);
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
