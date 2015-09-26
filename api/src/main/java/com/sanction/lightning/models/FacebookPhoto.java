package com.sanction.lightning.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FacebookPhoto {

  private final String id;
  private final String url;

  public FacebookPhoto(String id, String url) {
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
