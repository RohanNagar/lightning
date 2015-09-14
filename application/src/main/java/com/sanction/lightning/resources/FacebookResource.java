package com.sanction.lightning.resources;

import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookApplicationKey;
import com.sanction.lightning.facebook.FacebookProvider;
import com.sanction.lightning.models.FacebookPost;
import com.sanction.lightning.models.FacebookUser;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.StormUser;
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
  private final FacebookApplicationKey applicationKey;

  @Inject
  public FacebookResource(ThunderClient thunderClient, FacebookApplicationKey applicationKey) {
    this.thunderClient = thunderClient;
    this.applicationKey = applicationKey;
  }

  /**
   * Fetches a FacebookUser object containing user information.
   * @param key
   * @param username
   * @return a json object containing all the user information
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required").build();
    }

    StormUser stormUser = thunderClient.getUser(username);

    FacebookUser facebookUser = null;
    try {
      facebookUser = new FacebookProvider(stormUser.getFacebookAccessToken(),
              applicationKey).getFacebookUser();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth Token", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.status(Response.Status.ACCEPTED).entity(facebookUser).build();
  }

  /**
   * Fetches a List of FacebookPost objects.
   * @param key
   * @param username
   * @return a list of posts formatted in json
   */

  @GET
  @Path("/newsfeed")
  public Response getNewsfeed(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required").build();
    }

    StormUser stormUser = thunderClient.getUser(username);

    List<FacebookPost> facebookFeed = null;
    try {
      facebookFeed = new FacebookProvider(stormUser.getFacebookAccessToken(),
              applicationKey).getFacebookFeed();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth Toke", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.status(Response.Status.ACCEPTED).entity(facebookFeed).build();
  }
}