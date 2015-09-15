package com.sanction.lightning.resources;

import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookConfiguration;
import com.sanction.lightning.facebook.FacebookProvider;
import com.sanction.lightning.models.FacebookUser;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.StormUser;
import io.dropwizard.auth.Auth;

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
  private final FacebookConfiguration facebookConfiguration;

  @Inject
  public FacebookResource(ThunderClient thunderClient,
                          FacebookConfiguration facebookConfiguration) {
    this.thunderClient = thunderClient;
    this.facebookConfiguration = facebookConfiguration;
  }

  /**
   * Fetches a FacebookUser object containing user information.
   * @param key The authentication key for the requesting application.
   * @param username The username of the StormUser to get FacebookUser information for.
   * @return The FacebookUser object corresponding to the Storm username.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getUser").build();
    }

    StormUser stormUser = thunderClient.getUser(username);

    FacebookUser facebookUser;
    try {
      facebookUser = new FacebookProvider(stormUser.getFacebookAccessToken(),
          facebookConfiguration.getAppSecret()).getFacebookUser();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth Token", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.status(Response.Status.ACCEPTED).entity(facebookUser).build();
  }
}