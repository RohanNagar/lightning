package com.sanction.lightning.resources;

import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.models.twitter.TwitterUser;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;

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
  private final MetricRegistry metrics = new MetricRegistry();
  private final TwitterService service = mock(TwitterService.class);

  private final PilotUser pilotUser = mock(PilotUser.class);
  private final Key key = mock(Key.class);

  private final TwitterResource resource = new TwitterResource(thunderClient, metrics,
      serviceFactory);

  @Before
  public void setup() {
    // Setup TwitterServiceFactory
    when(serviceFactory.newTwitterService()).thenReturn(service);
    when(serviceFactory.newTwitterService(anyString(), anyString())).thenReturn(service);

    // Setup ThunderClient
    when(thunderClient.getUser(anyString(), anyString())).thenReturn(pilotUser);

    // Setup PilotUser
    when(pilotUser.getTwitterAccessToken()).thenReturn("twitterAccessToken");
    when(pilotUser.getTwitterAccessSecret()).thenReturn("twitterAccessSecret");
  }

  /* User Tests */
  @Test
  public void testGetUserWithNullEmail() {
    Response response = resource.getUser(key, null, "password");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetUserWithNullPassword() {
    Response response = resource.getUser(key, "Test", null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetUserWithNullTwitterResponse() {
    when(service.getTwitterUser()).thenReturn(null);

    Response response = resource.getUser(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetUserSuccess() {
    TwitterUser mockUser = mock(TwitterUser.class);
    when(service.getTwitterUser()).thenReturn(mockUser);

    Response response = resource.getUser(key, "Test", "password");
    TwitterUser user = (TwitterUser) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(user, mockUser);
  }

  /* OAuth Token Tests */
  @Test
  public void testGetOAuthTokenFailure() {
    when(service.getAuthorizationUrl()).thenReturn(null);

    Response response = resource.getOAuthUrl(key);

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetOAuthTokenSuccess() {
    when(service.getAuthorizationUrl()).thenReturn("URL");

    Response response = resource.getOAuthUrl(key);
    String url = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(url, "URL");
  }
}
