package dk.itu.spcl.dex.model;

import java.util.HashMap;

import dk.itu.spcl.dex.juidl.Widget;

public class Thingy implements Comparable<Thingy> {

  private String _name;
  private String _url;
  private boolean _status;
  private boolean _statusChangeQueued = false;

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
  
  public boolean getStatus() {
    return _status;
  }
  
  public Thingy setStatus(boolean status) {
    _status = status;
    return this;
  }
  
  public boolean isStatusChangeQueued() {
    return _statusChangeQueued;
  }

  public Thingy setStatusChangeQueued(boolean statusChangeQueued) {
    _statusChangeQueued = statusChangeQueued;
    return this;
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
