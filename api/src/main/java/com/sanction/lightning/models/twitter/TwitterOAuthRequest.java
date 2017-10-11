package com.sanction.lightning.models.twitter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterOAuthRequest {
  private final String requestToken;
  private final String requestSecret;
  private final String url;

  public TwitterOAuthRequest(@JsonProperty("requestToken") String requestToken,
                             @JsonProperty("requestSecret") String requestSecret,
                             @JsonProperty("url") String url) {
    this.requestToken = requestToken;
    this.requestSecret = requestSecret;
    this.url = url;
  }

  public String getRequestToken() {
    return requestToken;
  }

  public String getRequestSecret() {
    return requestSecret;
  }

  public String getUrl() {
    return url;
  }
}
