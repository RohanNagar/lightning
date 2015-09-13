package com.sanction.lightning.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.restfb.Facebook;

public class FacebookUser {

  @Facebook("id")
  private String facebookId;

  @Facebook("name")
  private String name;

  @Facebook("first_name")
  private String firstName;

  @Facebook("middle_name")
  private String middleName;

  @Facebook("last_name")
  private String lastName;

  @Facebook
  private String gender;

  @Facebook
  private boolean verified;

  public FacebookUser() {

  }

  @JsonProperty("facebookId")
  public String getFacebookId() {
    return facebookId;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  @JsonProperty("middleName")
  public String getMiddleName() {
    return middleName;
  }

  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  @JsonProperty("gender")
  public String getGender() {
    return gender;
  }

  @JsonProperty("verified")
  public boolean isVerified() {
    return verified;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof FacebookUser)) {
      return false;
    }

    FacebookUser other = (FacebookUser) obj;
    return Objects.equal(this.name, other.name)
            && Objects.equal(this.firstName, other.firstName)
            && Objects.equal(this.middleName, other.middleName)
            && Objects.equal(this.lastName, other.lastName)
            && Objects.equal(this.gender, other.gender)
            && Objects.equal(this.verified, other.verified);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name, this.firstName, this.middleName, this.lastName,
            this.gender, this.verified);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("name", name)
            .add("firstName", firstName)
            .add("middleName", middleName)
            .add("lastName", lastName)
            .add("gender", gender)
            .add("verified", verified)
            .toString();
  }
}
