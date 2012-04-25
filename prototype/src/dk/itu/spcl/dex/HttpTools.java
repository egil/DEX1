package dk.itu.spcl.dex;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpTools {

  public static String readStringFromUrl(String url) throws IOException,
      ClientProtocolException {
    HttpClient httpClient = new DefaultHttpClient();
    HttpGet get = new HttpGet(url);
    ResponseHandler<String> resp = new BasicResponseHandler();
    String responseText = httpClient.execute(get, resp);
    return responseText;
  }

}
