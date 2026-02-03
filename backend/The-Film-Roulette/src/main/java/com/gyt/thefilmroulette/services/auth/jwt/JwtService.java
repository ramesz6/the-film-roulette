package com.gyt.thefilmroulette.services.auth.jwt;

import java.util.Map;
import java.util.Objects;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface for generating JWT tokens.
 * 
 * <p>
 * This interface defines two methods for generating JWT tokens based on user
 * details,
 * either with or without additional parameters included in the token.
 * </p>
 */
public interface JwtService {

  /**
   * Generates a JWT token based on the provided user details.
   * 
   * @param userDetails The user details from which the token will be created.
   * @return The generated JWT token.
   */
  String generateToken(UserDetails userDetails);

  /**
   * Generates a JWT token based on the provided user details and additional
   * parameters.
   * 
   * @param userDetails The user details from which the token will be created.
   * @param additional  Additional parameters to be included in the token.
   * @return The generated JWT token.
   */
  String generateToken(UserDetails userDetails, Map<String, Objects> additional);

  /**
   * Extracts the token subject (principal identifier).
   *
   * @param token JWT token
   * @return subject from token
   */
  String extractSubject(String token);

  /**
   * Validates the token against the given user details.
   *
   * @param token       JWT token
   * @param userDetails expected user
   * @return true if the token is valid for this user
   */
  boolean isTokenValid(String token, UserDetails userDetails);
}
