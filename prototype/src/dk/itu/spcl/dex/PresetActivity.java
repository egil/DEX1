package dk.itu.spcl.dex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class PresetActivity extends Activity {
  
  private Preset _preset;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.preset);
    
    _preset = Repository.getInstance().getPreset(getIntent().getStringExtra("preset"));
    setTitle("Preset: " + _preset.getName());
    populateThingyList();
  }
  
  private void populateThingyList() {
    ArrayAdapter<Thingy> listAdapter = new ArrayAdapter<Thingy>(this, R.layout.default_list_item,
        _preset.getThingies());
    ListView listView = (ListView)findViewById(R.id.presetThingyList);
    listView.setAdapter(listAdapter);
  }
  
}




