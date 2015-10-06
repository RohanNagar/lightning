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
import com.sanction.lightning.utils.CustomWebRequester;

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
   * @param facebookAccessToken The access token for the user.
   * @param facebookApplicationId The facebook application id.
   * @param facebookApplicationSecret The authenticating application secret.
   */
  public FacebookService(String facebookAccessToken, String facebookApplicationId,
                         String facebookApplicationSecret) {
    this.client = new DefaultFacebookClient(facebookAccessToken, facebookApplicationSecret,
            new CustomWebRequester(), new DefaultJsonMapper(), VERSION);
    this.appId = facebookApplicationId;
    this.appSecret = facebookApplicationSecret;
  }

  /**
   * Constructs a new FacebookService for making requests to Facebook.
   *
   * @param facebookApplicationId The Facebook consumer application id.
   * @param facebookApplicationSecret The Facebook consumer application secret.
   */
  public FacebookService(String facebookApplicationId, String facebookApplicationSecret) {
    this.client = new DefaultFacebookClient(VERSION);
    this.appId = facebookApplicationId;
    this.appSecret = facebookApplicationSecret;
  }

  /**
   * Gets Facebook user information for a specific user.
   *
   * @return A FacebookUser object containing user information.
   */
  public FacebookUser getFacebookUser() {
    return client.fetchObject("me", FacebookUser.class, Parameter.with("fields",
            "first_name, last_name, middle_name, gender, name, verified"));
  }

  /**
   * Gets the user's photos.
   *
   * @return A list of FacebookPhoto objects representing the users photos.
   */
  public List<FacebookPhoto> getFacebookUserPhotos() {
    // Fetch a json object containing an array of photos, each with specified properties
    JsonObject photos = client.fetchObject("me/photos", JsonObject.class,
            Parameter.with("type", "uploaded"), Parameter.with("fields", "id, images"));

    // Fetch the array from JsonObject and make a JsonArray
    JsonArray photosArray = photos.getJsonArray("data");

    // Construct a new list of FacebookPhoto objects to return
    List<FacebookPhoto> photoList = Lists.newArrayList();

    // Construct a JsonMapper for json string to FacebookPhotoDetail object conversion
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
   * Published a file to facebook using the restFB api.
   *
   * @return A json String with returned file information denoting the call to Facebook worked.
   */
  public String publishToFacebook(byte[] inputBytes, String type, String fileName,
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
              BinaryAttachment.with(fileName, inputBytes),
              parameters.toArray(new Parameter[parameters.size()]));
    } catch (FacebookException e) {
      return null;
    }
    return response.toString();
  }

  /**
   * Gets the user's videos from Facebook.
   *
   * @return A list of FacebookVideo objects representing the users videos.
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
   * Sets the specified users permissions.
   *
   * @return The URL string for the permissions URL.
   */
  public String getOauthUrl() {
    ScopeBuilder scopeBuilder = new ScopeBuilder();
    scopeBuilder.addPermission(UserDataPermissions.USER_PHOTOS);
    scopeBuilder.addPermission(UserDataPermissions.USER_VIDEOS);
    scopeBuilder.addPermission(UserDataPermissions.USER_POSTS);
    scopeBuilder.addPermission(UserDataPermissions.USER_ACTIONS_VIDEO);
    scopeBuilder.addPermission(ExtendedPermissions.PUBLISH_ACTIONS);

    return client.getLoginDialogUrl(appId, REDIRECT_URL, scopeBuilder);
  }

  /**
   * Fetches an extended token from Facebook.
   *
   * @return An extended token, using the existing token.
   */
  public String getFacebookExtendedToken() {
    AccessToken accessToken = client
            .obtainExtendedAccessToken(appId, appSecret);
    return accessToken.getAccessToken();
  }
}
