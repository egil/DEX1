package dk.itu.spcl.dex.model;

import java.util.ArrayList;

public class Preset {
  
  private ArrayList<Thingy> _thingies;
  private String _name;
  
  public Preset() {
    _thingies = new ArrayList<Thingy>(); 
  }
 
  public Preset setName(String name) {
    _name = name;
    return this;
  }
  
  public String getName() {
    return _name;
  }
  
  public void addThingy(Thingy t) {
    _thingies.add(t);
  }
  
  public ArrayList<Thingy> getThingies() {
    return _thingies;
  }
  
  @Override
  public String toString() {
    return getName();
  }
}
