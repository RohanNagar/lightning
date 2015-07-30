package com.sanction.lightning;

import com.sanction.lightning.resources.FacebookResource;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component
public interface LightningComponent {

  FacebookResource getFacebookResource();
}
