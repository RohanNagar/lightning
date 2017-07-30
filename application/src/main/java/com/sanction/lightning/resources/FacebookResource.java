package com.sanction.lightning.resources;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.exception.ThunderConnectionException;
import com.sanction.lightning.facebook.FacebookService;
import com.sanction.lightning.facebook.FacebookServiceFactory;
import com.sanction.lightning.models.facebook.FacebookPhoto;
import com.sanction.lightning.models.facebook.FacebookUser;
import com.sanction.lightning.models.facebook.FacebookVideo;
import com.sanction.lightning.models.facebook.PublishType;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;
import io.dropwizard.auth.Auth;

import java.io.InputStream;
import java.util.List;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit.RetrofitError;

@Path("/facebook")
@Produces(MediaType.APPLICATION_JSON)
public class FacebookResource {
  private static final Logger LOG = LoggerFactory.getLogger(FacebookResource.class);

  private final ThunderClient thunderClient;
  private final FacebookServiceFactory facebookServiceFactory;

  // Counts number of requests
  private final Meter usersRequests;
  private final Meter photosRequests;
  private final Meter videosRequests;
  private final Meter publishRequests;
  private final Meter tokenRequests;
  private final Meter oauthRequests;

  /**
   * Constructs a new FacebookResource to handle Facebook HTTP requests.
   *
   * @param thunderClient Client for connecting to Thunder.
   * @param metrics The metrics object to set up meters with.
   * @param facebookServiceFactory A factory to create new instances of FacebookService.
   */
  @Inject
  public FacebookResource(ThunderClient thunderClient, MetricRegistry metrics,
                          FacebookServiceFactory facebookServiceFactory) {
    this.thunderClient = thunderClient;
    this.facebookServiceFactory = facebookServiceFactory;

    // Set up metrics
    this.usersRequests = metrics.meter(MetricRegistry.name(
        FacebookResource.class,
        "users-requests"));
    this.photosRequests = metrics.meter(MetricRegistry.name(
        FacebookResource.class,
        "photos-requests"));
    this.videosRequests = metrics.meter(MetricRegistry.name(
        FacebookResource.class,
        "videos-requests"));
    this.publishRequests = metrics.meter(MetricRegistry.name(
        FacebookResource.class,
        "publish-requests"));
    this.tokenRequests = metrics.meter(MetricRegistry.name(
        FacebookResource.class,
        "token-requests"));
    this.oauthRequests = metrics.meter(MetricRegistry.name(
        FacebookResource.class,
        "oauth-requests"));
  }

  /**
   * Retrieves a FacebookUser object for the requested PilotUser.
   *
   * @param key The authentication key for the requesting application.
   * @param email The email of the PilotUser to get information for.
   * @param password The password of the PilotUser of get information for.
   * @return The FacebookUser object corresponding to the given PilotUser.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key,
                          @QueryParam("email") String email,
                          @HeaderParam("password") String password) {
    usersRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to get a Facebook user.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    PilotUser pilotUser;
    try {
      pilotUser = getPilotUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService
        = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    FacebookUser facebookUser;
    try {
      facebookUser = facebookService.getFacebookUser();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}.", email, e);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Request rejected due to bad OAuth token.").build();
    }

    return Response.ok(facebookUser).build();
  }

  /**
   * Fetches all the photos for the requested PilotUser.
   * This method does not download the actual bytes of the photos.
   *
   * @param key The authentication key for the requesting application.
   * @param email The email of the PilotUser to get photos for.
   * @param password The password of the PilotUser.
   * @return A list of the photos that the user has on Facebook.
   */
  @GET
  @Path("/photos")
  public Response getPhotos(@Auth Key key,
                            @QueryParam("email") String email,
                            @HeaderParam("password") String password) {
    photosRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to get Facebook photos.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    PilotUser pilotUser;
    try {
      pilotUser = getPilotUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService
        = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    List<FacebookPhoto> photoList;
    try {
      photoList = facebookService.getFacebookUserPhotos();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for email {}.", email, e);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Request rejected due to bad OAuth token.").build();
    }

    return Response.ok(photoList).build();
  }

  /**
   * Publishes to a user's Facebook timeline.
   *
   * @param key The authentication key for the requesting application.
   * @param email The email of the PilotUser to upload as.
   * @param password The password of the PilotUser.
   * @param type The type of the publish to perform.
   * @param message The text message to publish.
   * @param inputStream The inputStream for the file to be upload.
   * @param contentDispositionHeader Additional information about the file to upload.
   * @param videoTitle If publishing a video, the title to attach to the video.
   * @return The uploaded file information if the request was successful.
   */
  @POST
  @Path("/publish")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response publish(@Auth Key key,
                          @QueryParam("email") String email,
                          @HeaderParam("password") String password,
                          @QueryParam("type") String type,
                          @QueryParam("message") String message,
                          @FormDataParam("file") InputStream inputStream,
                          @FormDataParam("file") FormDataContentDisposition
                              contentDispositionHeader,
                          @FormDataParam("title") @DefaultValue("") String videoTitle) {
    publishRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to publish to Facebook.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    if (type == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'type' query parameter is required to publish to Facebook.").build();
    }

    PublishType publishType = PublishType.fromString(type);
    if (publishType == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'type' query parameter must be either 'photo', 'video', or 'text'.").build();
    }

    if ((publishType.equals(PublishType.PHOTO) || publishType.equals(PublishType.VIDEO))
        && inputStream == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("A file is required to publish a photo or video.").build();
    }

    if (publishType.equals(PublishType.TEXT) && (message == null || message.equals(""))) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Posting a text message requires the 'message' parameter.").build();
    }

    PilotUser pilotUser;
    try {
      pilotUser = getPilotUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService =
        facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    String uploadedFile = facebookService.publishToFacebook(inputStream, publishType,
        contentDispositionHeader.getFileName(), message, videoTitle);

    if (uploadedFile == null) {
      LOG.error("Error uploading to Facebook for username {}.", email);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error uploading to Facebook.").build();
    }

    return Response.ok(uploadedFile).build();
  }

  /**
   * Fetches all the videos for the requested PilotUser.
   * This method does not download the actual bytes of the videos.
   *
   * @param key The authentication key for the requesting application.
   * @param email The email of the PilotUser to get videos for.
   * @param password The password of the PilotUser.
   * @return A list of the videos that the user has on Facebook.
   */
  @GET
  @Path("/videos")
  public Response getVideos(@Auth Key key,
                            @QueryParam("email") String email,
                            @HeaderParam("password") String password) {
    videosRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to get videos.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    PilotUser pilotUser;
    try {
      pilotUser = getPilotUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService
        = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    List<FacebookVideo> videoList;
    try {
      videoList = facebookService.getFacebookUserVideos();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for email {}.", email, e);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Request rejected due to bad OAuth token.").build();
    }

    return Response.ok(videoList).build();
  }

  /**
   * Fetches an extended Facebook access token for the PilotUser.
   *
   * @param key The authentication key for the requesting application.
   * @param email The PilotUser to fetch an extended token for.
   * @param password The password of the PilotUser.
   * @return The extended Facebook access token.
   */
  @GET
  @Path("/extendedToken")
  public Response getExtendedToken(@Auth Key key,
                                   @QueryParam("email") String email,
                                   @HeaderParam("password") String password) {
    tokenRequests.mark();

    if (email == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The 'email' query parameter is required to extend a token.").build();
    }

    if (password == null || password.equals("")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Incorrect or missing header credentials.").build();
    }

    PilotUser pilotUser;
    try {
      pilotUser = getPilotUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService
        = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    String extendedToken;
    try {
      extendedToken = facebookService.getFacebookExtendedToken();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth Token for email {}.", email, e);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Request rejected due to bad OAuth token.").build();
    }

    // Set up the PilotUser with the extended token
    pilotUser = new PilotUser(pilotUser.getEmail(), pilotUser.getPassword(), extendedToken,
        pilotUser.getTwitterAccessSecret(), pilotUser.getTwitterAccessSecret());

    try {
      thunderClient.updateUser(pilotUser, password);
    } catch (RetrofitError e) {
      LOG.error("Unable to update PilotUser ({}) through Thunder.", email, e);
      return Response.status(e.getResponse().getStatus())
          .entity(e.getResponse().getReason())
          .build();
    }

    return Response.ok(extendedToken).build();
  }

  /**
   * Retrieves an authentication URL that a new user should be
   * presented with to approve permissions.
   *
   * @param key The authentication key for the requesting application.
   * @return The URL to redirect the user to.
   */
  @GET
  @Path("/oauthUrl")
  public Response getOauthUrl(@Auth Key key) {
    oauthRequests.mark();

    FacebookService facebookService = facebookServiceFactory.newFacebookService();

    String permissionsUrl;
    try {
      permissionsUrl = facebookService.getOauthUrl();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token.", e);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Request rejected due to bad OAuth token.").build();
    }

    return Response.ok(permissionsUrl).build();
  }

  private PilotUser getPilotUser(String email, String password) {
    PilotUser pilotUser;

    try {
      pilotUser = thunderClient.getUser(email, password);
    } catch (RetrofitError e) {
      // If the error has a null response, then Thunder is down.
      if (e.getResponse() == null) {
        LOG.error("Thunder is currently unavailable.");
        throw new ThunderConnectionException(
            Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity("Error: " + e.getMessage())
                .build());
      }

      // If unauthorized, the API keys are incorrect - Internal Server Error.
      if (e.getResponse().getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
        LOG.error("Incorrect API Keys to access Thunder.");
        throw new ThunderConnectionException(
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Database error: " + e.getMessage())
                .build());
      }

      // Otherwise, supply the response that Thunder gave.
      LOG.error("Error accessing Thunder: {}", e.getResponse().getReason());
      throw new ThunderConnectionException(
          Response.status(e.getResponse().getStatus())
              .entity(e.getResponse().getReason())
              .build());
    }

    return pilotUser;
  }
}
