package dk.itu.spcl.dex;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import dk.itu.spcl.dex.model.Thingy;
import dk.itu.spcl.dex.tools.HttpTools;

public class ThingyStatusWriter {

  public void setStatus(Thingy thingy, boolean status) {
    if (thingy.isReadOnly())
      return;
    
    String url = Settings.WIZARD_URL + "write.php?thingy=" + thingy.getUrl()
        + "&status=" + (status ? 1 : 0);
    try {
      HttpTools.readStringFromUrl(url);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void flushWizard() {
    String url = Settings.WIZARD_URL + "write.php?setLast=1";
    try {
      HttpTools.readStringFromUrl(url);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
