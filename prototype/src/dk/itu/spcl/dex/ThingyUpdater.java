package dk.itu.spcl.dex;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;


import android.util.Log;
import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;
import dk.itu.spcl.dex.tools.HttpTools;

public class ThingyUpdater implements Runnable {

  private Thread _runningThread;
  private final static int UPDATE_INTERVAL = 500;
  private Repository _repository;
  private ArrayList<Object> _listeners = new ArrayList<Object>();
  private static ThingyUpdater _instance;

  private ThingyUpdater() {
    _repository = Repository.getInstance();
  }
  
  public static ThingyUpdater getInstance() {
    if (_instance == null) {
      _instance = new ThingyUpdater();
    }
    return _instance;
  }
 
  public void requestUpdatesFor(Object me) {
    _listeners.add(me);
    toggleUpdatesAsNeeded();
  }

  public void cancelUpdatesFor(Object me) {
    _listeners.remove(me);
    toggleUpdatesAsNeeded();
  }

  private synchronized void toggleUpdatesAsNeeded() {
    Log.i("dex", _listeners.size() + " activity/ies need updates");
    
    if (_listeners.size() == 0)
      stopBackgroundUpdates();
    else
      startBackgroundUpdates();
  }

  private void startBackgroundUpdates() {
    if (_runningThread != null)
      return;

    _runningThread = new Thread(this);
    _runningThread.start();
  }

  private void stopBackgroundUpdates() {
    _runningThread = null;
  }

  @Override
  public void run() {
    while (Thread.currentThread() == _runningThread) {
      update();
      pause();
    }
  }

  private void update() {
    Log.i("dex", "Updating");
    for (Thingy thingy : _repository.getThingies())
      updateThingy(thingy);

    _repository.onStatusChanged();
  }

  private void updateThingy(Thingy thingy) {
//    try {
//      boolean thingyStatus = getStatusFromUrl(Settings.WIZARD_URL + thingy.getUrl());
//      thingy.setStatus(thingyStatus);
//    } catch (ClientProtocolException e) {
//      Log.e("dex", e.toString());
//    } catch (IOException e) {
//      Log.e("dex", e.toString());
//    }
  }

  private boolean getStatusFromUrl(String url) throws ClientProtocolException,
      IOException {
    String responseText = HttpTools.readStringFromUrl(url);
    return responseText.equals("1");
  }

  private void pause() {
    try {
      Thread.sleep(UPDATE_INTERVAL);
    } catch (InterruptedException e) {
    }
  }
}
