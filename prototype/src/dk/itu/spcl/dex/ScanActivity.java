package dk.itu.spcl.dex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class ScanActivity extends Activity {

  private Repository _repository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _repository = Repository.getInstance();

    TextView textview = new TextView(this);
    textview.setText("Scanning goes here");
    setContentView(textview);

    performDummyScan();
  }

  private static int _thingiesReturned = 0;

  private void performDummyScan() {
    Thread scanThread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        Thingy thingy = getMockThingy();

        _repository.addThingy(thingy);

        returnScannedThingy(thingy);
      }

      private Thingy getMockThingy() {
        _thingiesReturned++;
        if (_thingiesReturned == 1) {
          return new Thingy().setName("Thingy one!").setUrl(
              "http://anti.zno.dk/wizard/one");
        } else if (_thingiesReturned == 2) {
          return new Thingy().setName("Thingy two!").setUrl(
              "http://anti.zno.dk/wizard/two");
        } else {
          return null;
        }
      }

      private void returnScannedThingy(Thingy thingy) {
        if (thingy == null) {
          setResult(RESULT_CANCELED);
        } else {
          Intent data = new Intent(ScanActivity.this, getClass()).putExtra(
              "thingy", thingy.getName());
          setResult(RESULT_OK, data);
        }
        finish();
      }
    });
    scanThread.start();
  }
}
