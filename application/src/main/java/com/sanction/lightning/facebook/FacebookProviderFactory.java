package com.sanction.lightning.facebook;

public class FacebookProviderFactory {

  private final String facebookApplicationSecret;
  private final String facebookApplicationId;

  public FacebookProviderFactory(String facebookApplicationId, String facebookApplicationSecret) {
    this.facebookApplicationSecret = facebookApplicationSecret;
    this.facebookApplicationId = facebookApplicationId;
  }

  public FacebookProvider newFacebookProvider(String facebookAccessToken) {
    return new FacebookProvider(facebookAccessToken,
            facebookApplicationId, facebookApplicationSecret);
  }

  public FacebookProvider newFacebookProvider() {
    return new FacebookProvider(facebookApplicationId, facebookApplicationSecret);
  }
}
