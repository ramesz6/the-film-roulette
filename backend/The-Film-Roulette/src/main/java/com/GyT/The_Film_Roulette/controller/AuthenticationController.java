package com.gyt.the_film_roulette.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gyt.the_film_roulette.dtos.login.LoginRequest;
import com.gyt.the_film_roulette.dtos.register.RegisterRequest;
import com.gyt.the_film_roulette.exceptions.EmailAlreadyTakenException;
import com.gyt.the_film_roulette.exceptions.InvalidCredentialsException;
import com.gyt.the_film_roulette.services.auth.AuthService;

import lombok.RequiredArgsConstructor;

/**
 * Controller for handling authentication-related operations such as user
 * registration and login.
 */
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthService authService;

  /**
   * Registers a new user.
   *
   * @param registerRequest The registration request containing user details.
   * @return ResponseEntity with a success message or an error if the email is
   *         already taken.
   */
  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody RegisterRequest registerRequest) {
    try {
      return ResponseEntity.ok(authService.register(registerRequest));
    } catch (EmailAlreadyTakenException e) {
      return ResponseEntity.badRequest().body("User already exists");
    }
  }

  /**
   * Authenticates a user and provides a token upon successful login.
   *
   * @param loginRequest The login request containing user credentials.
   *
   * @return ResponseEntity with a success message and token, or an error if
   *         credentials are invalid.
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    try {
      return ResponseEntity.ok(authService.login(loginRequest));
    } catch (InvalidCredentialsException e) {
      return ResponseEntity.badRequest().body("Invalid credentials");
    }
  }
}
