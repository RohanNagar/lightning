package com.sanction.lightning;

import com.sanction.thunder.ThunderBuilder;
import com.sanction.thunder.ThunderClient;
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

    ThunderBuilder thunderBuilder = new ThunderBuilder("http://localhost:9000", "user", "secret");
    ThunderClient thunderClient = thunderBuilder.newThunderClient();

    env.jersey().register(component.getFacebookResource());
  }

}
