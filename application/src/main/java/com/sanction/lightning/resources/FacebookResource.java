package com.sanction.lightning.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/facebook")
public class FacebookResource {

  @Inject
  public FacebookResource() {

  }

  @GET
  @Path("/newsFeed")
  public Response getNewsFeed() {
    return Response.status(Response.Status.ACCEPTED).entity("Worked!").build();
  }
}
