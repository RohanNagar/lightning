package com.sanction.lightning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.config.ThunderConfiguration;
import com.sanction.lightning.dropbox.DropboxConfiguration;
import com.sanction.lightning.facebook.FacebookConfiguration;
import com.sanction.lightning.twitter.TwitterConfiguration;
import io.dropwizard.Configuration;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class LightningConfiguration extends Configuration {

  @NotNull
  @Valid
  @JsonProperty("thunder")
  private final ThunderConfiguration thunderConfiguration = null;

  ThunderConfiguration getThunderConfiguration() {
    return thunderConfiguration;
  }

  @NotNull
  @Valid
  @JsonProperty("dropbox")
  private final DropboxConfiguration dropboxConfiguration = null;

  DropboxConfiguration getDropboxConfiguration() {
    return dropboxConfiguration;
  }

  @NotNull
  @Valid
  @JsonProperty("facebook")
  private final FacebookConfiguration facebookConfiguration = null;

  FacebookConfiguration getFacebookConfiguration() {
    return facebookConfiguration;
  }

  @NotNull
  @Valid
  @JsonProperty("twitter")
  private final TwitterConfiguration twitterConfiguration = null;

  TwitterConfiguration getTwitterConfiguration() {
    return twitterConfiguration;
  }

  @NotNull
  @Valid
  @JsonProperty("approved-keys")
  private final List<Key> approvedKeys = null;

  public List<Key> getApprovedKeys() {
    return approvedKeys;
  }
}
