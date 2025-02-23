package com.gyt.thefilmroulette.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application, setting up authentication and
 * authorization.
 * Configures basic security settings including CSRF, request authorization, and
 * login options.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApplicationSecurity {

  /**
   * A list of allowed paths that do not require authentication.
   */
  private static String[] ALLOW_LIST = { "/api/v1/auth/**", "/api/v1/movie/**" };

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * @param http the {@link HttpSecurity} instance to configure
   *
   * @return the configured {@link SecurityFilterChain}
   *
   * @throws Exception if an error occurs during the configuration
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req -> req.requestMatchers(ALLOW_LIST)
            .permitAll())
        .httpBasic(Customizer.withDefaults())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }
}
