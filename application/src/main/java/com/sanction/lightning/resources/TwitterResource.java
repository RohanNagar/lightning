package com.sanction.lightning.resources;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.models.twitter.TwitterUser;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;
import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

  // Counts number of requests
  private final Meter usersRequests;
  private final Meter oauthRequests;

  /**
   * Constructs a new TwitterResource to handle Twitter HTTP requests.
   *
   * @param thunderClient Client used to connect with Thunder.
   * @param metrics The metrics object to set up meters with.
   * @param twitterServiceFactory A factory to create new instances of TwitterResource.
   */
  @Inject
  public TwitterResource(ThunderClient thunderClient, MetricRegistry metrics,
                         TwitterServiceFactory twitterServiceFactory) {
    this.thunderClient = thunderClient;
    this.twitterServiceFactory = twitterServiceFactory;

    // Set up metrics
    this.usersRequests = metrics.meter(MetricRegistry.name(
        TwitterResource.class,
        "users-requests"));
    this.oauthRequests = metrics.meter(MetricRegistry.name(
        TwitterResource.class,
        "oauth-requests"));
  }

  /**
   * Retrieves a TwitterUser object for a given PilotUser username.
   *
   * @param key The authentication credentials of the calling application.
   * @param username The username of the PilotUser to find Twitter User information for.
   * @param password The password of the PilotUser to find Twitter User information for.
   * @return The TwitterUser object if successful.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key,
                          @QueryParam("username") String username,
                          @HeaderParam("password") String password) {
    usersRequests.mark();

    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("'username' query parameter is required.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    PilotUser pilotUser = thunderClient.getUser(password, username);
    TwitterService service = twitterServiceFactory.newTwitterService(
        pilotUser.getTwitterAccessToken(),
        pilotUser.getTwitterAccessSecret());

    TwitterUser user = service.getTwitterUser();
    if (user == null) {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
          .entity("Unable to retrieve information from Twitter.").build();
    }

    return Response.ok(user).build();
  }

  /**
   * Generates a Twitter URL that can be used to authenticate a PilotUser.
   *
   * @param key The authentication credentials of the calling application.
   * @return The URL to send the user to if successful.
   */
  @GET
  @Path("/oauthUrl")
  public Response getOAuthUrl(@Auth Key key) {
    oauthRequests.mark();

    TwitterService service = twitterServiceFactory.newTwitterService();

    String url = service.getAuthorizationUrl();
    if (url == null) {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
          .entity("Unable to retrieve OAuth URL from Twitter.").build();
    }

    return Response.ok(url).build();
  }
}
