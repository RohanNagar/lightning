package com.sanction.lightning.utils;

import com.restfb.DefaultWebRequestor;

import java.net.HttpURLConnection;

public class CustomWebRequester extends DefaultWebRequestor {
  private static final int chunkSize = 5242880;

  @Override
  public void customizeConnection(HttpURLConnection connection) {
    connection.setChunkedStreamingMode(chunkSize);
  }
}
