package com.sanction.lightning.resources;

import com.sanction.lightning.authentication.Key;
import com.sanction.lightning.facebook.FacebookApplicationKey;
import com.sanction.thunder.ThunderClient;

import javax.ws.rs.core.Response;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class FacebookResourceTest {
  private final ThunderClient client = mock(ThunderClient.class);
  private final FacebookApplicationKey appKey = mock(FacebookApplicationKey.class);
  private final Key key = mock(Key.class);

  private final FacebookResource resource = new FacebookResource(client, appKey);

  @Test
  public void testGetNullUsername() {
    Response response = resource.getUser(key, null);

    assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
  }

}
