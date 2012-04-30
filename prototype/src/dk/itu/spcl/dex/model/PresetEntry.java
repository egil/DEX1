package dk.itu.spcl.dex.model;

public class PresetEntry {
  
  private Thingy _thingy;
  private boolean _status;
  
  public PresetEntry setThingy(Thingy t) {
    _thingy = t;
    return this;
  }
  
  public PresetEntry setStatus(boolean status) {
    _status = status;
    return this;
  }
  
  public Thingy getThingy() {
    return _thingy;
  }
  
  public boolean getStatus() {
    return _status;
  }
  
  @Override
  public String toString() {
    return _thingy.toString() + " (" + (_status ? "on/open" : "off/closed") + ")";
  }

}
