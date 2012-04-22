package dk.itu.spcl.dex;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Repository.UpdateListener;

public class PresetListActivity extends ListActivity {

  private Repository _repository;
  private CustomArrayAdapter<Preset> _listAdapter;
  private Repository.UpdateListener _updateListener;

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
    _updateListener = new UpdateListener() {
      @Override
      public void repositoryUpdated() {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            populatePresetList();
          }
        });
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
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle("New preset");
    alert.setMessage("Name:");
    final EditText input = new EditText(this);
    alert.setView(input);

    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        String name = input.getText().toString();
        if (name.length() > 0)
          addPreset(name);
      }
    });

    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        // Canceled.
      }
    });
    alert.show();
  }

  protected void addPreset(String name) {
    Preset newPreset = new Preset().setName(name);
    _repository.addPreset(newPreset);
    startPresetActivity(newPreset);
  }

}
