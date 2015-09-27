package com.sanction.lightning.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UrlDownloadService {

  public UrlDownloadService() {

  }

  /**
   * Construct a new URLConnection for connecting to a provided URL.
   * @param url a supplied url
   * @return a URLConnection
   */
  public URLConnection fetchUrlConnection(String url) {
    URLConnection connection;
    try {
      connection = new URL(url).openConnection();
    } catch (IOException e) {
      return null;
    }

    return connection;
  }

  /**
   * Constructs a new byte array from the resource at a given URLConnection.
   * @param connection the supplied URLConnection
   * @return a byte array
   */
  public byte[] inputStreamToByteArray(URLConnection connection) {
    byte[] response;

    try (InputStream in = new BufferedInputStream(connection.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      // Construct a byte array from an InputStream
      byte[] buf = new byte[1024];
      int n;
      while (-1 != (n = in.read(buf))) {
        out.write(buf, 0, n);
      }

      response = out.toByteArray();
    } catch (IOException e) {
      return null;
    }

    return response;
  }
}
