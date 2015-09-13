package com.sanction.lightning.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.restfb.Facebook;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class FacebookPost {

  @Facebook("id")
  private String facebookId;

  @Facebook("caption")
  private String caption;

  @Facebook("created_time")
  private Date createdTime;

  @Facebook("description")
  private String description;

  @Facebook("from")
  private Object from;

  @Facebook("link")
  private String link;

  @Facebook("message")
  private String message;

  @Facebook("message_tags")
  private Map<String, List<Object>> messageTags;

  @Facebook("name")
  private String name;

  @Facebook("object_id")
  private String attachedMedia;

  @Facebook("picture")
  private String picture;

  @Facebook("place")
  private Object place;

  @Facebook("properties")
  private List<Object> attachedMediaProperties;

  @Facebook("shares")
  private Object shares;

  @Facebook("source")
  private String mediaSource;

  @Facebook("status_type")
  private String statusType;

  @Facebook("story")
  private String story;

  @Facebook("to")
  private List<Object> to;

  @Facebook("updated_time")
  private Date updatedTime;

  public FacebookPost() {

  }

  @JsonProperty
  public String getId() {
    return facebookId;
  }

  @JsonProperty
  public String getCaption() {
    return caption;
  }

  @JsonProperty
  public Date getCreatedTime() {
    return createdTime;
  }

  @JsonProperty
  public String getDescription() {
    return description;
  }

  @JsonProperty
  public Object getfrom() {
    return description;
  }

  @JsonProperty
  public String getLink() {
    return link;
  }

  @JsonProperty
  public String getMessage() {
    return message;
  }

  @JsonProperty
  public Map<String, List<Object>> getMesageTags() {
    return messageTags;
  }

  @JsonProperty
  public String getName() {
    return name;
  }

  @JsonProperty
  public String getAttachedMedia() {
    return attachedMedia;
  }

  @JsonProperty
  public String getPicture() {
    return picture;
  }

  @JsonProperty
  public Object getPlace() {
    return place;
  }

  @JsonProperty
  public List<Object> getAttachedMediaProperties() {
    return attachedMediaProperties;
  }

  @JsonProperty
  public Object getShares() {
    return shares;
  }

  @JsonProperty
  public String getMediaSource() {
    return mediaSource;
  }

  @JsonProperty
  public String getStatusType() {
    return statusType;
  }

  @JsonProperty
  public String getStory() {
    return story;
  }

  @JsonProperty
  public List<Object> getTo() {
    return to;
  }

  @JsonProperty
  public Date getUpdateTime() {
    return updatedTime;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof FacebookPost)) {
      return false;
    }

    FacebookPost other = (FacebookPost) obj;
    return Objects.equal(this.name, other.name)
            && Objects.equal(this.facebookId, other.facebookId)
            && Objects.equal(this.caption, other.caption)
            && Objects.equal(this.createdTime, other.createdTime)
            && Objects.equal(this.description, other.description)
            && Objects.equal(this.from, other.from)
            && Objects.equal(this.link, other.link)
            && Objects.equal(this.message, other.message)
            && Objects.equal(this.messageTags, other.messageTags)
            && Objects.equal(this.name, other.name)
            && Objects.equal(this.attachedMedia, other.attachedMedia)
            && Objects.equal(this.picture, other.picture)
            && Objects.equal(this.place, other.place)
            && Objects.equal(this.attachedMediaProperties, other.attachedMediaProperties)
            && Objects.equal(this.shares, other.shares)
            && Objects.equal(this.mediaSource, other.mediaSource)
            && Objects.equal(this.statusType, other.statusType)
            && Objects.equal(this.story, other.story)
            && Objects.equal(this.to, other.to)
            && Objects.equal(this.updatedTime, other.updatedTime);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.facebookId, this.caption, this.createdTime, this.description,
            this.from, this.link, this.message, this.messageTags, this.name, this.attachedMedia,
            this.picture, this.place, this.attachedMediaProperties, this.shares, this.mediaSource,
            this.statusType, this.story, this.to, this.updatedTime);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("facebookId", facebookId)
            .add("caption", caption)
            .add("createdTime", createdTime)
            .add("description", description)
            .add("from", from)
            .add("link", link)
            .add("message", message)
            .add("messageTags", messageTags)
            .add("name", name)
            .add("attachedMedia", attachedMedia)
            .add("pictures", picture)
            .add("place", place)
            .add("attachedMediaProperties", attachedMediaProperties)
            .add("shares", shares)
            .add("mediaSource", mediaSource)
            .add("statusType", statusType)
            .add("story", story)
            .add("to", to)
            .add("updateTime", updatedTime)
            .toString();
  }
}
