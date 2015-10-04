package com.sanction.lightning;

import com.sanction.lightning.utils.UrlService;
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
  public UrlService provideUrlService() {
    return new UrlService();
  }
}
