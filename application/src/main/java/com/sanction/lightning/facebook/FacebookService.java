package com.sanction.lightning.facebook;

import com.google.common.collect.Lists;

import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.scope.ExtendedPermissions;
import com.restfb.scope.ScopeBuilder;
import com.restfb.scope.UserDataPermissions;

import com.sanction.lightning.models.facebook.FacebookPhoto;
import com.sanction.lightning.models.facebook.FacebookPhotoDetail;
import com.sanction.lightning.models.facebook.FacebookUser;
import com.sanction.lightning.models.facebook.FacebookVideo;

import java.io.InputStream;
import java.util.List;

public class FacebookService {
  private static final String REDIRECT_URL = "http://example.com";
  private static final Version VERSION = Version.VERSION_2_4;

  private final DefaultFacebookClient client;
  private final String appId;
  private final String appSecret;

  /**
   * Constructs a new FacebookService for making requests to Facebook.
   *
   * @param facebookAccessToken The authenticating user's access token.
   * @param facebookApplicationId The requesting application's ID.
   * @param facebookApplicationSecret The requesting application's secret.
   */
  public FacebookService(String facebookAccessToken, String facebookApplicationId,
                         String facebookApplicationSecret) {
    this.client = new DefaultFacebookClient(facebookAccessToken, facebookApplicationSecret,
        VERSION);
    this.appId = facebookApplicationId;
    this.appSecret = facebookApplicationSecret;
  }

  /**
   * Constructs a new FacebookService for making requests to Facebook.
   *
   * @param facebookApplicationId The requesting application's ID.
   * @param facebookApplicationSecret The requesting application's secret.
   */
  public FacebookService(String facebookApplicationId, String facebookApplicationSecret) {
    this.client = new DefaultFacebookClient(VERSION);
    this.appId = facebookApplicationId;
    this.appSecret = facebookApplicationSecret;
  }

  /**
   * Retrieves Facebook user information for the authenticating user.
   *
   * @return A FacebookUser object containing the user's information.
   */
  public FacebookUser getFacebookUser() {
    return client.fetchObject("me", FacebookUser.class,
        Parameter.with("fields", "first_name, last_name, middle_name, gender, name, verified"));
  }

  /**
   * Retrieves the authenticating user's Facebook photos.
   * This method does not download the actual photo bytes.
   *
   * @return A list of FacebookPhoto objects representing the user's photos.
   */
  public List<FacebookPhoto> getFacebookUserPhotos() {
    // Fetch a JSON object containing an array of photos, each with specified properties
    JsonObject photos = client.fetchObject("me/photos", JsonObject.class,
            Parameter.with("type", "uploaded"), Parameter.with("fields", "id, images"));

    // Fetch the photo array from JsonObject and make a JsonArray
    JsonArray photosArray = photos.getJsonArray("data");

    List<FacebookPhoto> photoList = Lists.newArrayList();

    // Construct a JsonMapper for JSON string to FacebookPhotoDetail conversion
    DefaultJsonMapper mapper = new DefaultJsonMapper();
    for (int i = 0; i < photosArray.length(); i++) {
      JsonObject obj = photosArray.getJsonObject(i);
      List<FacebookPhotoDetail> detailList = mapper.toJavaList(obj.getString("images"),
          FacebookPhotoDetail.class);
      FacebookPhotoDetail detail = detailList.get(0);
      FacebookPhoto pic = new FacebookPhoto(obj.getString("id"), detail.getUri(),
          detail.getHeight(), detail.getWidth());
      photoList.add(pic);
    }

    return photoList;
  }

  /**
   * Publishes a photo or video to Facebook.
   *
   * @param inputStream The InputStream of the file to upload to Facebook.
   * @param type The type to upload. Should be either "photo" or "video"
   * @param fileName The name to call the file on Facebook.
   * @param message The caption for the photo or video.
   * @param videoTitle The title of the video if uploading a video.
   *                   Will be ignored when uploading a photo.
   * @return A String of JSON with returned information if successful, or {@code null} on failure.
   */
  public String publishToFacebook(InputStream inputStream, String type, String fileName,
                                  String message, String videoTitle) {
    List<Parameter> parameters = Lists.newArrayList();

    if (type.equals("photo")) {
      parameters.add(Parameter.with("message", message));
    } else {
      parameters.add(Parameter.with("description", message));
      parameters.add(Parameter.with("title", videoTitle));
    }

    JsonObject response;

    try {
      response = client.publish("me/" + type + "s", JsonObject.class,
          BinaryAttachment.with(fileName, inputStream),
          parameters.toArray(new Parameter[parameters.size()]));
    } catch (FacebookException e) {
      return null;
    }
    return response.toString();
  }

  /**
   * Retrieves the authenticating user's Facebook videos.
   * This method does not download the actual video bytes.
   *
   * @return A list of FacebookVideo objects representing the user's videos.
   */
  public List<FacebookVideo> getFacebookUserVideos() {
    JsonObject videos = client.fetchObject("me/videos", JsonObject.class,
            Parameter.with("type", "uploaded"), Parameter.with("fields", "id, source"));
    JsonArray videoArray = videos.getJsonArray("data");

    List<FacebookVideo> videoList = Lists.newArrayList();
    for (int i = 0; i < videos.length() - 1; i++) {
      JsonObject obj = videoArray.getJsonObject(i);
      FacebookVideo vid = new FacebookVideo(obj.getString("id"), obj.getString("source"));
      videoList.add(vid);
    }

    return videoList;
  }

  /**
   * Builds a URL that sends a user to a Facebook authentication page
   * to request the correct permissions.
   *
   * @return The URL string for the permissions URL.
   */
  public String getOauthUrl() {
    ScopeBuilder scopeBuilder = new ScopeBuilder()
        .addPermission(UserDataPermissions.USER_PHOTOS)
        .addPermission(UserDataPermissions.USER_VIDEOS)
        .addPermission(UserDataPermissions.USER_POSTS)
        .addPermission(UserDataPermissions.USER_ACTIONS_VIDEO)
        .addPermission(ExtendedPermissions.PUBLISH_ACTIONS);

    return client.getLoginDialogUrl(appId, REDIRECT_URL, scopeBuilder);
  }

  /**
   * Retrieves an extended token for the authenticating user from Facebook,
   * using their existing token.
   *
   * @return The extended token.
   */
  public String getFacebookExtendedToken() {
    AccessToken accessToken = client.obtainExtendedAccessToken(appId, appSecret);

    return accessToken.getAccessToken();
  }
}
