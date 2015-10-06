package com.sanction.lightning.models.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookVideo {

  private final String id;
  private final String url;

  public FacebookVideo(String id, String url) {
    this.id = id;
    this.url = url;
  }

  @JsonProperty
  public String getId() {
    return id;
  }

  @JsonProperty
  public String getUrl() {
    return url;
  }
}
