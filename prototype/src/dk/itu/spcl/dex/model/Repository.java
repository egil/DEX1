package dk.itu.spcl.dex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import dk.itu.spcl.dex.ScanActivity;

public class Repository {

  public interface Listener {
    public void repositoryStructureChanged();

    public void repositoryStatusChanged();
  }

  HashMap<String, Thingy> _thingies = new HashMap<String, Thingy>();
  HashMap<String, Preset> _presets = new HashMap<String, Preset>();

  private static Repository _instance;
  private ArrayList<Listener> _listeners = new ArrayList<Repository.Listener>();

  private Repository() {
  }

  public void addListener(Listener listener) {
    _listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    _listeners.remove(listener);
  }

  public static Repository getInstance() {
    if (_instance == null)
      _instance = new Repository();
    return _instance;
  }

  public void addThingy(Thingy t) {
    _thingies.put(t.getName(), t);
    onStructureChanged();
  }

  public void addPreset(Preset p) {
    _presets.put(p.getName(), p);
    onStructureChanged();
  }

  private void onStructureChanged() {
    for (Listener listener : _listeners) {
      listener.repositoryStructureChanged();
    }
  }

  public void onStatusChanged() {
    for (Listener listener : _listeners) {
      listener.repositoryStatusChanged();
    }
  }

  public ArrayList<Preset> getPresets() {
    ArrayList<Preset> list = new ArrayList<Preset>(_presets.values());
    Collections.sort(list);
    return list;
  }

  public ArrayList<Thingy> getThingies() {
    ArrayList<Thingy> list = new ArrayList<Thingy>(_thingies.values());
    Collections.sort(list);
    return list;
  }

  public Thingy getThingy(String name) {
    return _thingies.get(name);
  }

  public Preset getPreset(String name) {
    return _presets.get(name);
  }

  private Preset _dummyPreset;
  private Thingy _dummyThingy;
  private PresetEntry _dummyPresetEntry;

  public Preset getDummyPreset() {
    if (_dummyPreset == null) {
      _dummyPreset = new Preset().setName("New preset...");
    }

    return _dummyPreset;
  }

  public Thingy getDummyThingy() {
    if (_dummyThingy == null) {
      _dummyThingy = new Thingy().setName("Install new thingy...");
    }
    return _dummyThingy;
  }

  public PresetEntry getDummyPresetEntry() {
    if (_dummyPresetEntry == null) {
      _dummyPresetEntry = new PresetEntry() {
        @Override
        public String toString() {
          return "Add thingy to preset...";
        }
      };
    }
    return _dummyPresetEntry;
  }

  public void reset() {
    _thingies.clear();
    _presets.clear();
    ScanActivity._thingiesReturned = 0; // ew
    onStructureChanged();
  }

}
