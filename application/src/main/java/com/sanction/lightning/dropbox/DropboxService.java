package com.sanction.lightning.dropbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;

public class DropboxService {
  private final DbxAppInfo appInfo;
  private final DbxRequestConfig requestConfig;

  public DropboxService(String applicationKey, String applicationSecret) {
    this.appInfo = new DbxAppInfo(applicationKey, applicationSecret);
    this.requestConfig = new DbxRequestConfig("ThePilotApp");
  }

  /**
   * Builds a URL that sends a user to a Dropbox authentication page
   * to request an OAuth access token.
   *
   * @return The URL string for the OAuth URL.
   */
  public String getOauthUrl() {
    DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);

    // TODO: add redirect URL
    return webAuth.authorize(
        DbxWebAuth.newRequestBuilder()
            .withNoRedirect()
            .build());
  }
}
