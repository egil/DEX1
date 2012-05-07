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

    setTitle("Install new thingy");
    TextView textview = new TextView(this);
    textview.setText("Hold your phone close to the device you want to add.");
    textview.setTextSize(18);
    setContentView(textview);

    performDummyScan();
  }

  private void addThingy(String name) {
    Thingy thingy = new Thingy().setName(name).setUrl(Settings.THINGY_URL);
    _repository.addThingy(thingy);
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

  public static int _thingiesReturned = 0;

  private void performDummyScan() {
    if (_thingiesReturned >= 3)
      return;

    _scanTask = new ScanTask();
    _scanTask.execute(0);
  }

  private class ScanTask extends AsyncTask<Integer, Integer, String> {

    @Override
    protected String doInBackground(Integer... params) {
      waitForScan();
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      UITools.promptForString(ScanActivity.this, "Add thingy", "Name:",
          new UITools.PromptResultHandler<String>() {
            @Override
            public void closed(boolean accepted, String value) {
              if (accepted && value.length() > 0) {
                addThingy(value);
              }
            }
          });
    }

    private void waitForScan() {
      while (!isCancelled()) {
        return; // todo QR/whatever
      }
    }
  }

}
