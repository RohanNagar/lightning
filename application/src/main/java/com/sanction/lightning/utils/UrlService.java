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
   * Constructs a new URLConnection for connecting to a provided URL.
   *
   * @param url The URL to open the connection to.
   * @return The URLConnection that was opened or {@code null} if an error occurred.
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
   * Fetches an InputStream from a given URLConnection.
   *
   * @param connection The supplied URLConnection.
   * @return The InputStream of the URLConnection or {@code null} if an error occurred.
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
   * Reads bytes from an InputStream into a byte array.
   *
   * @param inputStream The InputStream to read from.
   * @return A byte array containing the data read from the InputStream or {@code null} upon error.
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
