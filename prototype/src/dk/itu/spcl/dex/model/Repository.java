package dk.itu.spcl.dex.model;

import java.util.*;

public class Repository {
  
  private ArrayList<Thingy> _thingies = new ArrayList<Thingy>();
  private static Repository _instance;
  
  private Repository() {
    // test data
    _thingies.add(new Thingy().setName("test thingy"));
    _thingies.add(new Thingy().setName("other thingy"));
  }
  
  public static Repository getInstance() {
    if (_instance == null)
      _instance = new Repository();
    return _instance;
  }  

  public ArrayList<Thingy> getThingies() {
    return _thingies;
  }

  public void addThingy(Thingy t) {
    _thingies.add(t);
  }

}
