package dk.itu.spcl.dex;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import dk.itu.spcl.dex.juidl.UIGenerator;
import dk.itu.spcl.dex.juidl.Widget;
import dk.itu.spcl.dex.juidl.WidgetType;
import dk.itu.spcl.dex.model.Thingy;

public class AndroidUIGenerator extends UIGenerator {
  
  private Thingy _thingy;
  private Activity _activty;
  private LinearLayout _layout;
  
  public AndroidUIGenerator(Thingy thingy, Activity activity) {
    _thingy = thingy;
    _activty = activity;
  }

  @Override
  protected void generateCanvas(String title, String description, double version) {
    _layout = new LinearLayout(_activty);
    _activty.setContentView(_layout);
  }

  @Override
  protected void generateWidget(Widget w, String content) {
    // not implemented    
  }

  @Override
  protected void generateWidget(Widget w, String[] contentArray) {
    // not implemented    
  }

  @Override
  protected void generateWidget(Widget w, boolean content) {
    ToggleButton button = new ToggleButton(_activty);
    button.setEnabled(w.getType() != WidgetType.READ_ONLY);
    _layout.addView(button);
  }

  @Override
  protected void generateWidget(Widget w, boolean[] contentArray) {
    // not implemented    
  }

  @Override
  protected void generateWidget(Widget w, int content) {
    // not implemented
  }

  @Override
  protected void generateWidget(Widget w, int[] contentArray) {
    // not implemented
  }

}
