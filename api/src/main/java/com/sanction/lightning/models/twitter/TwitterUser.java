package com.sanction.lightning.models.twitter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class TwitterUser {

  @JsonProperty("id")
  private final long id;

  @JsonProperty("favouritesCount")
  private final int favouritesCount;

  @JsonProperty("followersCount")
  private final int followersCount;

  @JsonProperty("createdAt")
  private final String createdAt;

  @JsonProperty("location")
  private final String location;

  @JsonProperty("name")
  private final String name;

  @JsonProperty("screenName")
  private final String screenName;

  @JsonProperty("profileImageUrl")
  private final String profileImageUrl;

  @JsonProperty("verified")
  private final boolean verified;

  /**
   * Constructs a new TwitterUser from the given parameters.
   *
   * @param id The TwitterID of the user.
   * @param favouritesCount The number of favourites the user has.
   * @param followersCount The number of followers the user has.
   * @param createdAt The date the user was created on Twitter.
   * @param location The location of the user.
   * @param name The name of the user.
   * @param screenName The screen name of the user.
   * @param profileImageUrl The URL to access the profile image of the user.
   * @param verified True if the user is verified, false otherwise.
   */
  @JsonCreator
  public TwitterUser(@JsonProperty("id") long id,
                     @JsonProperty("favouritesCount") int favouritesCount,
                     @JsonProperty("followersCount") int followersCount,
                     @JsonProperty("createdAt") String createdAt,
                     @JsonProperty("location") String location,
                     @JsonProperty("name") String name,
                     @JsonProperty("screenName") String screenName,
                     @JsonProperty("profileImageUrl") String profileImageUrl,
                     @JsonProperty("verified") boolean verified) {
    this.id = id;
    this.favouritesCount = favouritesCount;
    this.followersCount = followersCount;
    this.createdAt = createdAt;
    this.location = location;
    this.name = name;
    this.screenName = screenName;
    this.profileImageUrl = profileImageUrl;
    this.verified = verified;
  }

  public long getId() {
    return id;
  }

  public int getFavouritesCount() {
    return favouritesCount;
  }

  public int getFollowersCount() {
    return followersCount;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public String getLocation() {
    return location;
  }

  public String getName() {
    return name;
  }

  public String getScreenName() {
    return screenName;
  }

  public String getProfileImageUrl() {
    return profileImageUrl;
  }

  public boolean isVerified() {
    return verified;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof TwitterUser)) {
      return false;
    }

    TwitterUser other = (TwitterUser) obj;
    return Objects.equal(this.id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.id);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("createdAt", createdAt)
        .add("screenName", screenName)
        .add("name", name)
        .add("verified", verified)
        .toString();
  }
}
