package com.sanction.lightning.twitter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class TwitterConfiguration {

  @NotEmpty
  @JsonProperty("app-key")
  private final String appKey = null;

  @NotEmpty
  @JsonProperty("app-secret")
  private final String appSecret = null;

  public String getAppKey() {
    return appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }
}
