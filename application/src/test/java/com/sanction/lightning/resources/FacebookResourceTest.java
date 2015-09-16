package com.sanction.lightning.resources;

import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookProvider;
import com.sanction.lightning.facebook.FacebookProviderFactory;
import com.sanction.lightning.models.FacebookUser;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.StormUser;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FacebookResourceTest {
  private final ThunderClient client = mock(ThunderClient.class);
  private final FacebookProviderFactory providerFactory = mock(FacebookProviderFactory.class);
  private final FacebookProvider facebookProvider = mock(FacebookProvider.class);
  private final Key key = mock(Key.class);

  private final StormUser stormUser = mock(StormUser.class);
  private final FacebookUser facebookUser = mock(FacebookUser.class);

  private final FacebookResource resource = new FacebookResource(client, providerFactory);

  @Before
  public void setup() {
    when(providerFactory.newFacebookProvider(anyString())).thenReturn(facebookProvider);
    when(client.getUser(anyString())).thenReturn(stormUser);

    when(stormUser.getFacebookAccessToken()).thenReturn("fbAccessToken");

    when(facebookUser.getFacebookId()).thenReturn("1");
    when(facebookUser.getFirstName()).thenReturn("Bill");
    when(facebookUser.getMiddleName()).thenReturn("Joe");
    when(facebookUser.getLastName()).thenReturn("Nye");
    when(facebookUser.getName()).thenReturn("Bill Joe Nye");
    when(facebookUser.getGender()).thenReturn("Male");
  }

  @Test
  public void testGetUserWithNullUsername() {
    Response response = resource.getUser(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

  @Test
  public void testGetUser() {
    when(facebookProvider.getFacebookUser()).thenReturn(facebookUser);

    Response response = resource.getUser(key, "Test");
    FacebookUser userResponse = (FacebookUser) response.getEntity();

    assertEquals(response.getStatusInfo(), Response.Status.OK);
    assertEquals(userResponse, facebookUser);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testGetUserWithExpiredOAuth() {
    when(facebookProvider.getFacebookUser()).thenThrow(FacebookOAuthException.class);

    Response response = resource.getUser(key, "Test");

    assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
  }

}
