package dk.itu.spcl.dex.model;

public class Thingy implements Comparable<Thingy> {

  private String _name;
  private String _url;
  private boolean _status;

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
  
  // status (to be expanded)
  
  public boolean getStatus() {
    return _status;
  }
  
  public Thingy setStatus(boolean status) {
    _status = status;
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
