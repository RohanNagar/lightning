package com.sanction.lightning.utils;

import java.net.HttpURLConnection;
import org.junit.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CustomWebRequesterTest {
  private final CustomWebRequester webRequester = new CustomWebRequester();

  @Test
  public void testCustomizeConnection() {
    HttpURLConnection connection = mock(HttpURLConnection.class);

    webRequester.customizeConnection(connection);
    verify(connection).setChunkedStreamingMode(anyInt());
  }
}
