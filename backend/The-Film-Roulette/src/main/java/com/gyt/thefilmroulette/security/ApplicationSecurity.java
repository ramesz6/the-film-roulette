package com.gyt.thefilmroulette.security;

import com.gyt.thefilmroulette.configurations.CorsConfig;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for authentication, authorization, and CORS settings.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ApplicationSecurity {

  private final CorsConfig corsConfig;

  /** Publicly accessible paths. */
  private static final String[] ALLOW_LIST = {
      "/api/v1/auth/**",
      "/api/v1/movie/**"
  };

  /**
   * Configures security settings, disables CSRF, and permits access to allowed
   * paths.
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(req -> req.requestMatchers(ALLOW_LIST).permitAll())
        .httpBasic(Customizer.withDefaults())
        .formLogin(Customizer.withDefaults());
    return http.build();
  }

  /**
   * Configures CORS settings, allowing specified origins, methods, and headers.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.stream(corsConfig.getCorsUrls().split(",")).toList());
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
