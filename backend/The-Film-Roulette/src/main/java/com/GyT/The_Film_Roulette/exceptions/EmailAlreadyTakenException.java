package com.GyT.The_Film_Roulette.exceptions;

public class EmailAlreadyTakenException extends RuntimeException {

  public EmailAlreadyTakenException(String message) {
    super(message);
  }
}