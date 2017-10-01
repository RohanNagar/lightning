package com.sanction.lightning.resources;

import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.models.PublishType;
import com.sanction.lightning.models.twitter.TwitterUser;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterResourceTest {
  private final ThunderClient thunderClient = mock(ThunderClient.class);
  private final TwitterServiceFactory serviceFactory = mock(TwitterServiceFactory.class);
  private final MetricRegistry metrics = new MetricRegistry();
  private final TwitterService service = mock(TwitterService.class);
  private final InputStream inputStream = mock(InputStream.class);
  private final FormDataContentDisposition contentDisposition =
      mock(FormDataContentDisposition.class);

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

  /* Publish Tests */
  @Test
  public void testPublishWithNullEmail() {
    Response response = resource.publish(key, null, "password",
        PublishType.TEXT, "message", inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullPassword() {
    Response response = resource.publish(key, "Test", null,
        PublishType.TEXT, "message", inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullType() {
    Response response = resource.publish(key, "Test", "password",
        null, "message", inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithBadTypeValue() {
    Response response = resource.publish(key, "Test", "password",
        PublishType.fromString("Fake"), "message", inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishTextWithNullMessage() {
    Response response = resource.publish(key, "Test", "password",
        PublishType.TEXT, null, inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullInputStream() {
    Response response = resource.publish(key, "Test", "password",
        PublishType.PHOTO, "message", null, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullTwitterResponse() {
    when(service.publish(any(PublishType.class), any(String.class),
        any(String.class), any(InputStream.class)))
        .thenReturn(null);

    Response response = resource.publish(key, "Test", "password",
        PublishType.TEXT, "message", inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testPublishPhotoWithNullMessage() {
    when(service.publish(any(PublishType.class), any(String.class),
        any(String.class), any(InputStream.class)))
        .thenReturn(1L);

    Response response = resource.publish(key, "Test", "password",
        PublishType.PHOTO, null, inputStream, contentDisposition);
    Long result = (Long) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, Long.valueOf(1));
  }

  @Test
  public void testPublishTextWithNullInputStream() {
    when(service.publish(any(PublishType.class), any(String.class),
        any(String.class), any(InputStream.class)))
        .thenReturn(1L);

    Response response = resource.publish(key, "Test", "password",
        PublishType.TEXT, "message", null, null);
    Long result = (Long) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, Long.valueOf(1));
  }

  @Test
  public void testPublish() {
    when(service.publish(any(PublishType.class), any(String.class),
        any(String.class), any(InputStream.class)))
        .thenReturn(1L);

    Response response = resource.publish(key, "Test", "password",
        PublishType.PHOTO, "message", inputStream, contentDisposition);
    Long result = (Long) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, Long.valueOf(1));
  }

  /* OAuth Token Tests */
  @Test
  public void testGetOAuthTokenWithNullRedirect() {
    Response response = resource.getOAuthUrl(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetOAuthTokenFailure() {
    when(service.getAuthorizationUrl(anyString())).thenReturn(null);

    Response response = resource.getOAuthUrl(key, "example.com");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetOAuthTokenSuccess() {
    when(service.getAuthorizationUrl(anyString())).thenReturn("URL");

    Response response = resource.getOAuthUrl(key, "example.com");
    String url = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(url, "URL");
  }
}
