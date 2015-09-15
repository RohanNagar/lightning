package com.sanction.lightning.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.sanction.lightning.models.FacebookUser;

public class FacebookProvider {

  private final DefaultFacebookClient client;

  /**
   * Constructs a new FacebookProvider for making requests to Facebook.
   * @param facebookAccessToken The access token for the user.
   * @param facebookAppSecret The authenticating application secret.
   */
  public FacebookProvider(String facebookAccessToken, String facebookAppSecret) {
    this.client = new DefaultFacebookClient(facebookAccessToken, facebookAppSecret,
        Version.VERSION_2_4);
  }

  /**
   * Gets Facebook user information for a specific user.
   * @return A FacebookUser object containing user information.
   */
  public FacebookUser getFacebookUser() {
    return client.fetchObject("me", FacebookUser.class, Parameter.with("fields",
            "first_name, last_name, middle_name, gender, name, verified"));
  }
}