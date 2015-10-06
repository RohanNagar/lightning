package com.sanction.lightning.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlServiceTest {
  private final UrlService urlService = new UrlService();

  @Test
  public void testFetchUrlConnectionFailure() {
    URLConnection connection = urlService.fetchUrlConnection("BAD_URL");

    assertNull(connection);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchInputStreamFromConnectionFailure() throws Exception {
    URLConnection connection = mock(URLConnection.class);

    when(connection.getInputStream()).thenThrow(IOException.class);

    InputStream stream = urlService.fetchInputStreamFromConnection(connection);
    assertNull(stream);
  }

  @Test
  public void testFetchInputStreamFromConnection() throws Exception {
    URLConnection connection = mock(URLConnection.class);
    InputStream stream = mock(InputStream.class);

    when(connection.getInputStream()).thenReturn(stream);

    InputStream result = urlService.fetchInputStreamFromConnection(connection);
    assertEquals(stream, result);
  }

}
