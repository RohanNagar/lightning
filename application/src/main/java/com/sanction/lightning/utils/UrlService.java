package com.sanction.lightning.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UrlService {

  public UrlService() {

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
   * Fetch an InputStream from a given URLConnection.
   * @param connection The supplied URLConnection.
   * @return InputStream of the URLConnection.
   */
  public InputStream fetchInputStreamFromConnection(URLConnection connection) {
    InputStream inputStream;

    try {
      inputStream = connection.getInputStream();
    } catch (IOException e) {
      return null;
    }

    return inputStream;
  }

  /**
   * Constructs a byte array from the given InputStream.
   * @param inputStream the supplied URLConnection
   * @return a byte array
   */
  public byte[] inputStreamToByteArray(InputStream inputStream) {
    byte[] response;

    try (InputStream in = new BufferedInputStream(inputStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      // Construct a byte array from the InputStream
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
