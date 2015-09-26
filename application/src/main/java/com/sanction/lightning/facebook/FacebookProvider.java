package com.sanction.lightning.facebook;

import com.google.common.collect.Lists;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.scope.ScopeBuilder;
import com.restfb.scope.UserDataPermissions;

import com.sanction.lightning.models.FacebookPhoto;
import com.sanction.lightning.models.FacebookUser;
import com.sanction.lightning.models.FacebookVideo;

import java.util.List;

public class FacebookProvider {
  private static final String REDIRECT_URL = "example.com";

  private final DefaultFacebookClient client;
  private final String appId;
  private final String appSecret;

  /**
   * Constructs a new FacebookProvider for making requests to Facebook.
   *
   * @param facebookAccessToken The access token for the user.
   * @param facebookApplicationId The facebook application id
   * @param facebookApplicationSecret The authenticating application secret.
   */
  public FacebookProvider(String facebookAccessToken, String facebookApplicationId,
                          String facebookApplicationSecret) {
    this.client = new DefaultFacebookClient(facebookAccessToken, facebookApplicationSecret,
        Version.VERSION_2_4);
    this.appId = facebookApplicationId;
    this.appSecret = facebookApplicationSecret;
  }

  /**
   * Constructs a new FacebookProvider for making requests to Facebook.
   *
   * @param facebookApplicationId The Facebook consumer application id.
   * @param facebookApplicationSecret The Facebook consumer application secret.
   */
  public FacebookProvider(String facebookApplicationId, String facebookApplicationSecret) {
    this.client = new DefaultFacebookClient(Version.VERSION_2_4);
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
            Parameter.with("type", "uploaded"), Parameter.with("fields", "id, link"));

    // Fetch the array from JsonObject and make a JsonArray
    JsonArray photosArray = photos.getJsonArray("data");

    // Construct a new list of FacebookPhoto objects to return
    List<FacebookPhoto> photoList = Lists.newArrayList();
    for (int i = 0; i < photosArray.length(); i++) {
      JsonObject obj = photosArray.getJsonObject(i);
      FacebookPhoto pic = new FacebookPhoto(obj.getString("id"), obj.getString("link"));
      photoList.add(pic);
    }

    return photoList;
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
