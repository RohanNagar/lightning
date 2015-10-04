package com.sanction.lightning.facebook;

public class FacebookServiceFactory {

  private final String facebookApplicationSecret;
  private final String facebookApplicationId;

  public FacebookServiceFactory(String facebookApplicationId, String facebookApplicationSecret) {
    this.facebookApplicationSecret = facebookApplicationSecret;
    this.facebookApplicationId = facebookApplicationId;
  }

  public FacebookService newFacebookService(String facebookAccessToken) {
    return new FacebookService(facebookAccessToken,
            facebookApplicationId, facebookApplicationSecret);
  }

  public FacebookService newFacebookService() {
    return new FacebookService(facebookApplicationId, facebookApplicationSecret);
  }
}
