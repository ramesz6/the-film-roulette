package com.gyt.the_film_roulette.exceptions;

/**
 * Exception thrown when invalid credentials are provided during authentication.
 * This class extends {@link RuntimeException} to signal that the error is
 * related
 * to authentication failure.
 */
public class InvalidCredentialsException extends RuntimeException {

  /**
   * Constructs a new {@code InvalidCredentialsException} with the specified
   * detail message.
   *
   * @param message the detail message
   */
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
