package com.sanction.lightning.resources;

import com.google.common.collect.Lists;
import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookProvider;
import com.sanction.lightning.facebook.FacebookProviderFactory;
import com.sanction.lightning.models.FacebookUser;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;

import java.util.List;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacebookResourceTest {
  private final ThunderClient thunderClient = mock(ThunderClient.class);
  private final FacebookProviderFactory providerFactory = mock(FacebookProviderFactory.class);
  private final FacebookProvider facebookProvider = mock(FacebookProvider.class);

  private final PilotUser pilotUser = mock(PilotUser.class);
  private final Key key = mock(Key.class);

  private final FacebookResource resource = new FacebookResource(thunderClient, providerFactory);

  @Before
  public void setup() {
    // Setup ProviderFactory
    when(providerFactory.newFacebookProvider(anyString())).thenReturn(facebookProvider);
    when(providerFactory.newFacebookProvider()).thenReturn(facebookProvider);

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
  public void testGetUserWithExpiredOAuth() {
    when(facebookProvider.getFacebookUser()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getUser(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetUser() {
    FacebookUser facebookUser = mock(FacebookUser.class);
    when(facebookProvider.getFacebookUser()).thenReturn(facebookUser);

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
  public void testGetPhotosWithOauthException() {
    when(facebookProvider.getFacebookUserPhotos()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getPhotos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetPhotos() {
    List<String> fakeList = Lists.newArrayList();
    when(facebookProvider.getFacebookUserPhotos()).thenReturn(fakeList);

    Response response = resource.getPhotos(key, "Test");
    List<String> userResponse = (List<String>) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, fakeList);
  }

  /* Video Tests */
  @Test
  public void testGetVideosWithNullUsername() {
    Response response = resource.getVideos(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideosWithOauthException() {
    when(facebookProvider.getFacebookUserVideos()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getVideos(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetVideos() {
    List<String> fakeList = Lists.newArrayList();
    when(facebookProvider.getFacebookUserVideos()).thenReturn(fakeList);

    Response response = resource.getVideos(key, "Test");
    List<String> userResponse = (List<String>) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, fakeList);
  }

  /* OauthUrl Tests */
  @Test
  @SuppressWarnings("unchecked")
  public void testGetOauthUrlWithOauthException() {
    when(facebookProvider.getOauthUrl()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getOauthUrl(key);

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetOauthUrl() {
    when(facebookProvider.getOauthUrl()).thenReturn("Test");

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
  public void testGetExtendedTokenWithOauthException() {
    when(facebookProvider.getFacebookExtendedToken()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getExtendedToken(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

  @Test
  public void testGetExtendedTokenWithFailedUpdate() {
    String testToken = "Test";
    when(facebookProvider.getFacebookExtendedToken()).thenReturn(testToken);
    when(thunderClient.postUser(any(PilotUser.class))).thenReturn(null);

    Response response = resource.getExtendedToken(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.SERVICE_UNAVAILABLE);
  }

  @Test
  public void testGetExtendedToken() {
    String testToken = "Test";
    when(facebookProvider.getFacebookExtendedToken()).thenReturn(testToken);
    when(thunderClient.updateUser(any(PilotUser.class))).thenReturn(pilotUser);

    Response response = resource.getExtendedToken(key, "Test");
    String string = (String) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(string, testToken);
  }
}
