package com.gyt.thefilmroulette.exceptions;

/**
 * Exception thrown when a user is not found in the system.
 * This class extends {@link RuntimeException} to signal that the error is
 * related
 * to a missing user during authentication or other user-related operations.
 */
public class UserNotFoundException extends RuntimeException {

  /**
   * Constructs a new {@code UserNotFoundException} with the specified detail
   * message.
   *
   * @param message the detail message
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
