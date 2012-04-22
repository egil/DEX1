package dk.itu.spcl.dex.model;

public class Thingy {

  private String _name;

  public Thingy() {
  }

  public String getName() {
    return _name;
  }

  public Thingy setName(String name) {
    _name = name;
    return this;
  }

  @Override
  public String toString() {
    return getName();
  }
}
