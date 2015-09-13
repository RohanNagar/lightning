package com.sanction.lightning.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.sanction.lightning.models.FacebookPost;
import com.sanction.lightning.models.FacebookUser;

import java.util.List;

public class FacebookProvider {


  private final DefaultFacebookClient client;

  /**
   * Constructs a new FacebookProvider for making requests to facebook.
   * @param facebookAccessToken the access token for the user
   * @param applicationKey the authentication credentials used with this application
   */
  public FacebookProvider(String facebookAccessToken, FacebookApplicationKey applicationKey) {
    this.client = new DefaultFacebookClient(facebookAccessToken,
            applicationKey.getSecret(), Version.VERSION_2_4);
  }

  /**
   * Gets facebook user information for a specific user.
   * @return a FacebookUser object containing user information
   */
  public FacebookUser getFacebookUser() {
    return client.fetchObject("me", FacebookUser.class, Parameter.with("fields",
            "first_name, last_name, middle_name, gender, name, verified"));
  }

  /**
   * Gets facebook news feed for a specific user.
   * @return a List of FacebookPost objects
   */
  public List<FacebookPost> getFacebookFeed() {
    return client.fetchConnection("me/feed", FacebookPost.class,
            Parameter.with("limit", 25)).getData();
  }
}