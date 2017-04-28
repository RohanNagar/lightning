package com.sanction.lightning.dropbox;

public class DropboxServiceFactory {
  private final String applicationKey;
  private final String applicationSecret;

  public DropboxServiceFactory(String applicationKey, String applicationSecret) {
    this.applicationKey = applicationKey;
    this.applicationSecret = applicationSecret;
  }

  public DropboxService newDropboxService() {
    return new DropboxService(applicationKey, applicationSecret);
  }
}
