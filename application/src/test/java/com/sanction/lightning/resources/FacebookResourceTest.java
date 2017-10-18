package com.sanction.lightning.resources;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookService;
import com.sanction.lightning.facebook.FacebookServiceFactory;
import com.sanction.lightning.models.PublishType;
import com.sanction.lightning.models.facebook.FacebookOAuthRequest;
import com.sanction.lightning.models.facebook.FacebookPhoto;
import com.sanction.lightning.models.facebook.FacebookUser;
import com.sanction.lightning.models.facebook.FacebookVideo;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;

import java.io.InputStream;
import java.util.List;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Before;
import org.junit.Test;
import retrofit.RetrofitError;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacebookResourceTest {
  private final ThunderClient thunderClient = mock(ThunderClient.class);
  private final FacebookServiceFactory serviceFactory = mock(FacebookServiceFactory.class);
  private final MetricRegistry metrics = new MetricRegistry();
  private final FacebookService facebookService = mock(FacebookService.class);
  private final InputStream inputStream = mock(InputStream.class);
  private final FormDataContentDisposition contentDisposition =
      mock(FormDataContentDisposition.class);

  private final PilotUser pilotUser = mock(PilotUser.class);
  private final Key key = mock(Key.class);

  private final FacebookResource resource = new FacebookResource(thunderClient, metrics,
      serviceFactory);

  @Before
  public void setup() {
    // Setup ServiceFactory
    when(serviceFactory.newFacebookService(anyString())).thenReturn(facebookService);
    when(serviceFactory.newFacebookService()).thenReturn(facebookService);

    // Setup ThunderClient
    when(thunderClient.getUser(anyString(), anyString())).thenReturn(pilotUser);

    // Setup PilotUser
    when(pilotUser.getFacebookAccessToken()).thenReturn("fbAccessToken");
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
  @SuppressWarnings("unchecked")
  public void testGetUserWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getUser(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetUserWithUnauthorizedResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(
        new retrofit.client.Response("url", 401, "Unauthorized", Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getUser(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(
        new retrofit.client.Response("url", 404, "Not Found", Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getPhotos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserWithExpiredOAuth() {
    when(facebookService.getFacebookUser()).thenReturn(null);

    Response response = resource.getUser(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetUser() {
    FacebookUser facebookUser = mock(FacebookUser.class);
    when(facebookService.getFacebookUser()).thenReturn(facebookUser);

    Response response = resource.getUser(key, "Test", "password");
    FacebookUser userResponse = (FacebookUser) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, facebookUser);
  }

  /* Photo Tests */
  @Test
  public void testGetPhotosWithNullEmail() {
    Response response = resource.getPhotos(key, null, "password");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetPhotosWithNullPassword() {
    Response response = resource.getPhotos(key, "Test", null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotosWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getPhotos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetPhotosWithUnauthorizedResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(
        new retrofit.client.Response("url", 401, "Unauthorized", Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getPhotos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotosWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(
        new retrofit.client.Response("url", 404, "Not Found", Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getPhotos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotosWithExpiredOauth() {
    when(facebookService.getFacebookUserPhotos()).thenReturn(null);

    Response response = resource.getPhotos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotos() {
    List<FacebookPhoto> fakeList = Lists.newArrayList();
    when(facebookService.getFacebookUserPhotos()).thenReturn(fakeList);

    Response response = resource.getPhotos(key, "Test", "password");
    List<FacebookPhoto> userResponse = (List<FacebookPhoto>) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, fakeList);
  }

  /* Video Tests */
  @Test
  public void testGetVideosWithNullEmail() {
    Response response = resource.getVideos(key, null, "password");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetVideosWithNullPassword() {
    Response response = resource.getVideos(key, "Test", null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getVideos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetVideosWithUnauthorizedResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 401, "Unauthorized",
        Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getVideos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
        Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getVideos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithExpiredOauth() {
    when(facebookService.getFacebookUserVideos()).thenReturn(null);

    Response response = resource.getVideos(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideos() {
    List<FacebookVideo> fakeList = Lists.newArrayList();
    when(facebookService.getFacebookUserVideos()).thenReturn(fakeList);

    Response response = resource.getVideos(key, "Test", "password");
    List<FacebookVideo> userResponse = (List<FacebookVideo>) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, fakeList);
  }

  /* Publish Tests */
  @Test
  public void testPublishWithNullEmail() {
    Response response = resource.publish(key, null, "password", PublishType.TEXT,
        "Test Message", inputStream, contentDisposition, "Test Title");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullPassword() {
    Response response = resource.publish(key, "Test", null, PublishType.TEXT,
        "Test Message", inputStream, contentDisposition, "Test Title");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullType() {
    Response response = resource.publish(key, "Test", "password", null,
        "Test Message", inputStream, contentDisposition, "Test Title");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithBadTypeValue() {
    Response response = resource.publish(key, "Test", "password", PublishType.fromString("Fake"),
        "Test Message", inputStream, contentDisposition, "Test Title");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishTextWithNullMessage() {
    Response response = resource.publish(key, "Test", "password", PublishType.TEXT,
        null, inputStream, contentDisposition, "Test Title");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishPhotoWithNullInputStream() {
    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test Message", null, null, "Test Title");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPublishWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test", inputStream, contentDisposition, null);

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testPublishWithUnauthorizedResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(
        new retrofit.client.Response("url", 401, "Unauthorized", Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test", inputStream, contentDisposition, null);

    assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPublishWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(
        new retrofit.client.Response("url", 404, "Not Found", Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test", inputStream, contentDisposition, null);

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testPublishWithNullFacebookResponse() {
    when(facebookService.publish(any(InputStream.class),
        any(PublishType.class), any(String.class), any(String.class),
        any(String.class))).thenReturn(null);

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test", inputStream, contentDisposition, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testPublishTextWithNullInputStream() {
    when(facebookService.publish(any(InputStream.class),
        any(PublishType.class), any(String.class), any(String.class),
        any(String.class))).thenReturn("Test");

    Response response = resource.publish(key, "Test", "password", PublishType.TEXT,
        "Test", null, null, null);
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, "Test");
  }

  @Test
  public void testPublishPhotoWithNullVideoTitle() {
    when(facebookService.publish(any(InputStream.class),
        any(PublishType.class), any(String.class), any(String.class),
        any(String.class))).thenReturn("Test");

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test", inputStream, contentDisposition, null);
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, "Test");
  }

  @Test
  public void testPublishPhotoWithNullMessage() {
    when(facebookService.publish(any(InputStream.class),
        any(PublishType.class), any(String.class), any(String.class),
        any(String.class))).thenReturn("Test");

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        null, inputStream, contentDisposition, "Test");
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, "Test");
  }

  @Test
  public void testPublish() {
    when(facebookService.publish(any(InputStream.class),
        any(PublishType.class), any(String.class), any(String.class),
        any(String.class))).thenReturn("Test");

    Response response = resource.publish(key, "Test", "password", PublishType.PHOTO,
        "Test", inputStream, contentDisposition, "Test");
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.CREATED);
    assertEquals(result, "Test");
  }

  /* ExtendedToken Tests */
  @Test
  public void testGetExtendedTokenWithNullEmail() {
    Response response = resource.getExtendedToken(key, null, "password");

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetExtendedTokenWithNullPassword() {
    Response response = resource.getExtendedToken(key, "Test", null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getExtendedToken(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetExtendedTokenWithUnauthorizedResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 401, "Unauthorized",
        Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getExtendedToken(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
        Lists.newArrayList(), null));
    when(thunderClient.getUser(anyString(), anyString())).thenThrow(error);

    Response response = resource.getExtendedToken(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithExpiredOauth() {
    when(facebookService.getFacebookExtendedToken()).thenReturn(null);

    Response response = resource.getExtendedToken(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithFailedUpdate() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
        Lists.newArrayList(), null));
    when(facebookService.getFacebookExtendedToken()).thenReturn("Test");
    when(thunderClient.updateUser(any(PilotUser.class), anyString())).thenThrow(error);

    Response response = resource.getExtendedToken(key, "Test", "password");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetExtendedToken() {
    when(facebookService.getFacebookExtendedToken()).thenReturn("Test");
    when(thunderClient.updateUser(any(PilotUser.class), anyString())).thenReturn(pilotUser);

    Response response = resource.getExtendedToken(key, "Test", "password");
    String string = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(string, "Test");
  }

  /* OauthUrl Tests */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetOauthUrlWithNullRedirect() {
    Response response = resource.getOauthUrl(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetOauthUrlWithExpiredOauth() {
    when(facebookService.getOauthUrl(anyString())).thenReturn(null);

    Response response = resource.getOauthUrl(key, "example.com");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetOauthUrl() {
    FacebookOAuthRequest expected = new FacebookOAuthRequest("Test");
    when(facebookService.getOauthUrl(anyString())).thenReturn("Test");

    Response response = resource.getOauthUrl(key, "example.com");
    FacebookOAuthRequest authRequest = (FacebookOAuthRequest) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(expected, authRequest);
  }
}
