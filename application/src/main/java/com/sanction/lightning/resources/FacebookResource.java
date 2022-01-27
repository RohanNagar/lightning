package com.sanction.lightning.resources;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.exception.ThunderConnectionException;
import com.sanction.lightning.facebook.FacebookService;
import com.sanction.lightning.facebook.FacebookServiceFactory;
import com.sanction.lightning.models.PublishType;
import com.sanction.lightning.models.facebook.FacebookOAuthRequest;
import com.sanction.lightning.models.facebook.FacebookPhoto;
import com.sanction.lightning.models.facebook.FacebookUser;
import com.sanction.lightning.models.facebook.FacebookVideo;
import com.sanctionco.thunder.ThunderClient;
import com.sanctionco.thunder.models.User;

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

import retrofit2.HttpException;

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
      LOG.warn("Attempted to get a Facebook user with null email.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("An email is required to get a Facebook user.").build();
    }

    if (password == null || password.equals("")) {
      LOG.warn("Attempted to get a Facebook user without a password.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The user password is required to exist in the header.").build();
    }

    LOG.info("Attempting to get Facebook user information for {}.", email);

    User thunderUser;
    try {
      thunderUser = getThunderUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService = facebookServiceFactory.newFacebookService(
        thunderUser.getProperties().get("facebook-access-token").toString());

    FacebookUser facebookUser = facebookService.getFacebookUser();

    if (facebookUser == null) {
      LOG.error("Bad Facebook OAuth token for user {}.", email);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("The OAuth token for the user was rejected.").build();
    }

    LOG.info("Successfully retrieved Facebook user information for {}.", email);
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
      LOG.warn("Attempted to get Facebook photos with null email.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("An email is required to get Facebook photos.").build();
    }

    if (password == null || password.equals("")) {
      LOG.warn("Attempted to get Facebook photos without a password.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The user password is required to exist in the header.").build();
    }

    LOG.info("Attempting to get Facebook photos for user {}.", email);

    User thunderUser;
    try {
      thunderUser = getThunderUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService = facebookServiceFactory.newFacebookService(
        thunderUser.getProperties().get("facebook-access-token").toString());

    List<FacebookPhoto> photos = facebookService.getFacebookUserPhotos();

    if (photos == null) {
      LOG.error("Bad Facebook OAuth token for user {}.", email);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("The OAuth token for the user was rejected.").build();
    }

    LOG.info("Successfully retrieved Facebook photo information for user {}.", email);
    return Response.ok(photos).build();
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
      LOG.warn("Attempted to get Facebook videos with null email.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("An email is required to get videos.").build();
    }

    if (password == null || password.equals("")) {
      LOG.warn("Attempted to get Facebook videos without a password.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The user password is required to exist in the header.").build();
    }

    LOG.info("Attempting to get Facebook video information for user {}.", email);

    User thunderUser;
    try {
      thunderUser = getThunderUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService = facebookServiceFactory.newFacebookService(
        thunderUser.getProperties().get("facebook-access-token").toString());

    List<FacebookVideo> videos = facebookService.getFacebookUserVideos();

    if (videos == null) {
      LOG.error("Bad Facebook OAuth token for user {}.", email);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("The OAuth token for the user was rejected.").build();
    }

    LOG.info("Successfully retrieved Facebook video information for user {}.", email);
    return Response.ok(videos).build();
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
                          @QueryParam("type") PublishType type,
                          @QueryParam("message") String message,
                          @FormDataParam("file") InputStream inputStream,
                          @FormDataParam("file") FormDataContentDisposition
                              contentDispositionHeader,
                          @FormDataParam("title") @DefaultValue("") String videoTitle) {
    publishRequests.mark();

    if (email == null) {
      LOG.warn("Attempted to publish to Facebook with null email.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("An email is required to publish to Facebook.").build();
    }

    if (password == null || password.equals("")) {
      LOG.warn("Attempted to publish to Facebook without a password.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The user password is required to exist in the header.").build();
    }

    if (type == null) {
      LOG.warn("Attempted to publish to Facebook without specifying the type.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("A type of text, photo, or video is required to publish to Facebook.").build();
    }

    if ((type.equals(PublishType.PHOTO) || type.equals(PublishType.VIDEO))
        && inputStream == null) {
      LOG.warn("Attempted to publish media to Facebook without supplying the media.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("A file is required to publish a photo or video.").build();
    }

    if (type.equals(PublishType.TEXT) && (message == null || message.equals(""))) {
      LOG.warn("Attempted to publish text to Facebook without supplying the text.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Posting a text message requires the message parameter.").build();
    }

    LOG.info("Attempting to publish {} to Facebook for user {}.", type, email);

    User thunderUser;
    try {
      thunderUser = getThunderUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService = facebookServiceFactory.newFacebookService(
        thunderUser.getProperties().get("facebook-access-token").toString());

    // Get the name of the file if publishing media
    String filename = !type.equals(PublishType.TEXT)
        ? contentDispositionHeader.getFileName()
        : null;

    String uploadedFile = facebookService.publish(inputStream, type, message, filename, videoTitle);

    if (uploadedFile == null) {
      LOG.error("Error uploading to Facebook for {}.", email);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Error uploading to Facebook.").build();
    }

    LOG.info("Successfully published {} to Facebook for user {}.", type, email);
    return Response.status(Response.Status.CREATED).entity(uploadedFile).build();
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
      LOG.warn("Attempted to extend a Facebook OAuth token with a null email.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("An email is required to extend a token.").build();
    }

    if (password == null || password.equals("")) {
      LOG.warn("Attempted to extend a Facebook OAuth token without a password.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("The user password is required to exist in the header.").build();
    }

    LOG.info("Attempting to extend Facebook OAuth token for user {}.", email);

    User thunderUser;
    try {
      thunderUser = getThunderUser(email, password);
    } catch (ThunderConnectionException e) {
      LOG.error("Unable to retrieve PilotUser ({}) from Thunder.", email);
      return e.getResponse();
    }

    FacebookService facebookService = facebookServiceFactory.newFacebookService(
        thunderUser.getProperties().get("facebook-access-token").toString());

    String extendedToken = facebookService.getFacebookExtendedToken();

    if (extendedToken == null) {
      LOG.error("Bad Facebook OAuth Token for email {}.", email);
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Request rejected due to bad OAuth token.").build();
    }

    // Set up the updated PilotUser with the extended token
    thunderUser.getProperties().put("facebook-access-token", extendedToken);
    thunderUser = new User(
        thunderUser.getEmail(), thunderUser.getPassword(), thunderUser.getProperties());

    // Update the user in Thunder
    try {
      thunderClient.updateUser(thunderUser, thunderUser.getEmail().getAddress(), password);
    } catch (Exception e) {
      LOG.error("Unable to update User ({}) through Thunder.", email, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(e.getMessage())
          .build();
    }

    LOG.info("Successfully extended the Facebook OAuth token for user {}.", email);
    return Response.ok(extendedToken).build();
  }

  /**
   * Retrieves an authentication URL that a new user should be
   * presented with to approve permissions.
   *
   * @param key The authentication key for the requesting application.
   * @param redirectUrl The URL that Facebook should redirect to after the user authenticates.
   * @return The URL to redirect the user to.
   */
  @GET
  @Path("/oauthUrl")
  public Response getOauthUrl(@Auth Key key,
                              @QueryParam("redirect") String redirectUrl) {
    oauthRequests.mark();

    if (redirectUrl == null) {
      LOG.warn("Cannot get OAuth URL without a redirect URL specified.");
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("An redirect URL is required to get an OAuth URL.").build();
    }

    LOG.info("Retrieving OAuth URL to authenticate with Facebook.");

    FacebookService facebookService = facebookServiceFactory.newFacebookService();

    String permissionsUrl = facebookService.getOauthUrl(redirectUrl);

    if (permissionsUrl == null) {
      LOG.error("Something went wrong while getting the OAuth URL.");
      return Response.status(Response.Status.NOT_FOUND)
          .entity("Something went wrong, please try again later.").build();
    }

    FacebookOAuthRequest authRequest = new FacebookOAuthRequest(permissionsUrl);

    LOG.info("Successfully built Facebook OAuth URL.");
    return Response.ok(authRequest).build();
  }

  private User getThunderUser(String email, String password) {
    User user;

    try {
      user = thunderClient.getUser(email, password).join();
    } catch (HttpException e) {
      LOG.error("Error accessing Thunder: {}", e.getMessage());
      throw new ThunderConnectionException(
          Response.status(Response.Status.SERVICE_UNAVAILABLE)
              .entity(e.getMessage())
              .build());
    }

    return user;
  }
}
