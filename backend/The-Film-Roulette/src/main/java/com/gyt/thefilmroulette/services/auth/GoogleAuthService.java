package com.gyt.thefilmroulette.services.auth;

import com.gyt.thefilmroulette.configurations.GoogleOauthConfiguration;
import com.gyt.thefilmroulette.dtos.login.LoginResponse;
import com.gyt.thefilmroulette.exceptions.AuthenticationException;
import com.gyt.thefilmroulette.models.User;
import com.gyt.thefilmroulette.repositories.UserRepository;
import com.gyt.thefilmroulette.services.auth.jwt.JwtService;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

/**
 * Service for authenticating users via Google ID tokens.
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthService {

  private final JwtDecoder googleJwtDecoder;
  private final GoogleOauthConfiguration googleOauthConfiguration;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  /**
   * Verifies the given Google ID token and returns an application JWT.
   *
   * @param credential Google ID token string
   * @return login response containing an application JWT
   */
  public LoginResponse loginWithGoogle(String credential) {
    if (credential == null || credential.isBlank()) {
      throw new AuthenticationException("Missing Google credential");
    }

    String trimmedCredential = credential.trim();

    if (googleOauthConfiguration.getClientId() == null
        || googleOauthConfiguration.getClientId().isBlank()) {
      throw new IllegalStateException("Missing google.oauth.client-id configuration");
    }

    final Jwt jwt;
    try {
      jwt = googleJwtDecoder.decode(trimmedCredential);
    } catch (JwtException e) {
      throw new AuthenticationException("Invalid Google token");
    } catch (IllegalArgumentException e) {
      throw new AuthenticationException("Invalid Google token format");
    }

    if (jwt.getAudience() == null
        || !jwt.getAudience().contains(googleOauthConfiguration.getClientId())) {
      throw new AuthenticationException("Invalid Google token audience");
    }

    if (jwt.getIssuer() == null) {
      throw new AuthenticationException("Missing token issuer");
    }

    String issuer = jwt.getIssuer().toString();
    if (!"https://accounts.google.com".equals(issuer)
        && !"accounts.google.com".equals(issuer)) {
      throw new AuthenticationException("Invalid token issuer");
    }

    String email = jwt.getClaimAsString("email");
    Boolean emailVerified = jwt.getClaimAsBoolean("email_verified");
    if (email == null || email.isBlank()) {
      throw new AuthenticationException("Missing email claim");
    }
    if (emailVerified != null && !emailVerified) {
      throw new AuthenticationException("Email is not verified");
    }

    String name = jwt.getClaimAsString("name");
    String preferredUsername = jwt.getClaimAsString("given_name");
    String username = normalizeUsername(preferredUsername, name, email);

    User user = userRepository.findByEmail(email)
        .orElseGet(() -> {
          User newUser = Objects.requireNonNull(User.builder()
              .email(email)
              .username(username)
              // Store a random password so password login isn't accidentally usable
              // for Google-only accounts.
              .password(passwordEncoder.encode(UUID.randomUUID().toString()))
              .build(), "newUser");
          return userRepository.save(newUser);
        });

    return new LoginResponse(jwtService.generateToken(user));
  }

  private static String normalizeUsername(String preferredUsername, String name, String email) {
    String candidate = firstNonBlank(preferredUsername, name);
    if (candidate == null) {
      int at = email.indexOf('@');
      candidate = at > 0 ? email.substring(0, at) : email;
    }

    candidate = candidate.trim();
    if (candidate.isEmpty()) {
      return "user";
    }

    // Keep it simple and predictable.
    return candidate.toLowerCase(Locale.ROOT);
  }

  private static String firstNonBlank(String a, String b) {
    if (a != null && !a.isBlank()) {
      return a;
    }
    if (b != null && !b.isBlank()) {
      return b;
    }
    return null;
  }
}
