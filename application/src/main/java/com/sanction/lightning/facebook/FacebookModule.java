package com.sanction.lightning.facebook;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class FacebookModule {
  private final FacebookApplicationKey facebookApplicationKey;

  public FacebookModule(FacebookApplicationKey applicationKey) {
    this.facebookApplicationKey = applicationKey;
  }

  @Singleton
  @Provides
  public FacebookApplicationKey provideFacebookAuth() {
    return facebookApplicationKey;
  }
}