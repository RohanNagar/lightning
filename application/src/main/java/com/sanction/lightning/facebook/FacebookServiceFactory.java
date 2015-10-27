package com.sanction.lightning.facebook;

public class FacebookServiceFactory {

  private final String applicationId;
  private final String applicationSecret;

  public FacebookServiceFactory(String applicationId, String applicationSecret) {
    this.applicationId = applicationId;
    this.applicationSecret = applicationSecret;
  }

  public FacebookService newFacebookService(String facebookAccessToken) {
    return new FacebookService(facebookAccessToken, applicationId, applicationSecret);
  }

  public FacebookService newFacebookService() {
    return new FacebookService(applicationId, applicationSecret);
  }
}
