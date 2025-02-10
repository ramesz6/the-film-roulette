package com.GyT.The_Film_Roulette.exceptions;

/**
 * Exception thrown when the provided password is invalid.
 * This class extends {@link RuntimeException} to signal that the error is
 * related
 * to password validation failure.
 */
public class PasswordIsInvalidException extends RuntimeException {

  /**
   * Constructs a new {@code PasswordIsInvalidException} with the specified detail
   * message.
   *
   * @param message the detail message
   */
  public PasswordIsInvalidException(String message) {
    super(message);
  }
}
