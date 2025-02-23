package com.gyt.the_film_roulette.services.auth;

import com.gyt.the_film_roulette.dtos.login.LoginRequest;
import com.gyt.the_film_roulette.dtos.register.RegisterRequest;

/**
 * Service interface for handling user authentication operations.
 * <p>
 * Includes methods for user registration and login.
 * </p>
 */
public interface AuthService {

  /**
   * Registers a new user in the system.
   * 
   * <p>
   * This method takes the user registration details and processes them to create
   * a new user
   * in the system.
   * </p>
   * 
   * @param registerRequest The details required to register a new user.
   * 
   * @return An object representing the result of the registration process, such
   *         as a success message
   *         or the registered user details.
   */
  Object register(RegisterRequest registerRequest);

  /**
   * Logs in an existing user.
   * 
   * <p>
   * This method verifies the user's credentials and provides a response
   * indicating whether
   * the login was successful or not.
   * </p>
   * 
   * @param loginRequest The login credentials (username/email and password) to
   *                     authenticate the user.
   * @return An object representing the result of the login process, such as an
   *         authentication token
   *         or an error message if the login fails.
   */
  Object login(LoginRequest loginRequest);
}
