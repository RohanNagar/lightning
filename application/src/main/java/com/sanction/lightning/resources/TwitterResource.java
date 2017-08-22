package com.sanction.lightning.resources;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.models.PublishType;
import com.sanction.lightning.models.twitter.TwitterUser;
import com.sanction.lightning.twitter.TwitterService;
import com.sanction.lightning.twitter.TwitterServiceFactory;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;
import io.dropwizard.auth.Auth;

import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/twitter")
@Produces(MediaType.APPLICATION_JSON)
public class TwitterResource {
  private final ThunderClient thunderClient;
  private final TwitterServiceFactory twitterServiceFactory;

  // Counts number of requests
  private final Meter usersRequests;
  private final Meter publishRequests;
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
    this.publishRequests = metrics.meter(MetricRegistry.name(
        TwitterResource.class,
        "publish-requests"));
    this.oauthRequests = metrics.meter(MetricRegistry.name(
        TwitterResource.class,
        "oauth-requests"));
  }

  /**
   * Retrieves a TwitterUser object for a given PilotUser.
   *
   * @param key The authentication credentials of the calling application.
   * @param email The email of the PilotUser to find Twitter information for.
   * @param password The password of the PilotUser.
   * @return The TwitterUser object, if successful.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key,
                          @QueryParam("email") String email,
                          @HeaderParam("password") String password) {
    usersRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to get a Twitter user.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    PilotUser pilotUser = thunderClient.getUser(email, password);
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
   * Publishes to a user's Twitter Feed.
   *
   * @param key The authentication key for the requesting application.
   * @param email The email of the PilotUser to upload as.
   * @param password The password of the PilotUser.
   * @param type The type of the publish to perform.
   * @param message The text message to publish.
   * @param inputStream The inputStream for the file to be upload.
   * @param contentDispositionHeader Additional information about the file to upload.
   * @return The ID of the created post if successful.
   */
  @POST
  @Path("/publish")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response publish(@Auth Key key,
                          @QueryParam("email") String email,
                          @HeaderParam("password") String password,
                          @QueryParam("type") PublishType type,
                          @QueryParam("message") String message,
                          @FormDataParam("file") InputStream inputStream,
                          @FormDataParam("file") FormDataContentDisposition
                                contentDispositionHeader) {
    publishRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to post a tweet.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    if (type == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'type' query parameter is required to determine what to publish.").build();
    }

    if ((type.equals(PublishType.PHOTO) || type.equals(PublishType.VIDEO))
        && inputStream == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("A file is required to publish a photo or video.").build();
    }

    if (type.equals(PublishType.TEXT) && (message == null || message.equals(""))) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Posting a text message requires the 'message' parameter.").build();
    }

    PilotUser pilotUser = thunderClient.getUser(email, password);
    TwitterService service = twitterServiceFactory.newTwitterService(
        pilotUser.getTwitterAccessToken(),
        pilotUser.getTwitterAccessSecret());

    // Get the name of the file if publishing media
    String filename;
    if (type.equals(PublishType.TEXT)) {
      filename = null;
    } else {
      filename = contentDispositionHeader.getFileName();
    }

    Long id = service.publish(type, message, filename, inputStream);
    if (id == null) {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
          .entity("Unable to retrieve information from Twitter.").build();
    }

    return Response.status(Response.Status.CREATED).entity(id).build();
  }

  /**
   * Generates a Twitter URL that can be used to authenticate a PilotUser.
   *
   * @param key The authentication credentials of the calling application.
   * @return The application authentication URL, if successful.
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
