package com.GyT.The_Film_Roulette.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GyT.The_Film_Roulette.dtos.login.LoginRequest;
import com.GyT.The_Film_Roulette.dtos.register.RegisterRequest;
import com.GyT.The_Film_Roulette.exceptions.EmailAlreadyTakenException;
import com.GyT.The_Film_Roulette.exceptions.InvalidCredentialsException;
import com.GyT.The_Film_Roulette.services.auth.AuthService;

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
