package com.sanction.lightning.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ThunderConfiguration {

  @NotEmpty
  @JsonProperty("endpoint")
  private final String endpoint = null;

  @NotEmpty
  @JsonProperty("user-key")
  private final String userKey = null;

  @NotEmpty
  @JsonProperty("user-secret")
  private final String userSecret = null;

  public String getEndpoint() {
    return endpoint;
  }

  public String getUserKey() {
    return userKey;
  }

  public String getUserSecret() {
    return userSecret;
  }
}
