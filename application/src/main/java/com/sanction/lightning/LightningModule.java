package com.sanction.lightning;

import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.LightningAuthenticator;
import com.sanctionco.thunder.ThunderClient;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class LightningModule {
  private final MetricRegistry metrics;
  private final ThunderClient thunderClient;
  private final LightningConfiguration config;

  /**
   * Instantiates a new LightningModule object with the specified parameters.
   *
   * @param thunderClient Client for connecting to thunder.
   * @param config Configuration class for Lightning.
   * @param metrics Metrics class for resource classes.
   */
  public LightningModule(ThunderClient thunderClient, LightningConfiguration config,
                         MetricRegistry metrics) {
    this.metrics = metrics;
    this.thunderClient = thunderClient;
    this.config = config;
  }

  @Singleton
  @Provides
  ThunderClient provideThunderClient() {
    return thunderClient;
  }

  @Singleton
  @Provides
  MetricRegistry provideMetricRegistry() {
    return metrics;
  }

  @Singleton
  @Provides
  LightningAuthenticator provideLightningAuthenticator() {
    return new LightningAuthenticator(config);
  }
}
