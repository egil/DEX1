package dk.itu.spcl.dex;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;
import dk.itu.spcl.dex.tools.HttpTools;
import dk.itu.spcl.dex.tools.UITools;

public class ScanActivity extends Activity {

  private Repository _repository;
  private ScanTask _scanTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _repository = Repository.getInstance();

    TextView textview = new TextView(this);
    textview.setText("Hold your phone close to the device you want to add.");
    textview.setTextSize(16);
    setContentView(textview);

    performDummyScan();
  }

  private void addMockThingy(String name) {
    Thingy thingy = new Thingy().setName(name).setUrl(
        "status_" + _thingiesReturned);
    _repository.addThingy(thingy);
    _thingiesReturned++;
    returnScannedThingy(thingy);
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

  @Override
  protected void onDestroy() {
    if (_scanTask != null)
      _scanTask.cancel(true);

    super.onDestroy();
  }

  private static int _thingiesReturned = 0;

  private void performDummyScan() {
    if (_thingiesReturned >= 3)
      return;

    _scanTask = new ScanTask();
    _scanTask.execute(0);
  }

  private class ScanTask extends AsyncTask<Integer, Integer, String> {

    @Override
    protected String doInBackground(Integer... params) {
      waitForMockScan();
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      UITools.promptForString(ScanActivity.this, "Add thingy", "Name:",
          new UITools.PromptResultHandler<String>() {
            @Override
            public void closed(boolean accepted, String value) {
              if (accepted && value.length() > 0) {
                addMockThingy(value);
              }
            }
          });
    }

    private void waitForMockScan() {
      while (!isCancelled()) {
        String scanned = null;
        try {
          scanned = HttpTools.readStringFromUrl(Settings.WIZARD_URL + "scan_"
              + _thingiesReturned);
        } catch (ClientProtocolException e1) {
          e1.printStackTrace();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
        if (scanned.equals("1"))
          return;
        else
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
      }
    }
  }

}
