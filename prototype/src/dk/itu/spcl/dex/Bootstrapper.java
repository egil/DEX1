package dk.itu.spcl.dex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Bootstrapper {

  private WifiManager _wifiManager;
  private String _ssid;
  private String _key;

  public Bootstrapper(Context context, String ssid, String key) {
    _wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    _ssid = ssid;
    _key = key;
  }

  public void runBootstrapping() {
    try {
      toggleWifi(false);
      toggleHotspot(true);
      waitForConnection();
      pause(); // give the thingy a chance to disconnect
      toggleHotspot(false);
      toggleWifi(true);
    } catch (Exception e) {
      Log.e("dex", e.toString());
    }
  }

  private void pause() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
  }

  private void waitForConnection() throws IOException {
    Log.i("dex", "Listening");
    ServerSocket server = new ServerSocket(44444);
    Socket connection = server.accept();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        connection.getOutputStream(), Charset.forName("US-ASCII")));
    String response = "HTTP/1.1 200 OK\n" + "Content-Type: text/html\n" + "\n"
        + Settings.HOME_SSID + "/" + Settings.HOME_PSK + "\n";
    writer.write(response);
    writer.flush();
    writer.close();
    server.close();
    Log.i("dex", "Sent response");
  }

  private void toggleHotspot(boolean enable) throws IllegalArgumentException,
      IllegalAccessException, InvocationTargetException, SecurityException,
      NoSuchMethodException {
    Log.i("dex", "Setting up AP");
    WifiConfiguration wifiConfig = new WifiConfiguration();
    wifiConfig.SSID = _ssid;
    wifiConfig.preSharedKey = _key;
    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
    Method method = _wifiManager.getClass().getMethod("setWifiApEnabled",
        WifiConfiguration.class, boolean.class);
    method.invoke(_wifiManager, wifiConfig, enable);
  }

  private void toggleWifi(boolean enable) {
    _wifiManager.setWifiEnabled(enable);
  }

}
