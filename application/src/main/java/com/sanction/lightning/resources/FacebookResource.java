package com.sanction.lightning.resources;

import com.sanction.thunder.ThunderClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/facebook")
public class FacebookResource {
  private final ThunderClient thunderClient;

  @Inject
  public FacebookResource(ThunderClient thunderClient) {
    this.thunderClient = thunderClient;
  }

  @GET
  @Path("/newsFeed")
  public Response getNewsFeed() {
    return Response.status(Response.Status.ACCEPTED).entity("Worked!").build();
  }
}
