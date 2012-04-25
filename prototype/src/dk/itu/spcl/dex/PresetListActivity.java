package dk.itu.spcl.dex;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Repository.Listener;
import dk.itu.spcl.dex.tools.UITools;

public class PresetListActivity extends ListActivity {

  private Repository _repository;
  private CustomArrayAdapter<Preset> _listAdapter;
  private Repository.Listener _updateListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _repository = Repository.getInstance();
    initializePresetList();
    addRepositoryListener();
    addListSelectionListener();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    removeUpdateListener();
  }

  private void addRepositoryListener() {
    _updateListener = new Listener() {
      @Override
      public void repositoryStructureChanged() {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            populatePresetList();
          }
        });
      }

      @Override
      public void repositoryStatusChanged() {
        // don't care
      }
    };
    _repository.addListener(_updateListener);
  }

  private void removeUpdateListener() {
    _repository.removeListener(_updateListener);
  }

  private void addListSelectionListener() {
    ListView listView = getListView();
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Preset preset = (Preset) parent.getItemAtPosition(position);
        onPresetSelected(preset);
      }
    });
  }

  private void onPresetSelected(Preset preset) {
    if (preset != _repository.getDummyPreset())
      startPresetActivity(preset);
    else
      newPreset();
  }

  private void startPresetActivity(Preset preset) {
    Intent intent = new Intent(this, PresetActivity.class).putExtra("preset",
        preset.getName());
    startActivity(intent);
  }

  private void initializePresetList() {
    _listAdapter = new CustomArrayAdapter<Preset>(this,
        R.layout.default_list_item, R.id.listTextView, new ArrayList<Preset>());
    setListAdapter(_listAdapter);

    populatePresetList();
  }

  private void populatePresetList() {
    _listAdapter.clear();
    for (Preset p : _repository.getPresets())
      _listAdapter.add(p);
    _listAdapter.add(_repository.getDummyPreset());
    _listAdapter.notifyDataSetChanged();
  }

  private void newPreset() {
    UITools.promptForString(this, "New preset", "Name:", new UITools.PromptResultHandler() {
      @Override
      public void closed(boolean accepted, String value) {
        if (accepted && value.length() > 0)
          addPreset(value);
      }
    });
  }

  protected void addPreset(String name) {
    Preset newPreset = new Preset().setName(name);
    _repository.addPreset(newPreset);
    startPresetActivity(newPreset);
  }
}
