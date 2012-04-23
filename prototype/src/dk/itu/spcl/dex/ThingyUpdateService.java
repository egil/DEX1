package dk.itu.spcl.dex;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import dk.itu.spcl.dex.model.Repository;
import dk.itu.spcl.dex.model.Thingy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ThingyUpdateService extends Service implements Runnable {

  private Thread _thread;
  private boolean _running;
  private final static int UPDATE_INTERVAL = 2000;
  private Repository _repository;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("dex", "Service created");
    _repository = Repository.getInstance();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    System.out.println("* Service started!");
    startBackgroundUpdates();
    return START_STICKY;
  }

  private void startBackgroundUpdates() {
    _thread = new Thread(this);
    _running = true;
    _thread.start();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void run() {
    Log.i("dex", "Run called");
    while (_running) {
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
    try {
      boolean thingyStatus = getStatusFromUrl(thingy.getUrl());
      thingy.setStatus(thingyStatus);
    } catch (ClientProtocolException e) {
      Log.e("dex", e.toString());
    } catch (IOException e) {
      Log.e("dex", e.toString());
    }
  }

  private boolean getStatusFromUrl(String url) throws ClientProtocolException,
      IOException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet get = new HttpGet(url);
    ResponseHandler<String> resp = new BasicResponseHandler();
    String responseText = httpClient.execute(get, resp);
    return responseText.equals("1");
  }

  private void pause() {
    try {
      Thread.sleep(UPDATE_INTERVAL);
    } catch (InterruptedException e) {
    }
  }

  @Override
  public void onDestroy() {
    stopBackgroundUpdates();
    super.onDestroy();
  }

  private void stopBackgroundUpdates() {
    _running = false;
  }

}
