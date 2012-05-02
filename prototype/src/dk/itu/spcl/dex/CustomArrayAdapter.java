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
import dk.itu.spcl.dex.model.PresetEntry;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {

  private Repository _repository;
  private int _textViewResourceId;

  private enum CheckStatus {
    NONE, CHECK, FAIL
  };

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

    setDrawablesForTextView(textView, item);

    return itemView;
  }

  private void setDrawablesForTextView(TextView textView, T item) {
    Drawable typeIcon = getTypeIconForListItem(item);
    if (typeIcon != null)
      typeIcon.setBounds(new Rect(0, 0, 36, 36));
    Drawable statusIcon = getStatusIconForListItem(item);
    if (statusIcon != null)
      statusIcon.setBounds(new Rect(0, 0, 36, 36));
    textView.setCompoundDrawables(typeIcon, null, statusIcon, null);
  }

  private Drawable getStatusIconForListItem(T item) {
    if (!(item instanceof Thingy || item instanceof PresetEntry)
        || item == _repository.getDummyThingy()
        || item == _repository.getDummyPresetEntry())
      return null;

    Thingy thingy;
    if (item instanceof PresetEntry)
      thingy = ((PresetEntry) item).getThingy();
    else
      thingy = (Thingy) item;

    boolean on = thingy.getStatus();
    CheckStatus checkStatus = getCheckStatus(item);

    int iconId = -1;
    switch (checkStatus) {
    case CHECK:
      iconId = on ? R.drawable.on_check : R.drawable.off_check;
      break;
    case FAIL:
      iconId = on ? R.drawable.on_nocheck : R.drawable.off_nocheck;
      break;
    case NONE:
      iconId = on ? R.drawable.on : R.drawable.off;
      break;
    }
    return getDrawableFromId(iconId);
  }

  private CheckStatus getCheckStatus(T item) {
    if (!(item instanceof PresetEntry))
      return CheckStatus.NONE;

    PresetEntry entry = (PresetEntry) item;
    if (entry.getStatus() != entry.getThingy().getStatus())
      return CheckStatus.FAIL;
    else
      return CheckStatus.CHECK;
  }

  private Drawable getDrawableFromId(int iconId) {
    return getContext().getResources().getDrawable(iconId);
  }

  private Drawable getTypeIconForListItem(T listItem) {
    if (listItem == _repository.getDummyPreset()
        || listItem == _repository.getDummyThingy()
        || listItem == _repository.getDummyPresetEntry())
      return getDrawableFromId(android.R.drawable.ic_input_add);
    else if (listItem instanceof Preset)
      return null;
    else if (listItem instanceof Thingy)
      return null;
    else
      return null;
  }

}
