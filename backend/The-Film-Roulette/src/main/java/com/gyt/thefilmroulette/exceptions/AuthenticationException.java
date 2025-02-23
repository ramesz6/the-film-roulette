package com.gyt.thefilmroulette.exceptions;

/**
 * Exception thrown when there is an error during authentication.
 * This class extends {@link RuntimeException} and is used to signal
 * authentication-related issues.
 */
public class AuthenticationException extends RuntimeException {

  /**
   * Constructs a new {@code AuthenticationException} with the specified detail
   * message.
   *
   * @param message the detail message
   */
  public AuthenticationException(String message) {
    super(message);
  }
}
