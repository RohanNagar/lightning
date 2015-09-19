package com.sanction.lightning.twitter;

public class TwitterServiceFactory {

  private final String applicationKey;
  private final String applicationSecret;

  public TwitterServiceFactory(String applicationKey, String applicationSecret) {
    this.applicationKey = applicationKey;
    this.applicationSecret = applicationSecret;
  }

  public TwitterService newTwitterService(String userKey, String userSecret) {
    return new TwitterService(applicationKey, applicationSecret, userKey, userSecret);
  }

  public TwitterService newTwitterService() {
    return new TwitterService(applicationKey, applicationSecret);
  }
}
