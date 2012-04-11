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
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class ThingyListActivity extends ListActivity {

  private Repository _repository;
  private final int SCAN_FOR_THINGY_REQUEST_CODE = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    _repository = Repository.getInstance();
    populateThingyList();
    addThingyListListener();
  }

  private void addThingyListListener() {
    ListView listView = getListView();
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Thingy thingy = (Thingy) parent.getItemAtPosition(position);
        startThingyActivity(thingy);
      }
    });
  }
  
  private void startThingyActivity(Thingy thingy) {
    Intent intent = new Intent(this, ThingyActivity.class).putExtra("thingy", thingy);
    startActivity(intent);
  }

  private void populateThingyList() {
    ArrayAdapter<Thingy> listAdapter = new ArrayAdapter<Thingy>(this,
        R.layout.thingylist_item, _repository.getThingies());
    setListAdapter(listAdapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.thingylistmenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.newThingyMenu:
      newThingy();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  private void newThingy() {
    startActivityForResult(new Intent(this, ScanActivity.class), SCAN_FOR_THINGY_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_CANCELED) {
      Thingy newThingy = (Thingy)data.getExtras().get("thingy");
      _repository.addThingy(newThingy);
    }
  }
  
  

}
