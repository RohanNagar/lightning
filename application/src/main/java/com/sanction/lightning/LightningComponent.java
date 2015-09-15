package com.sanction.lightning;

import com.sanction.lightning.facebook.FacebookModule;
import com.sanction.lightning.resources.FacebookResource;
import com.sanction.lightning.resources.TwitterResource;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {FacebookModule.class,
                      LightningModule.class})
public interface LightningComponent {

  FacebookResource getFacebookResource();

  TwitterResource getTwitterResource();
}
