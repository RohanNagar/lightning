package com.sanction.lightning.dropbox;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DropboxModule {
  private final DropboxConfiguration dropboxConfiguration;

  public DropboxModule(DropboxConfiguration dropboxConfiguration) {
    this.dropboxConfiguration = dropboxConfiguration;
  }

  /**
   * Provides a new DropboxServiceFactory for generating instances of DropboxService objects.
   *
   * @return A new instance of DropboxServiceFactory.
   */
  @Singleton
  @Provides
  DropboxServiceFactory provideDropboxServiceFactory() {
    return new DropboxServiceFactory(
        dropboxConfiguration.getAppKey(),
        dropboxConfiguration.getAppSecret());
  }
}
