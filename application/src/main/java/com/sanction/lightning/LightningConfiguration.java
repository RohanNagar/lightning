package com.sanction.lightning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.config.ThunderConfiguration;
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

  public ThunderConfiguration getThunderConfiguration() {
    return thunderConfiguration;
  }

  @NotNull
  @Valid
  @JsonProperty("facebook")
  private final FacebookConfiguration facebookConfiguration = null;

  public FacebookConfiguration getFacebookConfiguration() {
    return facebookConfiguration;
  }

  @NotNull
  @Valid
  @JsonProperty("twitter")
  private final TwitterConfiguration twitterConfiguration = null;

  public TwitterConfiguration getTwitterConfiguration() {
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
