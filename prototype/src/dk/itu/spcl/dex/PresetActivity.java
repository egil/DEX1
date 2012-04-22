package dk.itu.spcl.dex;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
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

    _preset = Repository.getInstance().getPreset(
        getIntent().getStringExtra("preset"));
    setTitle("Preset: " + _preset.getName());
    initializeThingyList();
    addListSelectionListener();
  }

  private void addListSelectionListener() {
    ListView listView = (ListView) findViewById(R.id.presetThingyList);
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Thingy thingy = (Thingy) parent.getItemAtPosition(position);
        onThingySelected(thingy);
      }
    });
  }

  protected void onThingySelected(Thingy thingy) {
    if (thingy == _repository.getDummyThingy())
      addThingy();
  }

  private void initializeThingyList() {
    _listAdapter = new ArrayAdapter<Thingy>(this, R.layout.default_list_item,
        new ArrayList<Thingy>());
    ListView listView = (ListView) findViewById(R.id.presetThingyList);
    listView.setAdapter(_listAdapter);

    populateThingyList();
  }

  private void populateThingyList() {
    _listAdapter.clear();
    for (Thingy t : _preset.getThingies())
      _listAdapter.add(t);

    _listAdapter.add(_repository.getDummyThingy());
    _listAdapter.notifyDataSetChanged();
  }

  private void addThingy() {
    startActivityForResult(new Intent(this, ThingyListActivity.class).putExtra(
        "selectionMode", true), PICK_THINGY_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_CANCELED) {
      Thingy selected = _repository.getThingy(data.getExtras().getString("thingy"));
      _preset.addThingy(selected);
      populateThingyList();
    }
  }

}
