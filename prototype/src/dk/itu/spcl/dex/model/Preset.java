package dk.itu.spcl.dex.model;

import java.util.ArrayList;

public class Preset implements Comparable<Preset> {

  private ArrayList<PresetEntry> _entries;
  private String _name;

  public Preset() {
    _entries = new ArrayList<PresetEntry>();
  }

  public Preset setName(String name) {
    _name = name;
    return this;
  }

  public String getName() {
    return _name;
  }

  public void addEntry(PresetEntry e) {
    _entries.add(e);
  }

  public ArrayList<PresetEntry> getEntries() {
    return _entries;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public int compareTo(Preset another) {
    return getName().compareTo(another.getName());
  }
}
