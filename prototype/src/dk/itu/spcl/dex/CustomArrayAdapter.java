package dk.itu.spcl.dex;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dk.itu.spcl.dex.model.Preset;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {

  private Repository _repository;
  private int _textViewResourceId;

  public CustomArrayAdapter(Context context, int resource,
      int textViewResourceId, List<T> objects) {
    super(context, resource, textViewResourceId, objects);
    _textViewResourceId = textViewResourceId;
    _repository = Repository.getInstance();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View itemView = super.getView(position, convertView, parent);
    TextView textView = (TextView) itemView.findViewById(_textViewResourceId);
    T item = getItem(position);
    Drawable typeIcon = getTypeIconForListItem(item);
    if (typeIcon != null)
      typeIcon.setBounds(new Rect(0, 0, 20, 24));
    Drawable statusIcon = getStatusIconForListItem(item);
    if (statusIcon != null)
      statusIcon.setBounds(new Rect(0, 0, 20, 24));
    textView.setCompoundDrawables(typeIcon, null, statusIcon, null);
    return itemView;
  }

  private Drawable getStatusIconForListItem(T item) {
    if (!(item instanceof Thingy) || item == _repository.getDummyThingy())
      return null;

    Thingy thingy = (Thingy) item;
    int iconId;
    if (thingy.getStatus())
      iconId = android.R.drawable.button_onoff_indicator_on;
    else
      iconId = android.R.drawable.button_onoff_indicator_off;
    return getDrawableFromId(iconId);
  }

  private Drawable getDrawableFromId(int iconId) {
    return getContext().getResources().getDrawable(iconId);
  }

  private Drawable getTypeIconForListItem(T listItem) {
    if (listItem == _repository.getDummyPreset()
        || listItem == _repository.getDummyThingy())
      return getDrawableFromId(android.R.drawable.ic_input_add);
    else if (listItem instanceof Preset)
      return null;
    else if (listItem instanceof Thingy)
      return null;
    else
      return null;
  }

}
