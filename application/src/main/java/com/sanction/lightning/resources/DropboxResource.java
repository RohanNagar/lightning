package com.sanction.lightning.resources;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.dropbox.DropboxService;
import com.sanction.lightning.dropbox.DropboxServiceFactory;
import com.sanction.thunder.ThunderClient;

import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/dropbox")
@Produces(MediaType.APPLICATION_JSON)
public class DropboxResource {
  private static final Logger LOG = LoggerFactory.getLogger(DropboxResource.class);

  private final ThunderClient thunderClient;
  private final DropboxServiceFactory dropboxServiceFactory;

  // Counts number of requests
  private final Meter oauthRequests;

  /**
   * Constructs a new FacebookResource to handle Facebook HTTP requests.
   *
   * @param thunderClient Client for connecting to Thunder.
   * @param metrics The metrics object to set up meters with.
   * @param dropboxServiceFactory A factory to create new instances of DropboxService.
   */
  @Inject
  public DropboxResource(ThunderClient thunderClient, MetricRegistry metrics,
                         DropboxServiceFactory dropboxServiceFactory) {
    this.thunderClient = thunderClient;
    this.dropboxServiceFactory = dropboxServiceFactory;

    this.oauthRequests = metrics.meter(MetricRegistry.name(
        DropboxResource.class,
        "oauth-requests"));
  }

  /**
   * Retrieves an authentication URL that a new user should be
   * presented with to approve permissions.
   *
   * @param key The authentication key for the requesting application.
   * @return The URL to redirect the user to.
   */
  @GET
  @Path("/oauthUrl")
  public Response getOauthUrl(@Auth Key key) {
    oauthRequests.mark();

    DropboxService dropboxService = dropboxServiceFactory.newDropboxService();
    String url = dropboxService.getOauthUrl();

    return Response.ok(url).build();
  }
}
