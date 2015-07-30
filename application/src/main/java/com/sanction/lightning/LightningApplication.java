package com.sanction.lightning;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class LightningApplication extends Application<LightningConfiguration> {

  public static void main(String[] args) throws Exception {
    new LightningApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<LightningConfiguration> bootstrap) {

  }

  @Override
  public void run(LightningConfiguration config, Environment env) {
    LightningComponent component = DaggerLightningComponent.builder()
        .build();

    env.jersey().register(component.getFacebookResource());
  }

}
