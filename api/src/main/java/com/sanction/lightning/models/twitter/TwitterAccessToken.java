package com.sanction.lightning.models.twitter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class TwitterAccessToken {
  private final String accessToken;
  private final String accessSecret;

  /**
   * Constructs a new TwitterAccessToken from the given parameters.
   *
   * @param accessToken The access token retrieved from Twitter.
   * @param accessSecret The access token secret retrieved from Twitter.
   */
  @JsonCreator
  public TwitterAccessToken(@JsonProperty("accessToken") String accessToken,
                            @JsonProperty("accessSecret") String accessSecret) {
    this.accessToken = accessToken;
    this.accessSecret = accessSecret;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getAccessSecret() {
    return accessSecret;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof TwitterAccessToken)) {
      return false;
    }

    TwitterAccessToken other = (TwitterAccessToken) obj;
    return Objects.equals(this.accessToken, other.accessToken)
        && Objects.equals(this.accessSecret, other.accessSecret);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.accessToken, this.accessSecret);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "TwitterAccessToken [", "]")
        .add(String.format("accessToken=%s", accessToken))
        .add(String.format("accessSecret=%s", accessSecret))
        .toString();
  }
}
