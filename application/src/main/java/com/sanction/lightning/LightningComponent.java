package com.sanction.lightning;

import com.sanction.lightning.facebook.FacebookModule;
import com.sanction.lightning.resources.FacebookResource;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {LightningModule.class, FacebookModule.class})
public interface LightningComponent {

  FacebookResource getFacebookResource();
}
