package com.sanction.lightning.resources;

import com.restfb.exception.FacebookOAuthException;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookService;
import com.sanction.lightning.facebook.FacebookServiceFactory;
import com.sanction.lightning.models.facebook.FacebookPhoto;
import com.sanction.lightning.models.facebook.FacebookUser;
import com.sanction.lightning.models.facebook.FacebookVideo;
import com.sanction.lightning.utils.UrlService;
import com.sanction.thunder.ThunderClient;
import com.sanction.thunder.models.PilotUser;
import io.dropwizard.auth.Auth;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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

@Path("/facebook")
@Produces(MediaType.APPLICATION_JSON)
public class FacebookResource {
  private static final Logger LOG = LoggerFactory.getLogger(FacebookResource.class);

  private final ThunderClient thunderClient;
  private final FacebookServiceFactory facebookServiceFactory;
  private final UrlService urlService;

  /**
   * Constructs a new FacebookResource to handle Facebook HTTP requests.
   *
   * @param thunderClient Client for connecting to Thunder.
   * @param facebookServiceFactory A factory to create new instances of FacebookService.
   * @param urlService A helper class for HTTP requests.
   */
  @Inject
  public FacebookResource(ThunderClient thunderClient,
                          FacebookServiceFactory facebookServiceFactory,
                          UrlService urlService) {
    this.thunderClient = thunderClient;
    this.facebookServiceFactory = facebookServiceFactory;
    this.urlService = urlService;
  }

  /**
   * Retrieves a FacebookUser object for the requested PilotUser username.
   *
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to get information for.
   * @return The FacebookUser object corresponding to the PilotUser username.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getUser").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookService facebookService
        = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    FacebookUser facebookUser;
    try {
      facebookUser = facebookService.getFacebookUser();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}", username, e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(facebookUser).build();
  }

  /**
   * Fetches all the photos for the requested PilotUser username.
   * This method does not download the actual bytes of the photos.
   * To download the bytes, use the method {@link #getMediaBytes(Key, String) getMediaBytes}
   *
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to get photos for.
   * @return A list of the photos that the user has on Facebook.
   */
  @GET
  @Path("/photos")
  public Response getPhotos(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getPhotos").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookService facebookService
            = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    List<FacebookPhoto> photoList;
    try {
      photoList = facebookService.getFacebookUserPhotos();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}", username, e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(photoList).build();
  }

  /**
   * Publishes to a user's Facebook timeline.
   *
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to upload to.
   * @param inputStream The inputStream for the file to upload.
   * @param contentDispositionHeader Additional information about the file to upload.
   * @param type The type of file passed in ("photo" or "video").
   * @param message The message to post the user's timeline with the luded file.
   * @return The uploaded file information if the request was successful.
   */
  @POST
  @Path("/publish")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response publish(@Auth Key key, @QueryParam("username") String username,
                          @FormDataParam("file") InputStream inputStream,
                          @FormDataParam("file") FormDataContentDisposition
                                contentDispositionHeader,
                          @QueryParam("type") String type,
                          @FormDataParam("message") @DefaultValue("") String message,
                          @FormDataParam("title") @DefaultValue("") String videoTitle) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for publish").build();
    }

    if (type == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'type' query parameter is required for publish").build();
    }

    if (!type.equals("photo") && !type.equals("video")) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'type' query parameter must be 'photo' or 'video'").build();
    }

    if (inputStream == null) {
      LOG.error("InputStream was null.");
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'file' is required for publish").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookService facebookService =
        facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    String uploadedFile = facebookService.publishToFacebook(inputStream, type,
            contentDispositionHeader.getFileName(), message, videoTitle);

    if (uploadedFile == null) {
      LOG.error("Error uploading to Facebook.");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity("Error uploading to Facebook.").build();
    }

    return Response.ok(uploadedFile).build();
  }

  /**
   * Downloads the bytes of a file at the given URL.
   *
   * @param key The authentication key for the requesting application.
   * @param url The URL of the file to download.
   * @return The file represented as a byte array.
   */
  @GET
  @Path("/mediaBytes")
  public Response getMediaBytes(@Auth Key key, @QueryParam("url") String url) {
    if (url == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'url' query parameter is required for getMediaBytes").build();
    }

    URLConnection connection = urlService.fetchUrlConnection(url);

    if (connection == null) {
      LOG.error("Bad URL");
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("Request rejected due to bad URL").build();
    }

    InputStream connectionInputStream = urlService.fetchInputStreamFromConnection(connection);

    if (connectionInputStream == null) {
      LOG.error("Bad InputStream");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity("Error reading InputStream").build();
    }

    byte[] response = urlService.inputStreamToByteArray(connectionInputStream);

    if (response == null) {
      LOG.error("Error reading bytes");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity("Error trying to read bytes").build();
    }

    return Response.ok(response).build();
  }

  /**
   * Fetches all the videos for the requested PilotUser username.
   * This method does not download the actual bytes of the videos.
   * To download the bytes, use the method {@link #getMediaBytes(Key, String) getMediaBytes}
   *
   * @param key The authentication key for the requesting application.
   * @param username The username of the PilotUser to get videos for.
   * @return A list of the videos that the user has on Facebook.
   */
  @GET
  @Path("/videos")
  public Response getVideos(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required for getUser").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookService facebookService
            = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    List<FacebookVideo> videoList;
    try {
      videoList = facebookService.getFacebookUserVideos();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username {}", username, e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(videoList).build();
  }

  /**
   * Fetches an extended Facebook access token for the PilotUser.
   *
   * @param key The authentication key for the requesting application.
   * @param username The name of the PilotUser to fetch an extended token for.
   * @return The extended Facebook access token.
   */
  @GET
  @Path("/extendedToken")
  public Response getExtendedToken(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
              .entity("'username' query parameter is required").build();
    }

    PilotUser pilotUser = thunderClient.getUser(username);
    FacebookService facebookService
            = facebookServiceFactory.newFacebookService(pilotUser.getFacebookAccessToken());

    String extendedToken;
    try {
      extendedToken = facebookService.getFacebookExtendedToken();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth Token", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    // Set up the PilotUser with the extended token
    pilotUser = new PilotUser(pilotUser.getUsername(), pilotUser.getPassword(), extendedToken,
        pilotUser.getTwitterAccessSecret(), pilotUser.getTwitterAccessSecret());

    if (thunderClient.updateUser(pilotUser) == null) {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
              .entity("Request failed: Could not connect to database").build();
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
    FacebookService facebookService = facebookServiceFactory.newFacebookService();

    String permissionsUrl;
    try {
      permissionsUrl = facebookService.getOauthUrl();
    } catch (FacebookOAuthException e) {
      LOG.error("Bad Facebook OAuth token for username", e);
      return Response.status(Response.Status.NOT_FOUND)
              .entity("Request rejected due to bad OAuth token").build();
    }

    return Response.ok(permissionsUrl).build();
  }
}
