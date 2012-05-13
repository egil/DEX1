package dk.itu.spcl.dex;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;
import dk.itu.spcl.dex.tools.UITools;

public class ScanActivity extends Activity {

  private Repository _repository;
  private BootstrapTask _bootstrapTask;
  private TextView _textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    _repository = Repository.getInstance();

    setTitle("Install new thingy");

    _textView = new TextView(this);
    _textView.setTextSize(18);
    setContentView(_textView);

    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.initiateScan();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
        resultCode, intent);
    if (scanResult != null) {
      String scannedText = scanResult.getContents();
      if (scannedText.startsWith("thingy://")) {
        showText("Great! Now press the button on the thingy. Connecting will take a little while.");
        String[] wifiInfo = scannedText.substring("thingy://".length()).split(
            "\\/");
        startBootstrap(wifiInfo[0], wifiInfo[1]);
      }
    }
  }

  private void startBootstrap(String ssid, String key) {
    _bootstrapTask = new BootstrapTask();
    _bootstrapTask.execute(ssid, key);
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
    if (_bootstrapTask != null)
      _bootstrapTask.cancel(true);

    super.onDestroy();
  }

  private void showText(final String text) {
    runOnUiThread(new Runnable() {
      public void run() {
        _textView.setText(text);
      }
    });
  }

  private class BootstrapTask extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
      Bootstrapper bootstrapper = new Bootstrapper(ScanActivity.this,
          params[0], params[1]);
      return bootstrapper.runBootstrapping();
    }

    @Override
    protected void onPostExecute(final String result) {
      showText("Excellent");
      UITools.promptForString(ScanActivity.this, "Add thingy", "Name:",
          new UITools.PromptResultHandler<String>() {
            @Override
            public void closed(boolean accepted, String value) {
              if (accepted && value.length() > 0) {
                showText("");
                addThingy(value, result);
              }
            }
          });
    }
  }
}
