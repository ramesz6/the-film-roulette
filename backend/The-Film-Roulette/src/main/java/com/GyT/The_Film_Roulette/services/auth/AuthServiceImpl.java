package com.gyt.the_film_roulette.services.auth;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gyt.the_film_roulette.dtos.login.LoginRequest;
import com.gyt.the_film_roulette.dtos.login.LoginResponse;
import com.gyt.the_film_roulette.dtos.register.RegisterRequest;
import com.gyt.the_film_roulette.dtos.register.RegisterResponse;
import com.gyt.the_film_roulette.exceptions.AuthenticationException;
import com.gyt.the_film_roulette.exceptions.EmailAlreadyTakenException;
import com.gyt.the_film_roulette.models.User;
import com.gyt.the_film_roulette.repositories.UserRepository;
import com.gyt.the_film_roulette.services.auth.jwt.JwtService;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link AuthService} interface that handles
 * authentication-related services.
 * <p>
 * Provides methods for user registration and login, including password
 * encryption and token generation.
 * </p>
 */
@Service
@RequiredArgsConstructor
class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  /**
   * Registers a new user.
   * <p>
   * This method checks if the email is already taken. If not, it creates a new
   * user, encodes the password,
   * and stores the user in the database.
   * </p>
   * 
   * @param registerRequest The registration details of the user.
   * @return A response object containing the registration result.
   * @throws EmailAlreadyTakenException if the email is already associated with
   *                                    another user.
   */
  @Override
  public RegisterResponse register(RegisterRequest registerRequest) {

    if (userRepository.existsByEmail(registerRequest.email())) {
      throw new EmailAlreadyTakenException("E-mail is already taken.");
    }

    User newRegisterUser = User.builder()
        .username(registerRequest.username())
        .email(registerRequest.email())
        .password(passwordEncoder.encode(registerRequest.password()))
        .build();
    userRepository.save(newRegisterUser);
    return new RegisterResponse();

  }

  /**
   * Authenticates a user and generates a JWT token.
   * <p>
   * This method validates the user’s email and password, and if successful,
   * generates a token for authentication.
   * </p>
   * 
   * @param loginRequest The login credentials (email and password).
   * @return A response object containing the generated JWT token.
   * @throws AuthenticationException if the email or password is invalid.
   */
  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.email())
        .orElse(null);
    if (user == null || !BCrypt.checkpw(loginRequest.password(), user.getPassword())) {
      throw new AuthenticationException("Invalid email or password");
    }
    return new LoginResponse(jwtService.generateToken(user));
  }
}
