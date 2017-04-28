package com.sanction.lightning.facebook;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class FacebookModule {
  private final FacebookConfiguration facebookConfiguration;

  public FacebookModule(FacebookConfiguration facebookConfiguration) {
    this.facebookConfiguration = facebookConfiguration;
  }

  /**
   * Provides a new FacebookServiceFactory for generating instances of FacebookService objects.
   *
   * @return A new instance of FacebookServiceFactory.
   */
  @Singleton
  @Provides
  FacebookServiceFactory provideFacebookServiceFactory() {
    return new FacebookServiceFactory(
        facebookConfiguration.getAppId(),
        facebookConfiguration.getAppSecret());
  }
}
