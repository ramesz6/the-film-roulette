package com.GyT.The_Film_Roulette.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.GyT.The_Film_Roulette.exceptions.UserNotFoundException;
import com.GyT.The_Film_Roulette.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class for application-wide beans.
 * Provides beans for user authentication and password encoding.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

  private final UserRepository userRepository;

  /**
   * Creates a {@link UserDetailsService} bean that fetches user details
   * from the database using the username.
   *
   * @return a {@link UserDetailsService} implementation.
   *
   * @throws UserNotFoundException if the user is not found in the repository.
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User not found by name"));
  }

  /**
   * Creates a {@link PasswordEncoder} bean using BCrypt hashing algorithm.
   *
   * @return a {@link BCryptPasswordEncoder} instance.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
