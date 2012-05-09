package dk.itu.spcl.dex.model;

import java.util.HashMap;

import dk.itu.spcl.dex.juidl.Widget;

public class Thingy implements Comparable<Thingy> {

  private String _name;
  private String _url;
  private HashMap<String, Widget> _widgets = new HashMap<String, Widget>();

  public Thingy() {
  }
  
  // properties

  public String getName() {
    return _name;
  }
  
  public Thingy setName(String name) {
    _name = name;
    return this;
  }
  
  public String getUrl() {
    return _url;
  }
  
  public Thingy setUrl(String url) {
    _url = url;
    return this;
  }
    
  public void addWidget(Widget widget) {
    _widgets.put(widget.getId(), widget);
  }
  
  public boolean getStatus() {
    return false;
  }
  
  // standard overrides

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int compareTo(Thingy another) {
    return getName().compareTo(another.getName());
  }
}
