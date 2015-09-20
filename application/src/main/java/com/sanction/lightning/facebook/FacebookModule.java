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

  @Singleton
  @Provides
  public FacebookProviderFactory provideFacebookProviderFactory() {
    return new FacebookProviderFactory(facebookConfiguration.getAppId(),
            facebookConfiguration.getAppSecret());
  }
}