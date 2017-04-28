package com.sanction.lightning.dropbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class DropboxConfiguration {

  @NotEmpty
  @JsonProperty("app-key")
  private final String appKey = null;

  @NotEmpty
  @JsonProperty("app-secret")
  private final String appSecret = null;

  String getAppKey() {
    return appKey;
  }

  String getAppSecret() {
    return appSecret;
  }
}
