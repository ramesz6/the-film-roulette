package com.GyT.The_Film_Roulette.exceptions;

public class PasswordIsInvalidException extends RuntimeException {
  public PasswordIsInvalidException(String message) {
    super(message);
  }
}