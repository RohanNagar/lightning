package com.sanction.lightning.resources;

import com.google.common.collect.Lists;
import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookService;
import com.sanction.lightning.facebook.FacebookServiceFactory;
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
  private final FacebookService facebookService = mock(FacebookService.class);
  private final InputStream inputStream = mock(InputStream.class);
  private final FormDataContentDisposition contentDisposition =
          mock(FormDataContentDisposition.class);

  private final PilotUser pilotUser = mock(PilotUser.class);
  private final Key key = mock(Key.class);

  private final FacebookResource resource = new FacebookResource(thunderClient, serviceFactory);

  @Before
  public void setup() {
    // Setup ServiceFactory
    when(serviceFactory.newFacebookService(anyString())).thenReturn(facebookService);
    when(serviceFactory.newFacebookService()).thenReturn(facebookService);

    // Setup ThunderClient
    when(thunderClient.getUser(anyString())).thenReturn(pilotUser);

    // Setup PilotUser
    when(pilotUser.getFacebookAccessToken()).thenReturn("fbAccessToken");
  }

  /* User Tests */
  @Test
  public void testGetUserWithNullUsername() {
    Response response = resource.getUser(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getUser(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
            Lists.newArrayList(), null));
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getPhotos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserWithExpiredOAuth() {
    when(facebookService.getFacebookUser()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getUser(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetUser() {
    FacebookUser facebookUser = mock(FacebookUser.class);
    when(facebookService.getFacebookUser()).thenReturn(facebookUser);

    Response response = resource.getUser(key, "Test");
    FacebookUser userResponse = (FacebookUser) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, facebookUser);
  }

  /* Photo Tests */
  @Test
  public void testGetPhotosWithNullUsername() {
    Response response = resource.getPhotos(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotosWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getPhotos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotosWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
            Lists.newArrayList(), null));
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getPhotos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotosWithOauthException() {
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.getFacebookUserPhotos()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getPhotos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotos() {
    List<FacebookPhoto> fakeList = Lists.newArrayList();
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.getFacebookUserPhotos()).thenReturn(fakeList);

    Response response = resource.getPhotos(key, "Test");
    List<FacebookPhoto> userResponse = (List<FacebookPhoto>) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, fakeList);
  }

  /* Publish Tests */
  @Test
  public void testPublishWithNullUsername() {
    Response response = resource.publish(key, null, null, null, null, null, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithNullType() {
    Response response = resource.publish(key, "Test", null, null, null, null, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWithBadTypeValue() {
    Response response = resource.publish(key, "Test", null, null, "Test", null, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testPublishWIthNullInputStream() {
    Response response = resource.publish(key, "Test", null, null, "photo", null, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPublishWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.publish(key, "Test", inputStream, contentDisposition, "photo",
            null, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testPublishWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
            Lists.newArrayList(), null));
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.publish(key, "Test", inputStream, contentDisposition, "photo",
            null, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testPublishWithNullFacebookResponse() {
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.publishToFacebook(any(InputStream.class),
            any(String.class), any(String.class), any(String.class),
            any(String.class))).thenReturn(null);
    Response response = resource.publish(key, "Test", inputStream, contentDisposition, "photo",
            "Test", "Test");

    assertEquals(response.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
  }

  @Test
  public void testPublishWithNullVideoTitle() {
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.publishToFacebook(any(InputStream.class),
            any(String.class), any(String.class), any(String.class),
            any(String.class))).thenReturn("Test");
    Response response = resource.publish(key, "Test", inputStream, contentDisposition, "photo",
            "Test", null);
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(result, "Test");
  }

  @Test
  public void testPublishWithNullMessage() {
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.publishToFacebook(any(InputStream.class),
            any(String.class), any(String.class), any(String.class),
            any(String.class))).thenReturn("Test");
    Response response = resource.publish(key, "Test", inputStream, contentDisposition, "photo",
            null, "Test");
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(result, "Test");
  }

  @Test
  public void testPublish() {
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.publishToFacebook(any(InputStream.class),
            any(String.class), any(String.class), any(String.class),
            any(String.class))).thenReturn("Test");
    Response response = resource.publish(key, "Test", inputStream, contentDisposition,
            "photo", "Test", "Test");
    String result = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(result, "Test");
  }

  /* Video Tests */
  @Test
  public void testGetVideosWithNullUsername() {
    Response response = resource.getVideos(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getVideos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
            Lists.newArrayList(), null));
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getVideos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithOauthException() {
    when(facebookService.getFacebookUserVideos()).thenThrow(FacebookOAuthException.class);
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);

    Response response = resource.getVideos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideos() {
    List<FacebookVideo> fakeList = Lists.newArrayList();
    when(facebookService.getFacebookUserVideos()).thenReturn(fakeList);
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);

    Response response = resource.getVideos(key, "Test");
    List<FacebookVideo> userResponse = (List<FacebookVideo>) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, fakeList);
  }

  /* OauthUrl Tests */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetOauthUrlWithOauthException() {
    when(facebookService.getOauthUrl()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getOauthUrl(key);

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetOauthUrl() {
    when(facebookService.getOauthUrl()).thenReturn("Test");

    Response response = resource.getOauthUrl(key);
    String string = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(string, "Test");
  }

  /* ExtendedToken Tests */
  @Test
  public void testGetExtendedTokenWithNullUsername() {
    Response response = resource.getExtendedToken(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithNullRetrofitResponse() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(null);
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getExtendedToken(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithUnknownUser() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
            Lists.newArrayList(), null));
    when(thunderClient.getUser(any(String.class))).thenThrow(error);
    Response response = resource.getExtendedToken(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithOauthException() {
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(facebookService.getFacebookExtendedToken()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getExtendedToken(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetExtendedTokenWithFailedUpdate() {
    RetrofitError error = mock(RetrofitError.class);
    when(error.getResponse()).thenReturn(new retrofit.client.Response("url", 404, "Not Found",
            Lists.newArrayList(), null));
    when(facebookService.getFacebookExtendedToken()).thenReturn("Test");
    when(thunderClient.getUser(any(String.class))).thenReturn(pilotUser);
    when(thunderClient.updateUser(any(PilotUser.class))).thenThrow(error);

    Response response = resource.getExtendedToken(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetExtendedToken() {
    when(facebookService.getFacebookExtendedToken()).thenReturn("Test");
    when(thunderClient.updateUser(any(PilotUser.class))).thenReturn(pilotUser);

    Response response = resource.getExtendedToken(key, "Test");
    String string = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(string, "Test");
  }
}
