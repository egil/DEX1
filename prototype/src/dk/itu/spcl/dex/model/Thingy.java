package dk.itu.spcl.dex.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Thingy implements Parcelable {

  private String _name;

  public Thingy() {
  }

  private Thingy(Parcel in) {
    _name = in.readString();
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(_name);
  }

  public static final Parcelable.Creator<Thingy> CREATOR = new Parcelable.Creator<Thingy>() {
    public Thingy createFromParcel(Parcel in) {
      return new Thingy(in);
    }

    public Thingy[] newArray(int size) {
      return new Thingy[size];
    }
  };

}
