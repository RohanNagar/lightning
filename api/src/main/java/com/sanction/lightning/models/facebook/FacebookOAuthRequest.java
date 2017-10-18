package com.sanction.lightning.models.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class FacebookOAuthRequest {
  private final String url;

  /**
   * Constructs a new FacebookOAuthRequest from the given parameters.
   *
   * @param url The URL to direct the user in order to authenticate this request.
   */
  public FacebookOAuthRequest(@JsonProperty("url") String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof FacebookOAuthRequest)) {
      return false;
    }

    FacebookOAuthRequest other = (FacebookOAuthRequest) obj;
    return Objects.equals(this.url, other.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.url);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "FacebookOAuthRequest [", "]")
        .add(String.format("url=%s", url))
        .toString();
  }
}
