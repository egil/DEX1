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
    Drawable icon = getDrawableForListItem(getItem(position));
    if (icon != null)
      icon.setBounds(new Rect(0, 0, 20, 24));
    textView.setCompoundDrawables(icon, null, null, null);
    return itemView;
  }

  private Drawable getDrawableForListItem(T listItem) {
    if (listItem == _repository.getDummyPreset()
        || listItem == _repository.getDummyThingy())
      return getContext().getResources().getDrawable(
          android.R.drawable.ic_input_add);
    else if (listItem instanceof Preset)
      return null;
    else if (listItem instanceof Thingy)
      return null;
    else
      return null;

  }

}
