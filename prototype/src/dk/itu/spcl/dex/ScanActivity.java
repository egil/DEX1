package dk.itu.spcl.dex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ScanActivity extends Activity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    TextView textview = new TextView(this);
    textview.setText("Scanning goes here");
    setContentView(textview); 
  }

}
