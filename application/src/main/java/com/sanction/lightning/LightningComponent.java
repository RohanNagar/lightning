package com.sanction.lightning;

import com.sanction.lightning.authentication.LightningAuthenticator;
import com.sanction.lightning.facebook.FacebookModule;
import com.sanction.lightning.resources.FacebookResource;
import com.sanction.lightning.resources.TwitterResource;
import com.sanction.lightning.twitter.TwitterModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
    FacebookModule.class,
    LightningModule.class,
    TwitterModule.class})
public interface LightningComponent {

  FacebookResource getFacebookResource();

  TwitterResource getTwitterResource();

  LightningAuthenticator getLightningAuthenticator();
}
