package com.sanction.lightning;

import com.sanction.lightning.utils.UrlDownloadService;
import com.sanction.thunder.ThunderClient;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LightningModule {
  private final ThunderClient thunderClient;

  public LightningModule(ThunderClient thunderClient) {
    this.thunderClient = thunderClient;
  }

  @Singleton
  @Provides
  public ThunderClient provideThunderClient() {
    return thunderClient;
  }

  @Singleton
  @Provides
  public UrlDownloadService provideUrlDownloadService() {
    return new UrlDownloadService();
  }
}
