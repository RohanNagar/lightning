package com.sanction.lightning.facebook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class FacebookApplicationKey {
  private final String appId;
  private final String appSecret;

  @JsonCreator
  public FacebookApplicationKey(@JsonProperty("app-id") String appId,
                                @JsonProperty("app-secret") String appSecret) {
    this.appId = checkNotNull(appId);
    this.appSecret = checkNotNull(appSecret);
  }

  public String getSecret() {
    return appSecret;
  }

  public String getAppId() {
    return appId;
  }
}
