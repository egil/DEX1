package dk.itu.spcl.dex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import dk.itu.spcl.dex.model.Thingy;

public class ScanActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TextView textview = new TextView(this);
    textview.setText("Scanning goes here");
    setContentView(textview);

    performDummyScan();
  }

  private void performDummyScan() {
    Thread scanThread = new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        
        Thingy thingy = new Thingy().setName("scanned thingy whoo!");
        returnScannedThingy(thingy);
      }

      private void returnScannedThingy(Thingy thingy) { 
        Intent data = new Intent(ScanActivity.this, ScanActivity.class).putExtra("thingy", thingy.getName());
        setResult(RESULT_OK, data);
        finish();
      }
    });
    scanThread.start();
  }

}
