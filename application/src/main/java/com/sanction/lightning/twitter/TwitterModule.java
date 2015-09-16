package com.sanction.lightning.twitter;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class TwitterModule {
  private final TwitterConfiguration twitterConfiguration;

  public TwitterModule(TwitterConfiguration twitterConfiguration) {
    this.twitterConfiguration = twitterConfiguration;
  }

  /**
   * Provides a new TwitterServiceFactory for generating TwitterServices.
   * @return A new instance of TwitterServiceFactory.
   */
  @Singleton
  @Provides
  public TwitterServiceFactory provideTwitterServiceFactory() {
    return new TwitterServiceFactory(
        twitterConfiguration.getAppKey(),
        twitterConfiguration.getAppSecret());
  }
}
