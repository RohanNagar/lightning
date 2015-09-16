package com.sanction.lightning;

import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.authentication.LightningAuthenticator;
import com.sanction.lightning.facebook.FacebookModule;
import com.sanction.lightning.twitter.TwitterModule;
import com.sanction.thunder.ThunderBuilder;
import com.sanction.thunder.ThunderClient;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
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
    // Set up Thunder client
    ThunderBuilder thunderBuilder = new ThunderBuilder(
        config.getThunderConfiguration().getEndpoint(),
        config.getThunderConfiguration().getUserKey(),
        config.getThunderConfiguration().getUserSecret());
    ThunderClient thunderClient = thunderBuilder.newThunderClient();

    LightningComponent component = DaggerLightningComponent.builder()
        .facebookModule(new FacebookModule(config.getFacebookConfiguration()))
        .lightningModule(new LightningModule(thunderClient))
        .twitterModule(new TwitterModule(config.getTwitterConfiguration()))
        .build();

    // Authentication
    env.jersey().register(AuthFactory.binder(new BasicAuthFactory<>(
        new LightningAuthenticator(config.getApprovedKeys()), "AUTHENTICATION", Key.class)));

    // Resources
    env.jersey().register(component.getFacebookResource());
    env.jersey().register(component.getTwitterResource());
  }
}