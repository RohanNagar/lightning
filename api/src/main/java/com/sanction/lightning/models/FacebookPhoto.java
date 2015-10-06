package com.sanction.lightning.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restfb.Facebook;

public class FacebookPhoto {

  @Facebook
  private String id;

  @Facebook("uri")
  private String url;

  @Facebook
  private String height;

  @Facebook
  private String width;

  /**
   * Constructs a new FacebookPhoto representing a facebook user photo.
   * @param id the photo id from facebook
   * @param url uri of the facebook photo
   * @param height height in pixels of the photo
   * @param width width in pixels of the photo
   */
  public FacebookPhoto(String id, String url, String height, String width) {
    this.id = id;
    this.url = url;
    this.height = height;
    this.width = width;
  }

  public FacebookPhoto() {

  }

  @JsonProperty
  public String getId() {
    return id;
  }

  @JsonProperty
  public String getUrl() {
    return url;
  }

  @JsonProperty
  public String getHeight() {
    return height;
  }

  @JsonProperty
  public String getWidth() {
    return width;
  }
}
