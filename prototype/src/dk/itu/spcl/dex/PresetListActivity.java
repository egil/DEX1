package dk.itu.spcl.dex;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Repository.UpdateListener;

public class PresetListActivity extends ListActivity {

  private Repository _repository;
  private ArrayAdapter<Preset> _listAdapter;
  private Repository.UpdateListener _updateListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _repository = Repository.getInstance();
    populatePresetList();
    addRepositoryListener();
    addListSelectionListener();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    removeUpdateListener();
  }

  private void addRepositoryListener() {
    _updateListener = new UpdateListener() {
      @Override
      public void repositoryUpdated() {
        _listAdapter.clear();
        for (Preset p : _repository.getPresets())
          _listAdapter.add(p);
        _listAdapter.notifyDataSetChanged();
      }
    };
    _repository.addUpdateListener(_updateListener);
  }

  private void removeUpdateListener() {
    _repository.removeUpdateListener(_updateListener);
  }

  private void addListSelectionListener() {
    ListView listView = getListView();
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Preset preset = (Preset) parent.getItemAtPosition(position);
        startPresetActivity(preset);
      }
    });
  }

  private void startPresetActivity(Preset preset) {
    Intent intent = new Intent(this, PresetActivity.class).putExtra("preset",
        preset.getName());
    startActivity(intent);
  }

  private void populatePresetList() {
    _listAdapter = new ArrayAdapter<Preset>(this, R.layout.default_list_item,
        _repository.getPresets());
    setListAdapter(_listAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.presetlistmenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.newPresetMenu:
      newPreset();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  private void newPreset() {
    // http://blog.350nice.com/wp/archives/240
  }

}
