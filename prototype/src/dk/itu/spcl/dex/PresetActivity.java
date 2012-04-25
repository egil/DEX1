package dk.itu.spcl.dex;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class PresetActivity extends Activity implements Repository.Listener {

  private static final int PICK_THINGY_REQUEST_CODE = 1;
  private Preset _preset;
  private Repository _repository;
  private ThingyUpdater _thingyUpdater;
  private CustomArrayAdapter<Thingy> _listAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.preset);

    _repository = Repository.getInstance();
    _thingyUpdater = ThingyUpdater.getInstance();

    _preset = Repository.getInstance().getPreset(
        getIntent().getStringExtra("preset"));
    setTitle("Preset: " + _preset.getName());
    initializeThingyList();
    addListSelectionListener();
    _repository.addListener(this);
  }

  @Override
  protected void onPause() {
    _thingyUpdater.cancelUpdatesFor(this);
    super.onPause();
  }

  @Override
  protected void onResume() {
    _thingyUpdater.requestUpdatesFor(this);
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    _repository.removeListener(this);
    super.onDestroy();
  }

  @Override
  public void repositoryStructureChanged() {
    // don't care
  }

  @Override
  public void repositoryStatusChanged() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        _listAdapter.notifyDataSetChanged();
      }
    });
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
    _listAdapter = new CustomArrayAdapter<Thingy>(this,
        R.layout.default_list_item, R.id.listTextView, new ArrayList<Thingy>());
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
      Thingy selected = _repository.getThingy(data.getExtras().getString(
          "thingy"));
      _preset.addThingy(selected);
      populateThingyList();
    }
  }

}
