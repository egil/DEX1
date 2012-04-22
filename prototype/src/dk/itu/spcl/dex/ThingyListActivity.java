package dk.itu.spcl.dex;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Repository.UpdateListener;
import dk.itu.spcl.dex.model.Thingy;

public class ThingyListActivity extends ListActivity {

  private Repository _repository;
  private final int SCAN_FOR_THINGY_REQUEST_CODE = 1;
  private CustomArrayAdapter<Thingy> _listAdapter;
  private Repository.UpdateListener _updateListener;
  private boolean _selectionMode;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _repository = Repository.getInstance();

    _selectionMode = getIntent().getBooleanExtra("selectionMode", false);
    if (_selectionMode)
      setTitle("Select a thingy");

    initializeThingyList();
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
            populateThingyList();
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
        Thingy thingy = (Thingy) parent.getItemAtPosition(position);
        onThingySelected(thingy);
      }
    });
  }

  private void onThingySelected(Thingy thingy) {
    if (_selectionMode) {
      returnThingy(thingy);
    } else if (thingy == _repository.getDummyThingy()) {
      newThingy();
    } else {
      startThingyActivity(thingy);
    }
  }

  private void returnThingy(Thingy thingy) {
    Intent data = new Intent(this, getClass()).putExtra("thingy",
        thingy.getName());
    setResult(RESULT_OK, data);
    finish();
  }

  private void startThingyActivity(Thingy thingy) {
    Intent intent = new Intent(this, ThingyActivity.class).putExtra("thingy",
        thingy.getName());
    startActivity(intent);
  }

  private void initializeThingyList() {
    _listAdapter = new CustomArrayAdapter<Thingy>(this,
        R.layout.default_list_item, R.id.listTextView, new ArrayList<Thingy>());
    setListAdapter(_listAdapter);

    populateThingyList();
  }

  private void populateThingyList() {
    _listAdapter.clear();
    for (Thingy t : _repository.getThingies())
      _listAdapter.add(t);
    if (!_selectionMode)
      _listAdapter.add(_repository.getDummyThingy());
    _listAdapter.notifyDataSetChanged();
  }

  private void newThingy() {
    startActivityForResult(new Intent(this, ScanActivity.class),
        SCAN_FOR_THINGY_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_CANCELED) {
      // thingy added to repo already, nothing to do!
    }
  }

}
