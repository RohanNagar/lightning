package com.sanction.lightning;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.sanction.lightning.config.ThunderConfiguration;
import com.sanction.lightning.facebook.FacebookConfiguration;
import com.sanction.lightning.resources.FacebookResource;
import com.sanction.lightning.resources.TwitterResource;
import com.sanction.lightning.twitter.TwitterConfiguration;
import io.dropwizard.Bundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LightningApplicationTest {
  private final Environment environment = mock(Environment.class);
  private final JerseyEnvironment jersey = mock(JerseyEnvironment.class);
  private final HealthCheckRegistry healthChecks = mock(HealthCheckRegistry.class);
  private final MetricRegistry metrics = mock(MetricRegistry.class);

  private final LightningConfiguration config = mock(LightningConfiguration.class);
  private final ThunderConfiguration thunderConfig = mock(ThunderConfiguration.class);
  private final FacebookConfiguration facebookConfig = mock(FacebookConfiguration.class);
  private final TwitterConfiguration twitterConfig = mock(TwitterConfiguration.class);

  private final LightningApplication application = new LightningApplication();

  @Before
  public void setup() {
    when(environment.jersey()).thenReturn(jersey);
    when(environment.healthChecks()).thenReturn(healthChecks);
    when(environment.metrics()).thenReturn(metrics);

    // LightningConfiguration fields
    when(config.getApprovedKeys()).thenReturn(new ArrayList<>());
    when(config.getThunderConfiguration()).thenReturn(thunderConfig);
    when(config.getFacebookConfiguration()).thenReturn(facebookConfig);
    when(config.getTwitterConfiguration()).thenReturn(twitterConfig);

    // ThunderConfiguration
    when(thunderConfig.getEndpoint()).thenReturn("https://endpoint");
    when(thunderConfig.getUserKey()).thenReturn("userKey");
    when(thunderConfig.getUserSecret()).thenReturn("userSecret");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testInitialize() {
    Bootstrap<LightningConfiguration> bootstrap = mock(Bootstrap.class);

    application.initialize(bootstrap);

    verify(bootstrap, times(1)).addBundle(any(Bundle.class));
  }

  @Test
  public void testRun() {
    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

    application.run(config, environment);

    // Verify register was called on jersey and healthChecks
    verify(jersey, atLeastOnce()).register(captor.capture());

    // Make sure each class that should have been registered on jersey was registered
    List<Object> values = captor.getAllValues();

    assertEquals(1, values.stream().filter(v -> v instanceof AuthDynamicFeature).count());
    assertEquals(1, values.stream().filter(v -> v instanceof FacebookResource).count());
    assertEquals(1, values.stream().filter(v -> v instanceof TwitterResource).count());
  }
}
