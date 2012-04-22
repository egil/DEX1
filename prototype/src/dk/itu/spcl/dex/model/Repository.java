package dk.itu.spcl.dex.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Repository {

  public interface UpdateListener {
    public void repositoryUpdated();
  }

  HashMap<String, Thingy> _thingies = new HashMap<String, Thingy>();
  HashMap<String, Preset> _presets = new HashMap<String, Preset>();

  private static Repository _instance;
  private ArrayList<UpdateListener> _updateListeners = new ArrayList<Repository.UpdateListener>();

  private Repository() {
    // test data
    addThingy(new Thingy().setName("test thingy"));
    addThingy(new Thingy().setName("other thingy"));
    addThingy(new Thingy().setName("third thingy"));

    Preset samplePreset = new Preset().setName("sample preset");
    samplePreset.addThingy(getThingies().get(0));
    samplePreset.addThingy(getThingies().get(1));
    addPreset(samplePreset);
  }

  public void addUpdateListener(UpdateListener listener) {
    _updateListeners.add(listener);
  }

  public void removeUpdateListener(UpdateListener listener) {
    _updateListeners.remove(listener);
  }

  public static Repository getInstance() {
    if (_instance == null)
      _instance = new Repository();
    return _instance;
  }

  public void addThingy(Thingy t) {
    _thingies.put(t.getName(), t);
    onUpdate();
  }

  public void addPreset(Preset p) {
    _presets.put(p.getName(), p);
    onUpdate();
  }

  private void onUpdate() {
    for (UpdateListener listener : _updateListeners) {
      listener.repositoryUpdated();
    }
  }

  public ArrayList<Preset> getPresets() {
    return new ArrayList<Preset>(_presets.values());
  }
  
  public ArrayList<Thingy> getThingies() {
    return new ArrayList<Thingy>(_thingies.values());
  }
  
  public Thingy getThingy(String name) {
    return _thingies.get(name);
  }
  
  public Preset getPreset(String name) {
    return _presets.get(name);
  }

}
