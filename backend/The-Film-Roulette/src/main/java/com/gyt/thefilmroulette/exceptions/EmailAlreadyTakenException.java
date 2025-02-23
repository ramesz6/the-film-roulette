package com.gyt.thefilmroulette.exceptions;

/**
 * Exception thrown when an attempt is made to register with an email that is
 * already in use.
 * This class extends {@link RuntimeException} to signal that the error is
 * related to the
 * registration process.
 */
public class EmailAlreadyTakenException extends RuntimeException {

  /**
   * Constructs a new {@code EmailAlreadyTakenException} with the specified detail
   * message.
   *
   * @param message the detail message
   */
  public EmailAlreadyTakenException(String message) {
    super(message);
  }
}
