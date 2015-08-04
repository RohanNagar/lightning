package com.sanction.lightning;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanction.lightning.config.ThunderConfiguration;
import io.dropwizard.Configuration;

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
}
