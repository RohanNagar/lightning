package com.sanction.lightning.resources;

import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanction.thunder.ThunderClient;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterResourceTest {
  private final ThunderClient thunderClient = mock(ThunderClient.class);
  private final TwitterServiceFactory serviceFactory = mock(TwitterServiceFactory.class);
  private final TwitterService service = mock(TwitterService.class);

  private final Key key = mock(Key.class);

  private final TwitterResource resource = new TwitterResource(thunderClient, serviceFactory);

  @Before
  public void setup() {
    // Setup TwitterServiceFactory
    when(serviceFactory.newTwitterService()).thenReturn(service);
    when(serviceFactory.newTwitterService(anyString(), anyString())).thenReturn(service);
  }

  @Test
  public void testGetOAuthTokenFailure() {
    when(service.getAuthorizationUrl()).thenReturn(null);

    Response response = resource.getOAuthToken(key);

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetOAuthTokenSuccess() {
    when(service.getAuthorizationUrl()).thenReturn("URL");

    Response response = resource.getOAuthToken(key);
    String url = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(url, "URL");
  }
}
