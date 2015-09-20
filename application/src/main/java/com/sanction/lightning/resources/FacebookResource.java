package com.sanction.lightning.resources;

import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookProvider;
import com.sanction.lightning.facebook.FacebookProviderFactory;
import com.sanction.lightning.models.FacebookUser;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;
import io.dropwizard.auth.Auth;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/facebook")
@Produces(MediaType.APPLICATION_JSON)
public class FacebookResource {
  private static final Logger LOG = LoggerFactory.getLogger(FacebookResource.class);

  private final ThunderClient thunderClient;
  private final FacebookProviderFactory facebookProviderFactory;

  @Inject
  public FacebookResource(ThunderClient thunderClient,
                          FacebookProviderFactory facebookProviderFactory) {
    this.thunderClient = thunderClient;
    this.facebookProviderFactory = facebookProviderFactory;
  }

  /**
   * Fetches a FacebookUser object containing user information.
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to get FacebookUser information for.
   * @return The FacebookUser object corresponding to the Pilot username.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getUser").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookProvider facebookProvider
        = facebookProviderFactory.newFacebookProvider(pilotUser.getFacebookAccessToken());

    FacebookUser facebookUser;
    try {
      facebookUser = facebookProvider.getFacebookUser();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}", username, e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(facebookUser).build();
  }

  /**
   * Fetches all the photos of a specific user.
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to get photos for.
   * @return A list of the photos uploaded by the user.
   */
  @GET
  @Path("/photos")
  public Response getPhotos(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getUser").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookProvider facebookProvider
            = facebookProviderFactory.newFacebookProvider(pilotUser.getFacebookAccessToken());

    List<String> urlList;
    try {
      urlList = facebookProvider.getFacebookUserPhotos();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}", username, e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(urlList).build();
  }

  /**
   * Fetches all the videos of a specific user.
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to get videos for.
   * @return A list of the videos uploaded by the user.
   */
  @GET
  @Path("/videos")
  public Response getVideos(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getUser").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookProvider facebookProvider
            = facebookProviderFactory.newFacebookProvider(pilotUser.getFacebookAccessToken());

    List<String> urlList;
    try {
      urlList = facebookProvider.getFacebookUserVideos();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}", username, e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(urlList).build();
  }

  /**
   * Fetches a an extended token given an existing token.
   * @param key The authentication key for the requesting application.
   * @param username the name of the PilotUser to fetch an extended token for
   * @return an extended Facebook user token
   */
  @GET
  @Path("/extendedToken")
  public Response getExtendedToken(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookProvider facebookProvider
            = facebookProviderFactory.newFacebookProvider(pilotUser.getFacebookAccessToken());

    String extendedToken;
    try {
      extendedToken = facebookProvider.getFacebookExtendedToken();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth Token", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    //set up the PilotUser with the extended token
    pilotUser = new PilotUser(pilotUser.getUsername(), pilotUser.getPassword(), extendedToken,
            pilotUser.getTwitterAccessSecret(), pilotUser.getTwitterAccessSecret());

    if (thunderClient.updateUser(pilotUser) == null) {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
              .entity("Request failed: Could not connect to database").build();
    }

    return Response.ok(extendedToken).build();
  }

  /**
   * Fetches the loginDialogUrl for setting user permissions with Facebook.
   * @param key The authentication key for the requesting application.
   * @return the url string used to set permissions.
   */
  @GET
  @Path("/oauthUrl")
  public Response getOauthUrl(@Auth Key key) {
    FacebookProvider facebookProvider
            = facebookProviderFactory.newFacebookProvider();

    String permissionsUrl;
    try {
      permissionsUrl = facebookProvider.getOauthUrl();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(permissionsUrl).build();
  }
}