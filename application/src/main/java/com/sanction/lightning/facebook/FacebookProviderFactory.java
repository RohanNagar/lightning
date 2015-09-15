package com.sanction.lightning.facebook;

public class FacebookProviderFactory {

  private final String facebookApplicationSecret;

  public FacebookProviderFactory(String facebookApplicationSecret) {
    this.facebookApplicationSecret = facebookApplicationSecret;
  }

  public FacebookProvider newFacebookProvider(String facebookAccessToken) {
    return new FacebookProvider(facebookAccessToken, facebookApplicationSecret);
  }
}
