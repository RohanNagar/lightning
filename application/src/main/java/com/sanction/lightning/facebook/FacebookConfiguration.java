package com.sanction.lightning.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

public class FacebookConfiguration {

  @NotEmpty
  @JsonProperty("app-id")
  private final String appId = null;

  @NotEmpty
  @JsonProperty("app-secret")
  private final String appSecret = null;

  String getAppId() {
    return appId;
  }

  String getAppSecret() {
    return appSecret;
  }
}
