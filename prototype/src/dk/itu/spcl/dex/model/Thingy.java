package dk.itu.spcl.dex.model;

public class Thingy {

  private String _name;

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }
  
  @Override
  public String toString() {
    return getName();
  }

}
