package com.sanction.lightning.resources;

import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.models.PublishType;
import com.sanction.lightning.models.twitter.TwitterAccessToken;
import com.sanction.lightning.models.twitter.TwitterOAuthRequest;
import com.sanction.lightning.models.twitter.TwitterUser;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanctionco.thunder.ThunderClient;
import com.sanctionco.thunder.models.User;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

  private final User user = mock(User.class);
  private final Key key = mock(Key.class);

  private final TwitterResource resource = new TwitterResource(thunderClient, metrics,
      serviceFactory);

  @Before
  public void setup() {
    // Setup TwitterServiceFactory
    when(serviceFactory.newTwitterService()).thenReturn(service);
    when(serviceFactory.newTwitterService(anyString(), anyString())).thenReturn(service);

    // Setup ThunderClient
    when(thunderClient.getUser(anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(user));

    // Setup User
    Map<String, Object> properties = new HashMap<>();
    properties.put("twitter-access-token", "twitterAccessToken");
    properties.put("twitter-access-secret", "twitterAccessSecret");

    when(user.getProperties()).thenReturn(properties);

    // Setup Content
    when(contentDisposition.getFileName()).thenReturn("test-filename.png");
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
        any(), any(InputStream.class)))
        .thenReturn(null);

    Response response = resource.publish(key, "Test", "password",
        PublishType.TEXT, "message", inputStream, contentDisposition);

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testPublishPhotoWithNullMessage() {
    when(service.publish(any(PublishType.class), any(),
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
        any(), any()))
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

  /* OAuth URL Tests */
  @Test
  public void testGetOAuthUrlWithNullRedirect() {
    Response response = resource.getOAuthUrl(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetOAuthUrlFailure() {
    when(service.getAuthorizationUrl(anyString())).thenReturn(null);

    Response response = resource.getOAuthUrl(key, "example.com");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetOAuthUrlSuccess() {
    TwitterOAuthRequest request = new TwitterOAuthRequest(
        "requestToken", "requestSecret", "twitter.com");

    when(service.getAuthorizationUrl(anyString()))
        .thenReturn(request);

    Response response = resource.getOAuthUrl(key, "example.com");
    TwitterOAuthRequest result = (TwitterOAuthRequest) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(result, request);
  }

  /* OAuth Access Token Tests */
  @Test
  public void testGetAccessTokenWithNullRequestToken() {
    Response response = resource.getOAuthAccessToken(key, null, "secret", "verifier");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetAccessTokenWithNullRequestSecret() {
    Response response = resource.getOAuthAccessToken(key, "token", null, "verifier");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetAccessTokenWithNullOAuthVerifier() {
    Response response = resource.getOAuthAccessToken(key, "token", "secret", null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetAccessTokenFailure() {
    when(service.getOAuthAccessToken(anyString(), anyString(), anyString())).thenReturn(null);

    Response response = resource.getOAuthAccessToken(key, "token", "secret", "verifier");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetAccessTokenSuccess() {
    TwitterAccessToken request = new TwitterAccessToken("accessToken", "accessSecret");

    when(service.getOAuthAccessToken(anyString(), anyString(), anyString()))
        .thenReturn(request);

    Response response = resource.getOAuthAccessToken(key, "token", "secret", "verifier");
    TwitterAccessToken result = (TwitterAccessToken) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(result, request);
  }
}
