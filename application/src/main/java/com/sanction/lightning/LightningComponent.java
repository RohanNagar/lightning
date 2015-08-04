package com.sanction.lightning;

import com.sanction.lightning.resources.FacebookResource;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {LightningModule.class})
public interface LightningComponent {

  FacebookResource getFacebookResource();
}
