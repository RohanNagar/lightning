package com.sanction.lightning.models.facebook;

import com.restfb.Facebook;

public class FacebookPhotoDetail {

  @Facebook("source")
  private String source;

  @Facebook("height")
  private String height;

  @Facebook("width")
  private String width;

  public FacebookPhotoDetail() {

  }

  public String getUri() {
    return source;
  }

  public String getHeight() {
    return height;
  }

  public String getWidth() {
    return width;
  }

}
