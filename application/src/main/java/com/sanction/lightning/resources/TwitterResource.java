package com.sanction.lightning.resources;

import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanction.thunder.ThunderClient;
import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/twitter")
@Produces(MediaType.APPLICATION_JSON)
public class TwitterResource {
  private final ThunderClient thunderClient;
  private final TwitterServiceFactory twitterServiceFactory;

  @Inject
  public TwitterResource(ThunderClient thunderClient,
                         TwitterServiceFactory twitterServiceFactory) {
    this.thunderClient = thunderClient;
    this.twitterServiceFactory = twitterServiceFactory;
  }

  /**
   * Retrieves a TwitterUser object for a given PilotUser username.
   *
   * @param key The authentication credentials of the calling application.
   * @param username The username of the PilotUser to find Twitter User information for.
   * @return The TwitterUser object if successful.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("'username' query parameter is required.").build();
    }

    return Response.ok("Worked").build();
  }

  /**
   * Generates a Twitter URL that can be used to authenticate a PilotUser.
   *
   * @param key The authentication credentials of the calling application.
   * @return The URL to send the user to if successful.
   */
  @GET
  @Path("/oauthUrl")
  public Response getOAuthToken(@Auth Key key) {
    TwitterService service = twitterServiceFactory.newTwitterService();

    String url = service.getAuthorizationUrl();
    if (url == null) {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
          .entity("Unable to retrieve OAuth URL from Twitter.").build();
    }

    return Response.ok(url).build();
  }

}
