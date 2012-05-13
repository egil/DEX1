package dk.itu.spcl.dex;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;
import dk.itu.spcl.dex.tools.UITools;

public class ScanActivity extends Activity {

  private Repository _repository;
  private ScanTask _scanTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _repository = Repository.getInstance();

    setTitle("Install new thingy");

    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.initiateScan();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
        resultCode, intent);
    if (scanResult != null) {
      String scannedText = scanResult.getContents();
      if (scannedText.startsWith("thingy://")) {
        String[] wifiInfo = scannedText.substring("thingy://".length()).split(
            "\\/");
        startBootstrap(wifiInfo[0], wifiInfo[1]);
      }
    }
  }

  private void startBootstrap(String ssid, String key) {
    _scanTask = new ScanTask();
    _scanTask.execute(ssid, key);
  }

  private void addThingy(String name, String url) {
    Thingy thingy = new Thingy().setName(name).setUrl(url);
    Log.i("dex", "Adding thingy with url " + url);
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

  private class ScanTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
      Bootstrapper bootstrapper = new Bootstrapper(ScanActivity.this,
          params[0], params[1]);
      return bootstrapper.runBootstrapping();
    }

    @Override
    protected void onPostExecute(final String result) {
      UITools.promptForString(ScanActivity.this, "Add thingy", "Name:",
          new UITools.PromptResultHandler<String>() {
            @Override
            public void closed(boolean accepted, String value) {
              if (accepted && value.length() > 0) {
                addThingy(value, result);
              }
            }
          });
    }
  }
}
