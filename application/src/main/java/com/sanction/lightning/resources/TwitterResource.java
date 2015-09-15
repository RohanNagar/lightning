package com.sanction.lightning.resources;

import com.sanction.lightning.authentication.Key;
import com.sanction.thunder.ThunderClient;
import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/twitter")
@Produces(MediaType.APPLICATION_JSON)
public class TwitterResource {
  private final ThunderClient thunderClient;

  @Inject
  public TwitterResource(ThunderClient thunderClient) {
    this.thunderClient = thunderClient;
  }

  /**
   * Retrieves a TwitterUser object for a given StormUser username.
   * @param key The authentication credentials of the calling application.
   * @param username The username of the StormUser to find Twitter User information for.
   * @return The TwitterUser object if successful.
   */
  @GET
  @Path("/users")
  public Response getUser(@Auth Key key, @QueryParam("username") String username) {
    if (username == null) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("'username' query parameter is required for getUser").build();
    }

    return Response.ok("Worked").build();
  }

}
