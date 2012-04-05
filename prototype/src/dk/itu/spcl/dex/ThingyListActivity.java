package dk.itu.spcl.dex;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class ThingyListActivity extends ListActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setListAdapter(new ArrayAdapter<String>(this, R.layout.thingylist_item,
        new String[] { "one", "two" }));

    // ListView lv = getListView();
    // lv.setTextFilterEnabled(true);
    //
    // lv.setOnItemClickListener(new OnItemClickListener() {
    // public void onItemClick(AdapterView<?> parent, View view,
    // int position, long id) {
    // // When clicked, show a toast with the TextView text
    // Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
    // Toast.LENGTH_SHORT).show();
    // }
    // });

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
    startActivity(new Intent(this, ScanActivity.class));
  }

}
