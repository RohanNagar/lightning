package com.sanction.lightning.exception;

import javax.ws.rs.core.Response;

public class ThunderConnectionException extends RuntimeException {
  private final Response response;

  public ThunderConnectionException(Response response) {
    this.response = response;
  }

  /**
   * Constructs a new ThunderConnectionException.
   *
   * @param message The message for the exception.
   * @param response The HTTP Response that was the result of the failed connection to Thunder.
   */
  public ThunderConnectionException(String message, Response response) {
    super(message);

    this.response = response;
  }

  /**
   * Constructs a new ThunderConnectionException.
   *
   * @param message The message for the exception.
   * @param cause The cause of the exception.
   * @param response The HTTP Response that was the result of the failed connection to Thunder.
   */
  public ThunderConnectionException(String message, Throwable cause, Response response) {
    super(message, cause);

    this.response = response;
  }

  /**
   * Constructs a new ThunderConnectionException.
   *
   * @param cause The cause of the exception.
   * @param response The HTTP Response that was the result of the failed connection to Thunder.
   */
  public ThunderConnectionException(Throwable cause, Response response) {
    super(cause);

    this.response = response;
  }

  public Response getResponse() {
    return response;
  }
}
