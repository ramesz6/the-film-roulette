package com.gyt.thefilmroulette.exceptions;

/**
 * Custom exception class for handling errors related to the TMDB API requests.
 * Extends {@link RuntimeException} to provide a specific exception type for
 * TMDB API failures.
 */
public class MovieApiException extends RuntimeException {

  /**
   * Constructs a new MovieApiException with the specified detail message.
   *
   * @param message the detail message that explains the cause of the exception
   */
  public MovieApiException(String message) {
    super(message);
  }
}
