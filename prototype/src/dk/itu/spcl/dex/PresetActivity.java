package dk.itu.spcl.dex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class PresetActivity extends Activity {
  
  private static final int PICK_THINGY_REQUEST_CODE = 1;
  private Preset _preset;
  private Repository _repository;
  private ArrayAdapter<Thingy> _listAdapter;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.preset);
    
    _repository = Repository.getInstance();
    
    _preset = Repository.getInstance().getPreset(getIntent().getStringExtra("preset"));
    setTitle("Preset: " + _preset.getName());
    populateThingyList();
  }
  
  private void populateThingyList() {
    _listAdapter = new ArrayAdapter<Thingy>(this, R.layout.default_list_item,
        _preset.getThingies());
    ListView listView = (ListView)findViewById(R.id.presetThingyList);
    listView.setAdapter(_listAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.presetmenu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.addThingyMenu:
      addThingy();
      return true;
//    case R.id.renamePresetMenu:
//      renamePreset();
//      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  private void addThingy() {
    startActivityForResult(new Intent(this, ThingyListActivity.class).putExtra("selectionMode", true),
        PICK_THINGY_REQUEST_CODE);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_CANCELED) {
      Thingy selected = _repository.getThingy(data.getExtras().getString("thingy"));
      _preset.addThingy(selected);
      _listAdapter.notifyDataSetChanged();
    }
  }
  
  
  
}




