package com.gyt.thefilmroulette.services.auth;

import com.gyt.thefilmroulette.dtos.login.LoginRequest;
import com.gyt.thefilmroulette.dtos.register.RegisterRequest;

/**
 * Service interface for handling user authentication operations.
 * Includes methods for user registration and login.
 */
public interface AuthService {

  /**
   * Registers a new user in the system.
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
   * @param loginRequest The login credentials (email and password) to
   *                     authenticate the user.
   *
   * @return An object representing the result of the login process, such as an
   *         authentication token
   *         or an error message if the login fails.
   */
  Object login(LoginRequest loginRequest);
}
