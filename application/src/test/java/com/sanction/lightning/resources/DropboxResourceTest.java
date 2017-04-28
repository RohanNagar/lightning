package com.sanction.lightning.resources;

import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.dropbox.DropboxService;
import com.sanction.lightning.dropbox.DropboxServiceFactory;
import com.sanction.thunder.ThunderClient;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DropboxResourceTest {
  private final ThunderClient thunderClient = mock(ThunderClient.class);
  private final MetricRegistry metrics = new MetricRegistry();
  private final DropboxServiceFactory serviceFactory = mock(DropboxServiceFactory.class);
  private final DropboxService dropboxService = mock(DropboxService.class);

  private final Key key = mock(Key.class);

  private final DropboxResource resource = new DropboxResource(thunderClient, metrics,
      serviceFactory);

  @Before
  public void setup() {
    // Setup ServiceFactory
    when(serviceFactory.newDropboxService()).thenReturn(dropboxService);
  }

  @Test
  public void testGetOauthUrl() {
    when(dropboxService.getOauthUrl()).thenReturn("Test");

    Response response = resource.getOauthUrl(key);
    String string = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(string, "Test");
  }
}
